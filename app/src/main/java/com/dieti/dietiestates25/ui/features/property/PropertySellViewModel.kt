package com.dieti.dietiestates25.ui.features.property

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.local.UserPreferences
import com.dieti.dietiestates25.data.remote.ImmobileCreateRequest
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.ui.utils.SessionManager
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

sealed class PropertyFormState {
    object Idle : PropertyFormState()
    object Loading : PropertyFormState()
    object Success : PropertyFormState()
    data class Error(val message: String) : PropertyFormState()
}

class PropertySellViewModel : ViewModel() {

    private val api = RetrofitClient.retrofit.create(PropertyApiService::class.java)
    private val gson = Gson()

    private val _formState = MutableStateFlow<PropertyFormState>(PropertyFormState.Idle)
    val formState = _formState.asStateFlow()

    fun submitAd(
        context: Context,
        request: ImmobileCreateRequest,
        imageUris: List<Uri>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _formState.value = PropertyFormState.Loading
            try {// --- FIX 1: RIPRISTINO SESSIONE COMPLETA ---
                // Se l'app è stata riavviata, RetrofitClient ha perso email e token.
                // Li recuperiamo dal SessionManager.

                if (RetrofitClient.loggedUserEmail == null || RetrofitClient.authToken == null) {
                    val userPrefs = UserPreferences(context)
                    val savedEmail = userPrefs.userEmail.first()
                    val savedToken = SessionManager.getAuthToken(context) // Recupera il token

                    if (savedEmail != null && savedToken != null) {
                        RetrofitClient.loggedUserEmail = savedEmail
                        RetrofitClient.authToken = savedToken // IMPORTANTE: Ripristina il token per l'Auth Header
                        Log.d("PropertyViewModel", "Sessione ripristinata. Email: $savedEmail")
                    } else {
                        _formState.value = PropertyFormState.Error("Sessione scaduta o invalida. Effettua il login.")
                        return@launch
                    }
                }

                Log.d("PropertyViewModel", "Inizio upload...")

                val jsonString = gson.toJson(request)
                val immobileBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

                val imageParts = imageUris.mapNotNull { uri ->
                    prepareCompressedFilePart(context, uri)
                }

                if (imageParts.isEmpty() && imageUris.isNotEmpty()) {
                    _formState.value = PropertyFormState.Error("Errore compressione immagini.")
                    return@launch
                }

                // Chiamata API (Ora con il Token corretto nell'Header grazie a RetrofitClient)
                api.creaImmobile(immobileBody, imageParts)

                _formState.value = PropertyFormState.Success
                onSuccess()

            } catch (e: Exception) {
                Log.e("PropertyViewModel", "Errore upload", e)

                val msg = if (e is retrofit2.HttpException) {
                    // Tenta di leggere il corpo dell'errore dal server
                    val errorBody = e.response()?.errorBody()?.string()

                    when (e.code()) {
                        401, 403 -> "Sessione scaduta. Effettua nuovamente il login."
                        413 -> "File troppo grandi anche dopo la compressione!"
                        500 -> "Errore Server: ${errorBody ?: "Errore interno sconosciuto"}"
                        else -> "Errore HTTP ${e.code()}: $errorBody"
                    }
                } else {
                    "Errore di connessione: ${e.message}"
                }
                _formState.value = PropertyFormState.Error(msg)
            }
        }
    }

    /**
     * Legge l'immagine, la ridimensiona se troppo grande e la comprime in JPEG qualità 70.
     * Versione ottimizzata per la memoria (usa inSampleSize).
     */
    private fun prepareCompressedFilePart(context: Context, uri: Uri): MultipartBody.Part? {
        return try {
            val resolver = context.contentResolver

            // 1. Decodifica solo le dimensioni (inJustDecodeBounds = true)
            // Questo è molto leggero e non carica l'immagine in memoria
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            }

            // 2. Calcola il fattore di ridimensionamento (inSampleSize)
            // Se l'immagine è 4000x3000 e vogliamo 1024x768, inSampleSize sarà 4
            val maxDimension = 1024
            var inSampleSize = 1
            if (options.outHeight > maxDimension || options.outWidth > maxDimension) {
                val halfHeight: Int = options.outHeight / 2
                val halfWidth: Int = options.outWidth / 2
                while (halfHeight / inSampleSize >= maxDimension && halfWidth / inSampleSize >= maxDimension) {
                    inSampleSize *= 2
                }
            }

            // 3. Decodifica l'immagine reale usando il fattore di scala
            val decodeOptions = BitmapFactory.Options().apply { inSampleSize = inSampleSize }
            val inputStream = resolver.openInputStream(uri) ?: return null
            val scaledBitmap = BitmapFactory.decodeStream(inputStream, null, decodeOptions)
            inputStream.close()

            if (scaledBitmap == null) {
                Log.e("PropertyViewModel", "Impossibile decodificare bitmap da: $uri")
                return null
            }

            // 4. Comprimi in JPEG al 70% su file temporaneo
            val tempFile = File.createTempFile("upload_compressed", ".jpg", context.cacheDir)
            FileOutputStream(tempFile).use { output ->
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, output)
                output.flush()
            }

            // Libera memoria
            scaledBitmap.recycle()

            Log.d("PropertyViewModel", "Immagine originale: ${options.outWidth}x${options.outHeight}, Compressa: ${tempFile.length() / 1024} KB")

            // Crea la parte Multipart per Retrofit
            val reqFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("immagini", tempFile.name, reqFile)

        } catch (e: Exception) {
            Log.e("PropertyViewModel", "Errore compressione file", e)
            null
        }
    }

    fun resetState() { _formState.value = PropertyFormState.Idle }
}
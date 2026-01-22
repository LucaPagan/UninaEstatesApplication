package com.dieti.dietiestates25.ui.features.property

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.local.UserPreferences
import com.dieti.dietiestates25.data.remote.DietiEstatesApi
import com.dieti.dietiestates25.data.remote.ImmobileCreateRequest
import com.dieti.dietiestates25.data.remote.RetrofitClient
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

    private val api: DietiEstatesApi = RetrofitClient.retrofit.create(DietiEstatesApi::class.java)
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
            try {
                // --- FIX IMPORTANTE: RECUPERO CREDENZIALI ---
                // Se la variabile statica è null (es. dopo restart app), la recuperiamo dal disco
                if (RetrofitClient.loggedUserEmail == null) {
                    val userPrefs = UserPreferences(context)
                    val savedEmail = userPrefs.userEmail.first()

                    if (savedEmail != null) {
                        RetrofitClient.loggedUserEmail = savedEmail
                        Log.d("PropertyViewModel", "Email ripristinata dalle preferenze: $savedEmail")
                    } else {
                        _formState.value = PropertyFormState.Error("Errore: Utente non loggato. Effettua nuovamente il login.")
                        return@launch
                    }
                }
                // --------------------------------------------

                Log.d("PropertyViewModel", "Inizio upload. Dati: $request")

                // Convertiamo DTO in JSON
                val jsonString = gson.toJson(request)
                val immobileBody = jsonString.toRequestBody("application/json".toMediaTypeOrNull())

                // Convertiamo Immagini
                val imageParts = imageUris.mapNotNull { uri ->
                    prepareFilePart(context, uri)
                }

                // Chiamata API
                api.creaImmobile(immobileBody, imageParts)

                Log.d("PropertyViewModel", "Upload completato con successo")
                _formState.value = PropertyFormState.Success
                onSuccess()

            } catch (e: Exception) {
                Log.e("PropertyViewModel", "Errore invio immobile", e)
                // Mostriamo l'errore specifico se è un 403
                val errorMsg = if (e is retrofit2.HttpException && e.code() == 403) {
                    "Sessione scaduta o non valida (403). Riprova a fare login."
                } else {
                    "Errore durante la pubblicazione: ${e.message}"
                }
                _formState.value = PropertyFormState.Error(errorMsg)
            }
        }
    }

    private fun prepareFilePart(context: Context, uri: Uri): MultipartBody.Part? {
        return try {
            val contentResolver = context.contentResolver
            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "jpg"

            val file = File.createTempFile("upload_${System.currentTimeMillis()}", ".$extension", context.cacheDir)

            contentResolver.openInputStream(uri)?.use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }

            val requestFile = file.asRequestBody(mimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData("immagini", file.name, requestFile)
        } catch (e: Exception) {
            Log.e("PropertyViewModel", "Errore conversione file immagine", e)
            null
        }
    }

    fun resetState() {
        _formState.value = PropertyFormState.Idle
    }
}
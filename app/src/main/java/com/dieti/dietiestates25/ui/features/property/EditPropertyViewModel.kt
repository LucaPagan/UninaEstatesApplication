package com.dieti.dietiestates25.ui.features.property

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.local.UserPreferences
import com.dieti.dietiestates25.data.remote.AmbienteDto
import com.dieti.dietiestates25.data.remote.ImmobileCreateRequest
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.ui.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

// MODIFICA: Usiamo una data class per mantenere i dati visibili durante il caricamento
data class EditUiState(
    val immobile: ImmobileDTO? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccessOperation: Boolean = false // True solo se dobbiamo chiudere la schermata (es. delete o save totale)
)

class EditPropertyViewModel(application: Application) : AndroidViewModel(application) {

    private val api = RetrofitClient.retrofit.create(PropertyApiService::class.java)
    private val userPrefs = UserPreferences(application.applicationContext)

    private val _uiState = MutableStateFlow(EditUiState(isLoading = true))
    val uiState: StateFlow<EditUiState> = _uiState.asStateFlow()

    fun loadProperty(id: String) {
        viewModelScope.launch {
            try {
                // Manteniamo i dati vecchi se ci sono, attiviamo solo il loading
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val result = api.getImmobileById(id)

                _uiState.value = _uiState.value.copy(
                    immobile = result,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Errore caricamento: ${e.message}"
                )
            }
        }
    }

    fun updateProperty(
        id: String,
        originalDto: ImmobileDTO,
        tipoVendita: Boolean,
        categoria: String?,
        indirizzo: String?,
        localita: String?,
        mq: Int?,
        piano: Int?,
        ascensore: Boolean?,
        arredamento: String?,
        climatizzazione: Boolean?,
        esposizione: String?,
        statoProprieta: String?,
        annoCostruzione: String?,
        prezzo: Int?,
        speseCondominiali: Int?,
        descrizione: String?,
        ambienti: List<AmbienteDto>
    ) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)

                checkAndRestoreSession()

                val request = ImmobileCreateRequest(
                    tipoVendita = tipoVendita,
                    categoria = categoria,
                    indirizzo = indirizzo,
                    localita = localita,
                    mq = mq,
                    piano = piano,
                    ascensore = ascensore,
                    arredamento = arredamento,
                    climatizzazione = climatizzazione,
                    esposizione = esposizione,
                    statoProprieta = statoProprieta,
                    annoCostruzione = annoCostruzione,
                    prezzo = prezzo,
                    speseCondominiali = speseCondominiali,
                    descrizione = descrizione,
                    ambienti = ambienti
                )

                val response = api.updateImmobile(id, request)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isLoading = false, isSuccessOperation = true)
                } else {
                    val errorBody = response.errorBody()?.string()
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Errore salvataggio: ${response.code()} - $errorBody")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Errore rete: ${e.message}")
            }
        }
    }

    fun deleteProperty(id: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                checkAndRestoreSession()
                val response = api.deleteImmobile(id)
                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isLoading = false, isSuccessOperation = true)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Errore cancellazione: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Errore rete: ${e.message}")
            }
        }
    }

    // --- GESTIONE IMMAGINI ---

    fun uploadImages(immobileId: String, imageUris: List<Uri>) {
        viewModelScope.launch {
            try {
                // Attiva loading ma NON resetta i dati, così la UI resta visibile
                _uiState.value = _uiState.value.copy(isLoading = true)
                val context = getApplication<Application>().applicationContext

                if (!checkAndRestoreSession()) {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Sessione scaduta. Impossibile caricare immagini.")
                    return@launch
                }

                val imageParts = imageUris.mapNotNull { uri ->
                    prepareCompressedFilePart(context, uri)
                }

                if (imageParts.isNotEmpty()) {
                    val response = api.aggiungiImmagini(immobileId, imageParts)
                    if (response.isSuccessful) {
                        // Ricarica per aggiornare le foto, mantenendo il form attivo
                        loadProperty(immobileId)
                    } else {
                        val errorMsg = response.errorBody()?.string() ?: "Errore sconosciuto"
                        _uiState.value = _uiState.value.copy(isLoading = false, error = "Errore Server: $errorMsg")
                    }
                } else {
                    // Nessuna immagine valida, togliamo solo il loading
                    _uiState.value = _uiState.value.copy(isLoading = false)
                }
            } catch (e: Exception) {
                Log.e("EditViewModel", "Errore upload", e)
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Errore upload: ${e.message}")
            }
        }
    }

    fun deleteImage(immobileId: String, imageId: Int) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                checkAndRestoreSession()

                val response = api.eliminaImmagine(imageId)
                if (response.isSuccessful) {
                    loadProperty(immobileId)
                } else {
                    val errorMsg = response.errorBody()?.string()
                    _uiState.value = _uiState.value.copy(isLoading = false, error = "Errore eliminazione: ${response.code()} - $errorMsg")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = "Errore eliminazione: ${e.message}")
            }
        }
    }

    // --- HELPERS ---

    private suspend fun checkAndRestoreSession(): Boolean {
        if (RetrofitClient.loggedUserEmail == null || RetrofitClient.authToken == null) {
            val context = getApplication<Application>().applicationContext
            val savedEmail = userPrefs.userEmail.first()
            val savedToken = SessionManager.getAuthToken(context)

            if (savedEmail != null && savedToken != null) {
                RetrofitClient.loggedUserEmail = savedEmail
                RetrofitClient.authToken = savedToken
                return true
            } else {
                return false
            }
        }
        return true
    }

    private fun prepareCompressedFilePart(context: Context, uri: Uri): MultipartBody.Part? {
        return try {
            val resolver = context.contentResolver
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            resolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, options)
            }

            val maxDimension = 1024
            var inSampleSize = 1
            if (options.outHeight > maxDimension || options.outWidth > maxDimension) {
                val halfHeight: Int = options.outHeight / 2
                val halfWidth: Int = options.outWidth / 2
                while (halfHeight / inSampleSize >= maxDimension && halfWidth / inSampleSize >= maxDimension) {
                    inSampleSize *= 2
                }
            }

            val decodeOptions = BitmapFactory.Options().apply { inSampleSize = inSampleSize }
            val inputStream = resolver.openInputStream(uri) ?: return null
            val scaledBitmap = BitmapFactory.decodeStream(inputStream, null, decodeOptions)
            inputStream.close()

            if (scaledBitmap == null) return null

            val tempFile = File.createTempFile("upload_add_${System.currentTimeMillis()}", ".jpg", context.cacheDir)
            FileOutputStream(tempFile).use { output ->
                scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, output)
                output.flush()
            }
            scaledBitmap.recycle()

            val reqFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("immagini", tempFile.name, reqFile)

        } catch (e: Exception) {
            Log.e("EditViewModel", "Errore compressione file", e)
            null
        }
    }
}
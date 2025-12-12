package com.dieti.dietiestates25.ui.features.property

import android.content.Context
import android.net.Uri
import com.dieti.dietiestates25.ui.utils.Utils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.ImmobileCreateRequest
import com.dieti.dietiestates25.data.remote.ImmobileDetailDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class PropertyViewModel : ViewModel() {
    private val _selectedProperty = MutableStateFlow<ImmobileDetailDTO?>(null)
    val selectedProperty = _selectedProperty.asStateFlow()

    private val _uploadState = MutableStateFlow<String>("")
    val uploadState = _uploadState.asStateFlow()

    fun getPropertyDetail(id: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.instance.getImmobileDetail(id)
                if (response.isSuccessful) {
                    _selectedProperty.value = response.body()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun createProperty(request: ImmobileCreateRequest, imageUris: List<Uri>, context: Context) {
        viewModelScope.launch {
            _uploadState.value = "Caricamento in corso..."
            try {
                // 1. Crea Immobile
                val response = RetrofitClient.instance.createImmobile(request)
                if (response.isSuccessful && response.body() != null) {
                    val immobileId = response.body()!!["uuid"] ?: return@launch
                    
                    // 2. Carica Immagini una ad una
                    imageUris.forEach { uri ->
                        uploadImage(immobileId, uri, context)
                    }
                    _uploadState.value = "Successo!"
                } else {
                    _uploadState.value = "Errore creazione immobile"
                }
            } catch (e: Exception) {
                _uploadState.value = "Errore: ${e.message}"
            }
        }
    }

    private suspend fun uploadImage(immobileId: String, uri: Uri, context: Context) {
        val file = Utils.getFileFromUri(context, uri) ?: return
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        RetrofitClient.instance.uploadImmagine(immobileId, body)
    }
}
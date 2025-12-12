package com.dieti.dietiestates25.ui.features.property

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.remote.ImmobileCreateRequest
import com.dieti.dietiestates25.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import com.dieti.dietiestates25.ui.utils.Utils

class PropertySellViewModel : ViewModel() {

    private val _uploadState = MutableStateFlow<String>("")
    val uploadState = _uploadState.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun createProperty(request: ImmobileCreateRequest, imageUris: List<Uri>, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
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
                    _uploadState.value = "Errore creazione immobile: ${response.code()}"
                }
            } catch (e: Exception) {
                _uploadState.value = "Errore: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun uploadImage(immobileId: String, uri: Uri, context: Context) {
        try {
            val file = Utils.getFileFromUri(context, uri) ?: return
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            RetrofitClient.instance.uploadImmagine(immobileId, body)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
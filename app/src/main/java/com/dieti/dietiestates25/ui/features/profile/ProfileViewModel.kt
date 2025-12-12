package com.dieti.dietiestates25.ui.features.profile

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.data.model.ProfileData
import com.dieti.dietiestates25.data.model.modelsource.CommonPhonePrefixes
import com.dieti.dietiestates25.data.model.modelsource.DefaultPhonePrefix
import com.dieti.dietiestates25.data.model.modelsource.PhonePrefix
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.data.remote.UserUpdateRequest
import com.dieti.dietiestates25.ui.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private var _initialProfileDataOnEdit: ProfileData = ProfileData()

    private val _currentProfileData = MutableStateFlow(ProfileData())
    val currentProfileData: StateFlow<ProfileData> = _currentProfileData.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    val availablePhonePrefixes: List<PhonePrefix> = CommonPhonePrefixes

    private val _showExitEditModeConfirmDialog = MutableStateFlow(false)
    val showExitEditModeConfirmDialog: StateFlow<Boolean> =
        _showExitEditModeConfirmDialog.asStateFlow()

    private val _showLogoutConfirmDialog = MutableStateFlow(false)
    val showLogoutConfirmDialog: StateFlow<Boolean> = _showLogoutConfirmDialog.asStateFlow()

    private val _showDeleteConfirmDialog = MutableStateFlow(false)
    val showDeleteConfirmDialog: StateFlow<Boolean> = _showDeleteConfirmDialog.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Controllo modifiche non salvate
    val hasUnsavedChanges: StateFlow<Boolean> =
        isEditMode.combine(_currentProfileData) { editMode, currentData ->
            editMode && (currentData != _initialProfileDataOnEdit)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Validazione base
    val canSaveChanges: StateFlow<Boolean> =
        isEditMode.combine(_currentProfileData) { editMode, data ->
            editMode && data.email.isNotBlank() // Rimosso check sul nome
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // --- CARICAMENTO DATI DAL BACKEND ---
    fun loadProfile(context: Context) {
        val userId = SessionManager.getUserId(context)
        if (userId == null) {
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.instance.getUserProfile(userId)
                if (response.isSuccessful && response.body() != null) {
                    val dto = response.body()!!

                    val (prefix, number) = parsePhoneNumber(dto.telefono)

                    val loadedData = ProfileData(
                        // name = "${dto.nome} ${dto.cognome}", // Nome rimosso dalla UI e dal modello se non necessario per l'edit
                        name = "", // Lasciamo vuoto o rimuoviamo il campo da ProfileData se possibile
                        email = dto.email,
                        selectedPrefix = prefix,
                        phoneNumberWithoutPrefix = number
                    )

                    _currentProfileData.value = loadedData
                    _initialProfileDataOnEdit = loadedData
                } else {
                    Log.e("ProfileViewModel", "Errore caricamento profilo: ${response.code()}")
                    Toast.makeText(context, "Impossibile caricare il profilo", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Eccezione caricamento profilo", e)
                Toast.makeText(context, "Errore di connessione", Toast.LENGTH_SHORT).show()
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun parsePhoneNumber(fullNumber: String?): Pair<PhonePrefix, String> {
        if (fullNumber.isNullOrBlank()) return Pair(DefaultPhonePrefix, "")
        val matchedPrefix = availablePhonePrefixes.find { fullNumber.startsWith(it.prefix) }
        return if (matchedPrefix != null) {
            val numberPart = fullNumber.removePrefix(matchedPrefix.prefix).trim()
            Pair(matchedPrefix, numberPart)
        } else {
            Pair(DefaultPhonePrefix, fullNumber)
        }
    }

    // --- AGGIORNAMENTO CAMPI UI ---
    // Funzione onNameChange RIMOSSA

    fun onEmailChange(newEmail: String) {
        if (_isEditMode.value) {
            _currentProfileData.value = _currentProfileData.value.copy(email = newEmail)
        }
    }

    fun onPhonePrefixChange(newPrefix: PhonePrefix) {
        if (_isEditMode.value) {
            _currentProfileData.value = _currentProfileData.value.copy(selectedPrefix = newPrefix)
        }
    }

    fun onPhoneNumberWithoutPrefixChange(newNumber: String) {
        if (_isEditMode.value) {
            _currentProfileData.value =
                _currentProfileData.value.copy(phoneNumberWithoutPrefix = newNumber)
        }
    }

    // --- LOGICA MODALITÀ EDIT ---
    fun attemptToggleEditMode() {
        if (_isEditMode.value) {
            if (hasUnsavedChanges.value) {
                _showExitEditModeConfirmDialog.value = true
            } else {
                exitEditModeGracefully()
            }
        } else {
            enterEditMode()
        }
    }

    private fun enterEditMode() {
        _initialProfileDataOnEdit = _currentProfileData.value
        _isEditMode.value = true
    }

    private fun exitEditModeGracefully() {
        _isEditMode.value = false
    }

    fun confirmExitEditModeAndSave(context: Context) {
        if (canSaveChanges.value) {
            saveChanges(context)
        }
        closeExitEditModeConfirmDialog()
    }

    fun confirmExitEditModeAndSave() {}

    fun confirmExitEditModeWithoutSaving() {
        revertChanges()
        exitEditModeGracefully()
        closeExitEditModeConfirmDialog()
    }

    fun closeExitEditModeConfirmDialog() {
        _showExitEditModeConfirmDialog.value = false
    }

    // --- SALVATAGGIO SUL BACKEND ---
    fun saveChanges(context: Context? = null) {
        if (!canSaveChanges.value) return

        val userId = context?.let { SessionManager.getUserId(it) } ?: return
        val fullPhoneNumber = _currentProfileData.value.fullPhoneNumber
        val updatedEmail = _currentProfileData.value.email

        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Includiamo anche l'email nella richiesta
                val request = UserUpdateRequest(
                    email = updatedEmail,
                    telefono = fullPhoneNumber,
                    password = null
                )

                val response = RetrofitClient.instance.updateUserProfile(userId, request)

                if (response.isSuccessful) {
                    Toast.makeText(context, "Profilo aggiornato con successo", Toast.LENGTH_SHORT).show()
                    _initialProfileDataOnEdit = _currentProfileData.value
                    exitEditModeGracefully()
                } else {
                    Toast.makeText(context, "Errore aggiornamento: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Errore salvataggio", e)
                Toast.makeText(context, "Errore di connessione durante il salvataggio", Toast.LENGTH_SHORT).show()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveChanges() {}

    private fun revertChanges() {
        _currentProfileData.value = _initialProfileDataOnEdit
    }

    // --- DIALOGS ---
    fun triggerLogoutDialog() {
        _showLogoutConfirmDialog.value = true
    }

    fun confirmLogout(saveFirst: Boolean, context: Context) {
        if (saveFirst && hasUnsavedChanges.value) {
            if (canSaveChanges.value) {
                saveChanges(context)
            }
        }
        SessionManager.logout(context)
    }

    fun confirmLogout(saveFirst: Boolean) {}

    fun cancelLogoutDialog() {
        _showLogoutConfirmDialog.value = false
    }

    fun triggerDeleteProfileDialog() {
        _showDeleteConfirmDialog.value = true
    }

    fun cancelDeleteProfileDialog() {
        _showDeleteConfirmDialog.value = false
    }

    fun deleteProfile() {
        println("Richiesta eliminazione profilo")
        cancelDeleteProfileDialog()
        exitEditModeGracefully()
    }

    fun triggerExitEditModeDialog() {
        _showExitEditModeConfirmDialog.value = true
    }
}
package com.dieti.dietiestates25.ui.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import com.dieti.dietiestates25.ui.model.modelsource.PhonePrefix
import com.dieti.dietiestates25.ui.model.modelsource.CommonPhonePrefixes
import com.dieti.dietiestates25.ui.model.modelsource.DefaultPhonePrefix


data class ProfileData(
    val name: String = "Lorenzo",
    val email: String = "LorenzoTrignano@gmail.com",
    val selectedPrefix: PhonePrefix = DefaultPhonePrefix,
    val phoneNumberWithoutPrefix: String = "123456789"
) {
    val fullPhoneNumber: String
        get() = "${selectedPrefix.prefix}${phoneNumberWithoutPrefix}"
}

class ProfileViewModel : ViewModel() {
    private var _initialProfileDataOnEdit: ProfileData = ProfileData()

    private val _currentProfileData = MutableStateFlow(ProfileData())
    val currentProfileData: StateFlow<ProfileData> = _currentProfileData.asStateFlow()

    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode.asStateFlow()

    val availablePhonePrefixes: List<PhonePrefix> = CommonPhonePrefixes

    private val _showExitEditModeConfirmDialog = MutableStateFlow(false)
    val showExitEditModeConfirmDialog: StateFlow<Boolean> = _showExitEditModeConfirmDialog.asStateFlow()

    private val _showLogoutConfirmDialog = MutableStateFlow(false)
    val showLogoutConfirmDialog: StateFlow<Boolean> = _showLogoutConfirmDialog.asStateFlow()

    // *** NUOVO STATO PER IL DIALOGO DI CONFERMA ELIMINAZIONE ***
    private val _showDeleteConfirmDialog = MutableStateFlow(false)
    val showDeleteConfirmDialog: StateFlow<Boolean> = _showDeleteConfirmDialog.asStateFlow()

    val hasUnsavedChanges: StateFlow<Boolean> =
        isEditMode.combine(_currentProfileData) { editMode, currentData ->
            editMode && (currentData != _initialProfileDataOnEdit)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val canSaveChanges: StateFlow<Boolean> =
        isEditMode.combine(_currentProfileData) { editMode, data ->
            editMode && data.name.isNotBlank() && data.email.isNotBlank() && data.phoneNumberWithoutPrefix.isNotBlank()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    init {
        val loadedData = ProfileData()
        _initialProfileDataOnEdit = loadedData
        _currentProfileData.value = loadedData
    }

    fun onNameChange(newName: String) {
        if (_isEditMode.value) {
            _currentProfileData.value = _currentProfileData.value.copy(name = newName)
        }
    }

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
            _currentProfileData.value = _currentProfileData.value.copy(phoneNumberWithoutPrefix = newNumber)
        }
    }

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

    fun confirmExitEditModeAndSave() {
        if (canSaveChanges.value) {
            saveChanges()
        }
        closeExitEditModeConfirmDialog()
    }

    fun confirmExitEditModeWithoutSaving() {
        revertChanges()
        exitEditModeGracefully()
        closeExitEditModeConfirmDialog()
    }

    fun closeExitEditModeConfirmDialog() {
        _showExitEditModeConfirmDialog.value = false
    }

    fun saveChanges() {
        if (!canSaveChanges.value) return
        println("Salvataggio modifiche: ${_currentProfileData.value.name}, ${_currentProfileData.value.email}, ${_currentProfileData.value.fullPhoneNumber}")
        _initialProfileDataOnEdit = _currentProfileData.value
        exitEditModeGracefully()
    }

    private fun revertChanges() {
        _currentProfileData.value = _initialProfileDataOnEdit
    }

    fun triggerLogoutDialog() {
        _showLogoutConfirmDialog.value = true
    }

    fun confirmLogout(saveFirst: Boolean) {
        if (saveFirst && hasUnsavedChanges.value) {
            if (canSaveChanges.value) {
                saveChanges()
            }
        } else if (!saveFirst && hasUnsavedChanges.value) {
            revertChanges()
        }
        println("Logout eseguito")
        //closeLogoutConfirmDialog()
        exitEditModeGracefully()
    }

    fun cancelLogoutDialog() {
        _showLogoutConfirmDialog.value = false
    }

    fun triggerDeleteProfileDialog() {
        _showDeleteConfirmDialog.value = true
    }

    fun cancelDeleteProfileDialog() {
        _showDeleteConfirmDialog.value = false
    }

    fun deleteProfile() { // Questa funzione ora sarà chiamata DOPO la conferma
        println("Profilo eliminato definitivamente: ID Utente (es. ${_currentProfileData.value.email})")
        // Qui dovresti implementare la logica di eliminazione effettiva
        // (chiamata API, pulizia dati locali, navigazione alla schermata di login, ecc.)
        cancelDeleteProfileDialog() // Chiudi il dialogo dopo l'azione
        // Potrebbe essere necessario anche uscire dalla modalità modifica e resettare lo stato
        exitEditModeGracefully()
        // TODO: Eseguire il logout effettivo o navigare via
    }

    fun triggerExitEditModeDialog() {
        _showExitEditModeConfirmDialog.value = true
    }
}
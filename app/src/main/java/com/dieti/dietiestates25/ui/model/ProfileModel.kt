package com.dieti.dietiestates25.ui.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import androidx.lifecycle.viewModelScope
import com.dieti.dietiestates25.ui.model.modelsource.CommonPhonePrefixes
import com.dieti.dietiestates25.ui.model.modelsource.DefaultPhonePrefix
import com.dieti.dietiestates25.ui.model.modelsource.PhonePrefix
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine

data class ProfileData(
    val name: String = "Lorenzo",
    val email: String = "LorenzoTrignano@gmail.com",
    val selectedPrefix: PhonePrefix = DefaultPhonePrefix, // Prefisso selezionato
    val phoneNumberWithoutPrefix: String = "123456789" // Numero senza prefisso
) {
    val fullPhoneNumber: String
        get() = "${selectedPrefix.prefix}${phoneNumberWithoutPrefix}"
}

class ProfileViewModel : ViewModel() {
    // Dati iniziali come erano al momento dell'ingresso in modalità modifica o al caricamento
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

    val hasUnsavedChanges: StateFlow<Boolean> =
        isEditMode.combine(_currentProfileData) { editMode, currentData ->
            editMode && (currentData != _initialProfileDataOnEdit)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // Stato per validazione campi
    val canSaveChanges: StateFlow<Boolean> =
        isEditMode.combine(_currentProfileData) { editMode, data ->
            editMode && data.name.isNotBlank() && data.email.isNotBlank() && data.phoneNumberWithoutPrefix.isNotBlank()
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)


    init {
        val loadedData = ProfileData() // In un'app reale, caricheresti da db/rete
        _initialProfileDataOnEdit = loadedData // Inizializza anche questo
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
        if (canSaveChanges.value) { // Controlla se si può salvare
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
        if (!canSaveChanges.value) return // Non salvare se i campi non sono validi

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
            } else {
                // Potresti voler mostrare un messaggio che non è possibile salvare e uscire
                // Per ora, non salviamo se i campi non sono validi e si è scelto di salvare.
                // O, alternativamente, impedire il logout se il salvataggio fallisce.
                // Semplifichiamo: se canSaveChanges è false, saveChanges non farà nulla.
                saveChanges() // Tenterà di salvare, ma non lo farà se i campi sono invalidi
            }
        } else if (!saveFirst && hasUnsavedChanges.value) {
            revertChanges()
        }
        println("Logout eseguito")
        // closeLogoutConfirmDialog()
        exitEditModeGracefully()
    }

    fun cancelLogoutDialog() {
        _showLogoutConfirmDialog.value = false
    }

    fun deleteProfile() {
        println("Profilo eliminato")
    }
}
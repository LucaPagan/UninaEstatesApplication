package com.dieti.dietiestates25.data.remote

import java.time.LocalDate

// --- IMMOBILI ---

data class ImmobileDTO(
    val id: String,
    val tipoVendita: Boolean,
    val categoria: String?,
    val indirizzo: String?,
    val localita: String? = null,
    val prezzo: Int?,
    val mq: Int?,
    val descrizione: String?,
    val annoCostruzione: String?,
    val immagini: List<ImmagineDto> = emptyList(),
    val ambienti: List<AmbienteDto> = emptyList(),
    val lat: Double? = null,
    val long: Double? = null,
    val parco: Boolean = false,
    val scuola: Boolean = false,
    val servizioPubblico: Boolean = false
)

data class ImmagineDto(
    val id: Int,
    val url: String
)

data class AmbienteDto(
    val tipologia: String,
    val numero: Int
)

// DTO Semplificato per i Preferiti
data class ImmobileSummaryDTO(
    val id: String,
    val titolo: String?,
    val prezzo: Int?,
    val indirizzo: String?,
    val urlImmagine: String?
)

// DTO per la richiesta creazione
data class ImmobileCreateRequest(
    val tipoVendita: Boolean,
    val categoria: String?,
    val indirizzo: String?,
    val localita: String?,
    val mq: Int?,
    val piano: Int?,
    val ascensore: Boolean?,
    val arredamento: String?,
    val climatizzazione: Boolean?,
    val esposizione: String?,
    val statoProprieta: String?,
    val annoCostruzione: String?,
    val prezzo: Int?,
    val speseCondominiali: Int?,
    val descrizione: String?,
    val ambienti: List<AmbienteDto> = emptyList()
)

data class ImmobileDetailDTO(
    val id: String,
    val proprietarioNome: String,
    val proprietarioId: String,
    val tipoVendita: Boolean,
    val categoria: String?,
    val tipologia: String?,
    val localita: String?,
    val mq: Int?,
    val piano: Int?,
    val ascensore: Boolean?,
    val dettagli: String?,
    val arredamento: String?,
    val climatizzazione: Boolean?,
    val esposizione: String?,
    val tipoProprieta: String?,
    val statoProprieta: String?,
    val annoCostruzione: String?,
    val prezzo: Int?,
    val speseCondominiali: Int?,
    val disponibilita: Boolean,
    val descrizione: String?,
    val immaginiIds: List<Int>
)

// --- UTENTI ---

data class UtenteRegistrazioneRequest(
    val nome: String,
    val cognome: String,
    val email: String,
    val password: String,
    val telefono: String?
)

// --- MODIFICA QUI: Aggiunto agenziaNome ---
data class UtenteResponseDTO(
    val id: String,
    val nome: String,
    val cognome: String,
    val email: String,
    val telefono: String?,
    val ruolo: String,
    val preferiti: List<ImmobileSummaryDTO> = emptyList(),
    // Nuovo campo per gestire i dati del Manager
    val agenziaNome: String? = null
)

data class UserUpdateRequest(
    val email: String?,
    val telefono: String?,
    val password: String?
)

// --- APPUNTAMENTI ---

data class AppuntamentoRequest(
    val utenteId: String,
    val immobileId: String,
    val agenteId: String,
    val data: String,
    val orario: String
)

data class ProposalResponseRequest(
    val accettata: Boolean
)

data class AppuntamentoDTO(
    val id: String,
    val utenteId: String?, // Aggiunto nullable per sicurezza
    val data: String,
    val ora: String,
    val stato: String,
    val immobileId: String?,
    val titoloImmobile: String?
)

// --- OFFERTE ---

data class OffertaRequest(
    val utenteId: String,
    val immobileId: String,
    val importo: Int,
    val corpo: String? = null
)

data class OffertaDTO(
    val id: String,
    val immobileTitolo: String,
    val importo: Int,
    val data: String
)

// --- NOTIFICHE ---

data class NotificationDTO(
    val id: String,
    val titolo: String,
    val corpo: String?,
    val data: String,
    val letto: Boolean,
    val tipo: String = "INFO"
)

data class NotificationDetailDTO(
    val id: String,
    val titolo: String,
    val corpo: String?,
    val data: String,
    val letto: Boolean,
    val tipo: String,
    val mittenteNome: String?,
    val mittenteTipo: String?,
    val isProposta: Boolean = false,
    val immobileId: String? = null,
    val prezzoProposto: Double? = null
)

// --- AUTH ---

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val nome: String,
    val cognome: String,
    val email: String,
    val password: String,
    val telefono: String?
)

data class GoogleLoginRequest(
    val email: String,
    val firstName: String?,
    val lastName: String?,
    val idToken: String? = null
)

data class AuthResponse(
    val token: String,
    val userId: String,
    val nome: String,
    val email: String,
    val ruolo: String
)

// --- AGENZIE E AGENTI ---

data class AgenziaDTO(
    val nome: String
)

data class AgenteDTO(
    val id: String,
    val nome: String,
    val cognome: String,
    val email: String,
    val agenziaNome: String
)
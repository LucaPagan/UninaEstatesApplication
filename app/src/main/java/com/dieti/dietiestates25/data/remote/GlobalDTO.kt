package com.dieti.dietiestates25.data.remote

// --- IMMOBILI ---
data class ImmobileDTO(
    val id: String,
    val titolo: String,
    val prezzo: Int,
    val tipologia: String?,
    val localita: String?,
    val mq: Int?,
    val descrizione: String?,
    val coverImageId: Int?,
    val isVendita: Boolean,
    val proprietarioId: String
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
    val annoCostruzione: String?, // Arriverà come "YYYY-MM-DD"
    val prezzo: Int?,
    val speseCondominiali: Int?,
    val disponibilita: Boolean,
    val descrizione: String?,
    val immaginiIds: List<Int>
)

data class ImmobileCreateRequest(
    val proprietarioId: String,
    val tipoVendita: Boolean,
    val categoria: String,
    val tipologia: String,
    val localita: String,
    val mq: Int,
    val piano: Int,
    val ascensore: Boolean,
    val dettagli: String,
    val arredamento: String,
    val climatizzazione: Boolean,
    val esposizione: String,
    val tipoProprieta: String,
    val statoProprieta: String,
    val prezzo: Int,
    val speseCondominiali: Int,
    val descrizione: String
)

// --- UTENTI ---
data class UserProfileDTO(
    val id: String,
    val nome: String,
    val cognome: String,
    val email: String,
    val telefono: String?,
    val bio: String? = null
)

data class UserUpdateRequest(
    val telefono: String?,
    val password: String?
)

// --- APPUNTAMENTI ---
data class AppuntamentoRequest(
    val utenteId: String,
    val immobileId: String,
    val agenteId: String,
    val data: String, // Formato "YYYY-MM-DD"
    val orario: String // Formato "HH:mm"
)

data class AppuntamentoDTO(
    val id: String,
    val immobileTitolo: String,
    val data: String,
    val orario: String
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
data class NotificaDTO(
    val id: String,
    val titolo: String,
    val corpo: String?,
    val data: String,
    val letto: Boolean
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
package com.dieti.dietiestates25.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class NotificaDTO(
    val id: String,
    val titolo: String,
    val corpo: String,
    val data: String,
    val letto: Boolean
)

data class FcmTokenRequest(val token: String)

data class NotificationPreferencesRequest(
    val notifTrattative: Boolean,
    val notifPubblicazione: Boolean,
    val notifNuoviImmobili: Boolean
)

// DTO Aggiornato con i campi per il dettaglio manager
data class TrattativaSummaryDTO(
    val offertaId: String,
    val immobileId: String, // NUOVO
    val immobileTitolo: String,
    val immobileIndirizzo: String?,
    val prezzoOfferto: Int, // NUOVO
    val nomeOfferente: String, // NUOVO
    val ultimoStato: String,
    val ultimaModifica: String,
    val immagineUrl: String?
)

data class StoriaTrattativaDTO(
    val offertaId: String,
    val prezzoIniziale: Int,
    val immobileTitolo: String,
    val cronologia: List<MessaggioTrattativaDTO>,
    val canUserReply: Boolean
)

data class MessaggioTrattativaDTO(
    val autoreNome: String,
    val isMe: Boolean,
    val testo: String,
    val prezzo: Int?,
    val tipo: String,
    val data: String
)

data class UserResponseRequest(
    val offertaId: String,
    val utenteId: String,
    val esito: String,
    val nuovoPrezzo: Int? = null,
    val messaggio: String? = null
)

interface NotificationApiService {

    @POST("api/notifications/token")
    suspend fun updateFcmToken(@Body request: FcmTokenRequest): Response<Unit>

    @PUT("api/notifications/preferences")
    suspend fun updatePreferences(@Body request: NotificationPreferencesRequest): Response<Unit>
    @GET("api/notifiche/utente/{userId}")
    suspend fun getNotificheUtente(@Path("userId") userId: String): Response<List<NotificaDTO>>

    @GET("api/trattative/utente/{userId}")
    suspend fun getTrattativeUtente(@Path("userId") userId: String): Response<List<TrattativaSummaryDTO>>

    @GET("api/trattative/{offertaId}/storia")
    suspend fun getStoriaTrattativa(
        @Path("offertaId") offertaId: String,
        @Query("viewerId") viewerId: String
    ): Response<StoriaTrattativaDTO>

    @POST("api/trattative/utente/rispondi")
    suspend fun inviaRispostaUtente(@Body request: UserResponseRequest): Response<Unit>
}
package com.dieti.dietiestates25.data.remote

import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface UninaAPI {

    // --- AUTH ---
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    // --- IMMOBILI ---
    @GET("api/immobili")
    suspend fun getAllImmobili(
        @Query("localita") localita: String? = null,
        @Query("tipologia") tipologia: String? = null,
        @Query("prezzoMax") prezzoMax: Int? = null
    ): List<ImmobileDTO>

    @GET("api/immobili/{id}")
    suspend fun getImmobileDetail(@Path("id") id: String): Response<ImmobileDetailDTO>

    @POST("api/immobili")
    suspend fun createImmobile(@Body request: ImmobileCreateRequest): Response<Map<String, String>> // Torna UUID

    // Upload Immagine
    @Multipart
    @POST("api/immobili/{id}/immagini")
    suspend fun uploadImmagine(
        @Path("id") id: String,
        @Part file: MultipartBody.Part
    ): Response<String>

    // --- UTENTI ---
    // Ottieni Profilo Utente
    @GET("api/utenti/{id}")
    suspend fun getUserProfile(@Path("id") id: String): Response<UserProfileDTO>

    // Ottieni i Immobili Preferiti di un Utente
    @GET("api/utenti/{userId}/preferiti")
    suspend fun getPreferiti(@Path("userId") userId: String): List<ImmobileDTO>

    // Aggiorna Profilo Utente (Telefono e Password)
    @PUT("api/utenti/{id}")
    suspend fun updateUserProfile(
        @Path("id") id: String,
        @Body request: UserUpdateRequest
    ): Response<Any>

    // --- NOTIFICHE & APPUNTAMENTI ---
    @GET("api/utenti/{id}/notifiche")
    suspend fun getUserNotifications(@Path("id") userId: String): List<NotificationDTO>

    @GET("api/immobili")
    suspend fun cercaImmobili(
        @Query("comune") comune: String,
        @Query("ricerca") ricerca: String
    ): Response<List<ImmobileDTO>>
    @GET("api/utenti/{id}/appuntamenti")
    suspend fun getUserAppointments(@Path("id") userId: String): List<AppuntamentoDTO>

    // --- DETTAGLIO NOTIFICA & PROPOSTE ---
    @GET("api/notifiche/{id}")
    suspend fun getNotificationDetail(@Path("id") id: String): Response<NotificationDetailDTO>

    @POST("api/notifiche/{id}/risposta")
    suspend fun respondToProposal(
        @Path("id") id: String,
        @Body response: ProposalResponseRequest
    ): Response<Any>

    // --- APPUNTAMENTI ---
    @POST("api/appuntamenti/crea")
    suspend fun createAppointment(@Body request: AppuntamentoRequest): Response<String>

    // Endpoint per ottenere i dettagli (Assicurati di implementarlo nel backend se non c'è!)
    // Se il backend non ha questo endpoint specifico, useremo un filtro lato client nel ViewModel.
    @GET("api/appuntamenti/{id}")
    suspend fun getAppointment(@Path("id") id: String): Response<AppuntamentoDTO>

    @GET("api/appuntamenti/miei/{userId}")
    suspend fun getMyAppointments(@Path("userId") userId: String): List<AppuntamentoDTO>

    // --- MANAGER ---
    @GET("api/manager/agenzie")
    suspend fun getAllAgenzie(): List<AgenziaDTO>
}
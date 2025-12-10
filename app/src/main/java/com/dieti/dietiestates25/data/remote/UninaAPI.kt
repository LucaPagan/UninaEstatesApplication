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
    @GET("api/utenti/{userId}/preferiti")
    suspend fun getPreferiti(@Path("userId") userId: String): List<ImmobileDTO>
}
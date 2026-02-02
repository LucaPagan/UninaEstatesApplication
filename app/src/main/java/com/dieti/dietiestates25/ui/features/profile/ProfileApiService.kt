package com.dieti.dietiestates25.ui.features.profile

import com.dieti.dietiestates25.data.remote.UtenteResponseDTO
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileApiService {

    @GET("api/utenti/{id}")
    suspend fun getUserProfile(@Path("id") id: String): Response<UtenteResponseDTO>

    // --- NUOVA CHIAMATA ---
    @DELETE("api/utenti/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Void>
}
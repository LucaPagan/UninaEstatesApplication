package com.dieti.dietiestates25.ui.features.profile

import com.dieti.dietiestates25.data.remote.AgenteDTO
import com.dieti.dietiestates25.data.remote.UtenteResponseDTO
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileApiService {

    // --- UTENTE ---
    @GET("api/utenti/{id}")
    suspend fun getUserProfile(@Path("id") id: String): Response<UtenteResponseDTO>

    @DELETE("api/utenti/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<Void>

    // --- MANAGER (Spostato qui) ---
    // Dato che il profilo del manager lo vediamo nella stessa schermata,
    // mettiamo qui la chiamata per ottenerne i dati.
    @GET("api/agenti/{id}")
    suspend fun getAgenteProfile(@Path("id") id: String): Response<AgenteDTO>
}
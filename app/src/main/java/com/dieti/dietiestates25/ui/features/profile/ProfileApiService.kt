package com.dieti.dietiestates25.ui.features.profile
import com.dieti.dietiestates25.data.remote.UtenteResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ProfileApiService {
    
    // Endpoint: GET /api/utenti/{id}
    @GET("api/utenti/{id}")
    suspend fun getUserProfile(@Path("id") id: String): Response<UtenteResponseDTO>
}
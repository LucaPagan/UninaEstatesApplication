package com.dieti.dietiestates25.data.remote

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

// DTO per visualizzare l'offerta ricevuta

data class OffertaRequest(
    val utenteId: String,
    val immobileId: String,
    val importo: Int,
    val corpo: String? = null
)

interface OffertaApiService {

    // L'utente invia un'offerta
    @POST("api/offerte")
    suspend fun inviaOfferta(@Body request: OffertaRequest): Response<String>

    // FIX PATH: Il manager scarica le offerte pendenti
    // Mappiamo la chiamata del frontend all'endpoint corretto del backend
    @GET("api/offerte/pendenti/{agenteId}")
    suspend fun getOfferteRicevute(@Path("agenteId") agenteId: String): Response<List<OffertaRicevutaDTO>>
}
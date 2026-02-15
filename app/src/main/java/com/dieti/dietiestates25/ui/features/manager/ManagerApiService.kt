package com.dieti.dietiestates25.ui.features.manager

import com.dieti.dietiestates25.data.remote.TrattativaSummaryDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

data class DashboardStatsDTO(
    val numeroNotifiche: Int,
    val numeroProposte: Int,
    val isCapo: Boolean
)

data class RispostaRequest(
    val offertaId: String,
    val venditoreId: String,
    val esito: String,
    val nuovoPrezzo: Int? = null,
    val messaggio: String? = null
)

data class RichiestaDTO(
    val id: String,
    val titolo: String,
    val descrizione: String?,
    val data: String,
    val stato: String,
    val immagineUrl: String? = null
)

data class EsitoRichiestaRequest(
    val id: String
)

// FIX: Classe per mappare la risposta JSON del backend (evita crash Unit)
data class ManagerActionResponse(
    val message: String? = null,
    val error: String? = null
)

interface ManagerApiService {

    @GET("/api/manager/dashboard/{agenteId}")
    suspend fun getDashboardStats(@Path("agenteId") agenteId: String): Response<DashboardStatsDTO>

    @POST("api/agente/create-sub-agent")
    suspend fun createSubAgent(@Body request: CreateSubAgentRequest): Response<Unit>

    @POST("/api/risposte")
    suspend fun inviaRisposta(@Body request: RispostaRequest): Response<ManagerActionResponse> // FIX: Return Type

    @GET("/api/manager/richieste/{agenteId}/pendenti")
    suspend fun getRichiestePendenti(@Path("agenteId") agenteId: String): Response<List<RichiestaDTO>>

    @POST("/api/manager/richieste/accetta")
    suspend fun accettaRichiesta(
        @Header("X-Manager-Id") managerId: String,
        @Body request: EsitoRichiestaRequest
    ): Response<ManagerActionResponse> // FIX: Return Type

    @POST("/api/manager/richieste/rifiuta")
    suspend fun rifiutaRichiesta(@Body request: EsitoRichiestaRequest): Response<ManagerActionResponse> // FIX: Return Type

    @GET("/api/trattative/manager/{agenteId}")
    suspend fun getTrattativeManager(@Path("agenteId") agenteId: String): Response<List<TrattativaSummaryDTO>>
}
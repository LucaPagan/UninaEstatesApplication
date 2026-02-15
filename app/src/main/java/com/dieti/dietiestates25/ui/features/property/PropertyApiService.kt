package com.dieti.dietiestates25.ui.features.property

import com.dieti.dietiestates25.data.remote.ImmobileCreateRequest
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface PropertyApiService {

    @GET("api/immobili")
    suspend fun getImmobili(
        @Query("query") query: String? = null,
        @Query("tipoVendita") tipoVendita: Boolean? = null,
        @Query("minPrezzo") minPrezzo: Int? = null,
        @Query("maxPrezzo") maxPrezzo: Int? = null,
        @Query("minMq") minMq: Int? = null,
        @Query("maxMq") maxMq: Int? = null,
        @Query("minStanze") minStanze: Int? = null, // AGGIUNTO
        @Query("maxStanze") maxStanze: Int? = null, // AGGIUNTO
        @Query("bagni") bagni: Int? = null,
        @Query("condizione") condizione: String? = null,
        @Query("lat") lat: Double? = null,
        @Query("lon") lon: Double? = null,
        @Query("radiusKm") radiusKm: Double? = null
    ): List<ImmobileDTO>

    @GET("api/immobili/{id}")
    suspend fun getImmobileById(@Path("id") id: String): ImmobileDTO

    @GET("api/immobili/cities")
    suspend fun getComuni(@Query("query") query: String): List<String>

    @Multipart
    @POST("api/immobili")
    suspend fun creaImmobile(
        @Part("immobile") immobile: RequestBody,
        @Part immagini: List<MultipartBody.Part>
    ): ImmobileDTO

    @GET("api/immobili/agente/{id}")
    suspend fun getImmobiliByAgente(@Path("id") id: String): Response<List<ImmobileDTO>>

    @PUT("api/immobili/{id}")
    suspend fun updateImmobile(
        @Path("id") id: String,
        @Body request: ImmobileCreateRequest
    ): Response<ImmobileDTO>

    @DELETE("api/immobili/{id}")
    suspend fun deleteImmobile(@Path("id") id: String): Response<Unit>


    @Multipart
    @POST("api/immobili/{id}/immagini")
    suspend fun aggiungiImmagini(
        @Path("id") id: String,
        @Part immagini: List<MultipartBody.Part>
    ): Response<ImmobileDTO>

    @DELETE("api/immobili/immagini/{imageId}")
    suspend fun eliminaImmagine(@Path("imageId") imageId: Int): Response<Unit>


    // Utilizziamo il metodo @GetMapping base del controller che restituisce tutti gli immobili
    @GET("api/immobili")
    suspend fun getAllImmobili(): Response<List<ImmobileDTO>>
}
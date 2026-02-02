package com.dieti.dietiestates25.data.remote

import android.util.Log
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface DietiEstatesApi {
    @GET("api/immobili")
    suspend fun getImmobili(
        @Query("query") query: String? = null,
        @Query("tipoVendita") tipoVendita: Boolean? = null,
        @Query("minPrezzo") minPrezzo: Int? = null,
        @Query("maxPrezzo") maxPrezzo: Int? = null,
        @Query("minMq") minMq: Int? = null,
        @Query("maxMq") maxMq: Int? = null,
        @Query("bagni") bagni: Int? = null,
        @Query("condizione") condizione: String? = null,
        @Query("lat") lat: Double? = null,
        @Query("lon") lon: Double? = null,
        @Query("radiusKm") radiusKm: Double? = null
    ): List<ImmobileDTO>

    // NUOVO: Ottiene suggerimenti comuni
    @GET("api/immobili/cities")
    suspend fun getComuni(@Query("query") query: String): List<String>

    @Multipart
    @POST("api/immobili")
    suspend fun creaImmobile(
        @Part("immobile") immobile: RequestBody,
        @Part immagini: List<MultipartBody.Part>
    ): ImmobileDTO
}

object RetrofitClient {
    // 10.0.2.2 è l'indirizzo speciale per localhost dell'emulatore
    private const val BASE_URL = "http://10.0.2.2:8080/"

    // Variabile statica per l'email (volatile)
    var loggedUserEmail: String? = null

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()

            // LOG DI DEBUG: Vediamo cosa stiamo inviando
            if (loggedUserEmail != null) {
                Log.d("AUTH_DEBUG", "Aggiungo Header X-Auth-Email: $loggedUserEmail")
                builder.header("X-Auth-Email", loggedUserEmail!!)
            } else {
                Log.e("AUTH_DEBUG", "ATTENZIONE: loggedUserEmail è NULL! L'header non verrà inviato.")
            }

            val request = builder.build()
            chain.proceed(request)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun getImageUrl(imageId: Int): String {
        return "${BASE_URL}api/immagini/$imageId"
    }
}
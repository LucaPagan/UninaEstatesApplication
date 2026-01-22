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
import java.util.concurrent.TimeUnit

interface DietiEstatesApi {
    // Defines the GET call to fetch the list of properties
    @GET("api/immobili")
    suspend fun getImmobili(): List<ImmobileDTO>

    // Defines the POST call to create a property with images
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
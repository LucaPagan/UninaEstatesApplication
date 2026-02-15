package com.dieti.dietiestates25.data.remote

import AuthApiService
import android.util.Log
import com.dieti.dietiestates25.ui.features.manager.ManagerApiService
import com.dieti.dietiestates25.ui.features.property.PropertyApiService
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Interfaccia segnaposto
interface DietiEstatesApi { }

object RetrofitClient {
    // Sostituisci con il tuo IP
    private const val BASE_URL = "http://192.168.1.9:8080/"

    @Volatile
    var loggedUserEmail: String? = null

    @Volatile
    var authToken: String? = null

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()

            // Header Authorization (Bearer Token)
            authToken?.let {
                builder.header("Authorization", it)
            }

            // Header Legacy (opzionale se il backend non lo usa più)
            loggedUserEmail?.let {
                builder.header("X-Auth-Email", it)
            }

            Log.d("API_REQ", "${original.method} ${original.url}")

            // DEBUG: Verifichiamo cosa stiamo inviando
            if (authToken != null) {
                Log.d("AUTH_DEBUG", "Invio Header Authorization: $authToken")
            } else {
                Log.w("AUTH_DEBUG", "!!! ATTENZIONE !!! Richiesta SENZA Token Auth")
            }

            val request = builder.build()
            chain.proceed(request)
        }
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val offertaService: OffertaApiService by lazy {
        retrofit.create(OffertaApiService::class.java)
    }
    // FIX: Aggiunta la definizione del servizio mancante
    val notificationService: NotificationApiService by lazy {
        retrofit.create(NotificationApiService::class.java)
    }

    val managerService: ManagerApiService by lazy {
        retrofit.create(ManagerApiService::class.java)
    }

    val authService: AuthApiService by lazy {
        retrofit.create(AuthApiService::class.java)
    }

    val propertyService: PropertyApiService by lazy {
        retrofit.create(PropertyApiService::class.java)
    }

    val adminService: AdminApiService by lazy {
        retrofit.create(AdminApiService::class.java)
    }

    fun getFullUrl(relativePath: String?): String? {
        if (relativePath.isNullOrBlank()) return null
        if (relativePath.startsWith("http")) return relativePath
        val cleanBase = BASE_URL.removeSuffix("/")
        val cleanPath = relativePath.removePrefix("/")
        return "$cleanBase/$cleanPath"
    }
}
package com.dieti.dietiestates25.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    // IMPORTANTE: 
    // Usa "10.0.2.2" se sei sull'emulatore Android.
    // Usa l'IP del tuo PC (es. "192.168.1.5") se usi un telefono vero collegato via USB/WiFi.
    private const val BASE_URL = "http://10.0.2.2:8080/" 

    // Configura il logger per vedere le richieste nella console (Logcat)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY 
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS) // Aumenta il tempo per le immagini pesanti
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Crea l'istanza dell'API
    val api: UninaApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UninaApi::class.java)
    }
}
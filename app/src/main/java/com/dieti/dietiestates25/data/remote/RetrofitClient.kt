package com.dieti.dietiestates25.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Se usi l'EMULATORE Android standard:
    private const val BASE_URL = "http://10.0.2.2:8080/"
    // Se usi un DISPOSITIVO FISICO: usa l'IP del PC, es "http://192.168.1.15:8080/"

    val instance: UninaAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UninaAPI::class.java)
    }

    // Helper per costruire l'URL delle immagini
    fun getImageUrl(imageId: Int): String {
        return "${BASE_URL}api/immagini/$imageId"
    }
}
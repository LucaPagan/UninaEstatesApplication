package com.dieti.dietiestates25.ui.features.search

import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchApiService {
    @GET("api/ricerche")
    suspend fun getRicercheRecenti(): List<String>

    @DELETE("api/ricerche")
    suspend fun cancellaRicerca(@Query("query") query: String)
}

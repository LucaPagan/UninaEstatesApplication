package com.dieti.dietiestates25.data.remote

import com.dieti.dietiestates25.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AdminApiService {
    @POST("api/admin/create-admin")
    suspend fun createAdmin(@Body request: CreateAdminRequest): Response<Any>

    @POST("api/admin/create-agent")
    suspend fun createAgent(@Body request: CreateAgenteRequest): Response<Any>

    @POST("api/admin/create-agency")
    suspend fun createAgency(@Body request: CreateAgenziaRequest): Response<Any>

    @GET("api/admin/agencies-options")
    suspend fun getAgenciesOptions(): List<AgenziaOptionDTO>

    // --- NUOVI METODI ---

    @GET("api/admin/administrators-options")
    suspend fun getAdministratorsOptions(): List<AdminOptionDTO>

    @POST("api/admin/change-my-password")
    suspend fun changeMyPassword(@Body request: ChangeMyPasswordRequest): Response<Any>
}
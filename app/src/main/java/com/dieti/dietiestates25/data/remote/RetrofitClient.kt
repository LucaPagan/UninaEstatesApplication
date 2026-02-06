package com.dieti.dietiestates25.data.remote

import android.util.Log
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.concurrent.TimeUnit
// Assicurati di importare BuildConfig se/quando configurerai le varianti di build
// import com.dieti.dietiestates25.BuildConfig

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

    // --- NUOVI ENDPOINT RICERCA RECENTE ---

    @GET("api/ricerche")
    suspend fun getRicercheRecenti(): List<String>

    @DELETE("api/ricerche")
    suspend fun cancellaRicerca(@Query("query") query: String)
}

object RetrofitClient {
    // IN PRODUZIONE: Spostare questo URL nel file local.properties o build.gradle (flavor dimensions)
    // Esempio: BuildConfig.BASE_URL
    //private const val BASE_URL = "http://10.0.2.2:8080/"

    // 10.0.2.2 è l'indirizzo speciale per localhost dell'emulatore
    // 10.84.50.219 è l'indirizzo dell'hotspot di Danilo
    // 192.168.1.11 è l'indirizzo del wifi di casa Scala
    private const val BASE_URL = "http://192.168.1.9:8080/"

    // Variabile statica per l'email (In un'app reale, usare EncryptedSharedPreferences o AccountManager)
    @Volatile
    var loggedUserEmail: String? = null

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()

            // SICUREZZA: Aggiungi l'header di autenticazione
            loggedUserEmail?.let {
                builder.header("X-Auth-Email", it)
            }

            // LOGGING: Loggare solo in DEBUG per non esporre dati sensibili in produzione
            // if (BuildConfig.DEBUG) {
            Log.d("API_REQ", "${original.method} ${original.url}")
            if (loggedUserEmail != null) {
                Log.d("AUTH_DEBUG", "Auth Header presente per: $loggedUserEmail")
            } else {
                Log.w("AUTH_DEBUG", "Richiesta effettuata SENZA Auth Header (Utente non loggato?)")
            }
            // }

            val request = builder.build()
            chain.proceed(request)
        }
        // Timeout configurati per reti mobili potenzialmente lente
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

    /**
     * Metodo sicuro per ottenere l'URL completo di una risorsa.
     * Gestisce path relativi e assoluti, evitando duplicazione di slash.
     */
    fun getFullUrl(relativePath: String?): String? {
        if (relativePath.isNullOrBlank()) return null
        if (relativePath.startsWith("http")) return relativePath

        val cleanBase = BASE_URL.removeSuffix("/")
        val cleanPath = relativePath.removePrefix("/")

        return "$cleanBase/$cleanPath"
    }
}
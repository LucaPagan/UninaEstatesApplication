package com.dieti.dietiestates25.data.remote

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Interfaccia segnaposto se la tieni nello stesso file, altrimenti puoi rimuoverla se definita altrove
interface DietiEstatesApi {
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

    // --- AGGIUNTA FONDAMENTALE: Variabile per il Token di Autenticazione ---
    // Questa variabile viene popolata al login o al riavvio dell'app dal SessionManager
    @Volatile
    var authToken: String? = null

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()
            val builder = original.newBuilder()

            // 1. Header Email (Custom, se usato dal tuo backend)
            loggedUserEmail?.let {
                builder.header("X-Auth-Email", it)
            }

            // 2. --- AGGIUNTA FONDAMENTALE: Header Authorization standard ---
            // Inserisce il token (Basic o Bearer) salvato in memoria
            authToken?.let {
                builder.header("Authorization", it)
            }

            // LOGGING: Loggare solo in DEBUG per non esporre dati sensibili in produzione
            // if (BuildConfig.DEBUG) {
            Log.d("API_REQ", "${original.method} ${original.url}")

            if (loggedUserEmail != null) {
                Log.d("AUTH_DEBUG", "Header Email presente: $loggedUserEmail")
            }

            if (authToken != null) {
                Log.d("AUTH_DEBUG", "Header Authorization presente")
            } else {
                Log.w("AUTH_DEBUG", "Richiesta effettuata SENZA Auth Token (Utente non loggato?)")
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
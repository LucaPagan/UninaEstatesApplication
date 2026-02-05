import com.dieti.dietiestates25.data.remote.AdminLoginResponse
import com.dieti.dietiestates25.data.remote.LoginRequest
import com.dieti.dietiestates25.data.remote.UtenteRegistrazioneRequest
import com.dieti.dietiestates25.data.remote.UtenteResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("/auth/register")
    suspend fun registrazione(
        @Body request: UtenteRegistrazioneRequest
    ): Response<UtenteResponseDTO>

    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<UtenteResponseDTO>

    // NUOVO: Login specifico per amministratori
    @POST("/api/admin/login")
    suspend fun loginAdmin(@Body request: LoginRequest): Response<AdminLoginResponse>
}
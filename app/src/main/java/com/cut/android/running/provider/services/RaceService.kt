
import com.cut.android.running.common.response.IResponse
import com.cut.android.running.models.Race
import com.cut.android.running.models.User
import com.cut.android.running.models.dto.RaceDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RaceService {
    
    @GET("race")
    suspend fun getRaces(): Response<IResponse<List<Race>>>

    @GET("race/{id}")
    suspend fun getRaceById(@Path("id") id: Int): Response<IResponse<Race>>

    @POST("race")
    suspend fun addRace(@Body race: RaceDto): Response<IResponse<Boolean>>

    @PUT("race")
    suspend fun updateRace(@Body race: RaceDto): Response<IResponse<Boolean>>

    @DELETE("race/{id}")
    suspend fun deleteRace(@Path("id") id: Int): Response<IResponse<Boolean>>

    @POST("race/userrace/{email}/{raceId}")
    suspend fun addUserRelation(@Path("email") email: String, @Path("raceId") raceId: Int): Response<IResponse<Boolean>>

    @GET("race/racebyuser/{userCode}")
    suspend fun getRaceByUser(@Path("userCode") userCode: Int): Response<IResponse<List<Race>>>

    @GET("race/userbyrace/{raceId}")
    suspend fun getUserByRace(@Path("raceId") raceId: Int): Response<IResponse<List<User>>>

    @DELETE("race/userrace/{userCode}/{raceId}")
    suspend fun deleteUserRelation(@Path("userCode") userCode: Int, @Path("raceId") raceId: Int): Response<IResponse<Boolean>>
}
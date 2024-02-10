

import com.cut.android.running.common.response.IResponse
import com.cut.android.running.models.UniversityCenter
import com.raywenderlich.android.rwandroidtutorial.models.dto.UniversityCenterDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UcService {

    @GET("uc")
    suspend fun getUc(): Response<IResponse<List<UniversityCenter>>>

    @GET("uc/{id}")
    suspend fun getUcById(@Path("id") id: Int): Response<IResponse<UniversityCenter>>

    @POST("uc")
    suspend fun addUc(@Body uc: UniversityCenterDto): Response<IResponse<Boolean>>

    @PUT("uc")
    suspend fun  updateUc(@Body uc: UniversityCenterDto): Response<IResponse<Boolean>>

    @DELETE("uc/{id}")
    suspend fun deleteUc(@Path("id") id: Int): Response<IResponse<Boolean>>
}
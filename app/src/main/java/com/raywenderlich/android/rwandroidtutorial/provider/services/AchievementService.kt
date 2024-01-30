package com.raywenderlich.android.rwandroidtutorial.provider.services

import com.raywenderlich.android.rwandroidtutorial.common.response.IResponse
import com.raywenderlich.android.rwandroidtutorial.models.Achievement
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AchievementService {

    @GET("achievement")
    suspend fun getAchievements(): Response<IResponse<List<Achievement>>>

    @GET("achievement/{id}")
    suspend fun getAchievementById(@Path("id") id: Int): Response<IResponse<Achievement>>

    @POST("achievement")
    suspend fun addAchievement(@Body achievement: Achievement): Response<IResponse<Boolean>>

    @PUT("achievement")
    suspend fun updateAchievement(@Body achievement: Achievement): Response<IResponse<Boolean>>

    @DELETE("achievement/{id}")
    suspend fun deleteAchievement(@Path("id") id: Int): Response<IResponse<Boolean>>
}

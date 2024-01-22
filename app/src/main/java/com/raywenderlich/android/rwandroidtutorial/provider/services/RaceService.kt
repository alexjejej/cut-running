package com.raywenderlich.android.rwandroidtutorial.provider.services

import com.raywenderlich.android.rwandroidtutorial.common.response.IResponse
import com.raywenderlich.android.rwandroidtutorial.models.Race
import com.raywenderlich.android.rwandroidtutorial.models.dto.RaceDto
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
    suspend fun deleteRace(): Response<IResponse<Boolean>>
}
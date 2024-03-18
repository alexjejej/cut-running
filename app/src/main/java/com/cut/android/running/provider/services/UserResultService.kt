package com.cut.android.running.provider.services

import com.cut.android.running.common.response.IResponse
import com.cut.android.running.models.RaceResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RaceResultService {

    @GET("raceResult")
    suspend fun getAllRaceResults(): Response<IResponse<List<RaceResult>>>

    @GET("raceResult/byrace/{raceId}")
    suspend fun getResultsByRaceId(@Path("raceId") raceId: Int): Response<IResponse<List<RaceResult>>>

    @GET("raceResult/byemail/{email}")
    suspend fun getResultsByEmail(@Path("email") email: String): Response<IResponse<List<RaceResult>>>

    // Agregar, actualizar o eliminar resultados, si se necesita
    // Por ejemplo:
    @POST("raceResult")
    suspend fun addRaceResult(@Body raceResult: RaceResult): Response<IResponse<RaceResult>>

    @PUT("raceResult/{id}")
    suspend fun updateRaceResult(@Path("id") id: Int, @Body raceResult: RaceResult): Response<IResponse<Boolean>>

    @DELETE("raceResult/{id}")
    suspend fun deleteRaceResult(@Path("id") id: Int): Response<IResponse<Boolean>>
}



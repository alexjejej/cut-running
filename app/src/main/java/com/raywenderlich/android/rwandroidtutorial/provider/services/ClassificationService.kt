package com.raywenderlich.android.rwandroidtutorial.provider.services

import com.raywenderlich.android.rwandroidtutorial.common.response.IResponse
import com.raywenderlich.android.rwandroidtutorial.models.Classification
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ClassificationService {

    @GET("classification")
    suspend fun getClassifications(): Response<IResponse<List<Classification>>>

    @GET("classification/{id}")
    suspend fun getClassificationById(@Path("id") id: String): Response<IResponse<Classification>>

    @POST("classification")
    suspend fun addClassification(@Body classification: Classification): Response<IResponse<Boolean>>

    @PUT("classification")
    suspend fun updateClassification(@Body classification: Classification): Response<IResponse<Boolean>>

    @DELETE("classification/{id}")
    suspend fun deleteClassification(@Path("id") id: String): Response<IResponse<Boolean>>
}

package com.raywenderlich.android.rwandroidtutorial.provider.services

import com.raywenderlich.android.rwandroidtutorial.common.response.IResponse
import com.raywenderlich.android.rwandroidtutorial.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {

    @GET("user")
    suspend fun getUsers(): Response<IResponse<List<User>>>

    @GET("user/email/{email}")
    suspend fun getUserByEmail(@Path("email") email: String): Response<IResponse<User>>

    @POST("user")
    suspend fun addUser(@Body user: User): Response<IResponse<Boolean>>

    @PUT("user")
    suspend fun updateUser(@Body user: User): Response<IResponse<Boolean>>

    @DELETE("user/{id}")
    suspend fun deleteUser(@Path("id") id: String): Response<IResponse<Boolean>>
}

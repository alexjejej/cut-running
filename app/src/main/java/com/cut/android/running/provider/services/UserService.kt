package com.cut.android.running.provider.services

import com.cut.android.running.common.response.IResponse
import com.cut.android.running.models.User
import com.cut.android.running.models.dto.UserDto
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
    suspend fun addUser(@Body user: UserDto): Response<IResponse<Boolean>>

    @PUT("user")
    suspend fun updateUser(@Body user: User): Response<IResponse<Boolean>>

    @DELETE("user/{email}")
    suspend fun deleteUser(@Path("email") id: String): Response<IResponse<Boolean>>
}

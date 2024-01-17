package com.raywenderlich.android.rwandroidtutorial.provider.services

import com.raywenderlich.android.rwandroidtutorial.common.response.IResponse
import com.raywenderlich.android.rwandroidtutorial.models.Role
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Url

interface RoleService {
    @GET("role")
    suspend fun getRoles(): Response<IResponse<List<Role>>>

    @GET("role/{id}")
    suspend fun getRoleById(@Path("id") id: Int): Response<IResponse<Role>>

    @POST("role")
    suspend fun addRole(): Response<IResponse<Boolean>>

    @PUT("role")
    suspend fun updateRole(): Response<IResponse<Boolean>>

    @DELETE("role/{id}")
    suspend fun deleteRole(@Path("id") id: Int): Response<IResponse<Boolean>>
}
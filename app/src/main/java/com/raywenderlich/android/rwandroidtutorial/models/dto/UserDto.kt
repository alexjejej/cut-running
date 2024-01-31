package com.raywenderlich.android.rwandroidtutorial.models.dto

import com.google.gson.annotations.SerializedName

data class UserDto(
    val id: Int? = null,
    val firstname: String,
    val lastname: String? = null,
    val age: Int? = null,
    val height: Int? = null,
    val weight: Int? = null,
    val email: String,
    val pass: String? = null,
    val specialtyId: Int? = null,
    val specialty: Int? = null,
    val roleId: Int? = null,
    val enabled: Int? = null,
    val updateDate: String? = null,
    val totalsteps: Int? = null,
    val totaldistance: Float? = null,
    val code: Int? = null
)

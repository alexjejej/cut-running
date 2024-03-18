package com.cut.android.running.models

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int? = null,
    val firstname: String,
    val lastname: String? = null,
    val age: Int? = null,
    val height: Int? = null,
    val weight: Int? = null,
    val email: String,
    val pass: String? = null,
    val specialtyId: Int? = null,
    @SerializedName("Specialty") // Mapea el campo JSON "Specialty" a "specialty" en Kotlin
    val specialty: Specialty? = null,
    var Role: Role? = null,
    val enabled: Int? = null,
    val updateDate: String? = null,
    val distanceperstep: Int? = null,
    var totalsteps: Int? = null,
    val totaldistance: Float? = null,
    val code: Int? = null
)

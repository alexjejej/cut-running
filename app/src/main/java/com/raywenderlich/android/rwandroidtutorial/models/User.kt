package com.raywenderlich.android.rwandroidtutorial.models

data class User (
    val id: Int,
    val firstname: String,
    val lastname: String,
    val age: Int,
    val height: Double,
    val weight: Double,
    val email: String,
    val pass: String,
    val specialtyId: Int,
    val roleId: Int,
    val enabled: Boolean,
    val updateDate: String
)
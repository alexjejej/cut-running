package com.raywenderlich.android.rwandroidtutorial.models

import com.raywenderlich.android.rwandroidtutorial.login.ProviderType

data class User(
//    val code: Int,
//    val firstname: String,
//    val lastname: String,
//    val age: Int,
//    val height: Double,
//    val weight: Double,
//    val email: String,
//    val pass: String,
//    val specialtyId: Int,
//    val roleId: Int,
//    val enabled: Boolean,
//    val updateDate: String
    val cu: String,
    val career: String,
    val completeInformation: Boolean,
    val provider: String,
    val enable: Boolean,
    val semester: Int
)

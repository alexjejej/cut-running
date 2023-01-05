package com.raywenderlich.android.rwandroidtutorial.models

import com.raywenderlich.android.rwandroidtutorial.login.ProviderType

data class User(
    val cu: String,
    val career: String,
    val completeInformation: Boolean,
    val provider: String,
    val enable: Boolean,
    val semester: Int
)

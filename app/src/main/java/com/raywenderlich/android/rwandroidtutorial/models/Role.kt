package com.raywenderlich.android.rwandroidtutorial.models

import java.util.Date

data class Role (
    val id: Int,
    val name: String,
    val enabled: Boolean,
    val updateDate: String
)
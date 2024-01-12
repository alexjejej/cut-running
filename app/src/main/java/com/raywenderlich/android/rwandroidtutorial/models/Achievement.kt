package com.raywenderlich.android.rwandroidtutorial.models

data class Achievement (
    val id: Int,
    val name: String,
    val description: String,
    val enabled: Boolean,
    val updateDate: String
)
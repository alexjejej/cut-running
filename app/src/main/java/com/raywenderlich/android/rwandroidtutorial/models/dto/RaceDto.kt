package com.raywenderlich.android.rwandroidtutorial.models.dto

import com.raywenderlich.android.rwandroidtutorial.models.UniversityCenter

data class RaceDto (
    val id: Int?,
    val name: String,
    val date: String,
    val description: String,
    val UC: Int,
    val enabled: Int,
    val updateDate: String?
)
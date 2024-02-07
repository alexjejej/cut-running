package com.cut.android.running.models

data class Race (
    val id: Int,
    val name: String,
    val date: String,
    val description: String,
    val UC: UniversityCenter,
    val enabled: Int,
    val updateDate: String
)
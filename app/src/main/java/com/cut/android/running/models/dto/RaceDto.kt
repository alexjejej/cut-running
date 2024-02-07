package com.cut.android.running.models.dto

data class RaceDto (
    val id: Int?,
    val name: String,
    val date: String,
    val description: String,
    val UC: Int,
    val enabled: Int,
    val updateDate: String?
)
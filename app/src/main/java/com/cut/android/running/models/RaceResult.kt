package com.cut.android.running.models

data class RaceResult (
    val id: Int?,
    val raceId: Int,
    val userName: String,
    val userEmail: String,
    val time: String,
    val position: Int?
)
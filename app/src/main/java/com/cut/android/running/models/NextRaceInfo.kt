package com.cut.android.running.models

data class NextRaceInfo(
    val hasNextRace: Boolean,
    val daysUntilRace: Int,
    val eventTime: String?,
    val raceId: Int?
)


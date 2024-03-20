package com.cut.android.running.models

import java.io.Serializable

data class UniversityCenter (
    val id: Int,
    val name: String,
    val acronym: String,
    val enabled: Int,
    val updateDate: String
): Serializable
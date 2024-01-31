package com.raywenderlich.android.rwandroidtutorial.models


data class AchievementsByUser(
    val id: Int,
    var name:String?=null,
    var description: String?=null,
    var photo: String?=null,
    var steps: Int?=null,
    val enabled: Int?=null,
    val updateDate: String?=null
)
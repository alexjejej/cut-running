package com.cut.android.running.models

data class Achievement(
    val id: Int?=null,
    var name:String,
    var description:String,
    var progreso:String?=null,
    var steps:Int,
    var photo:String?=null,
    val enabled: Int?=null,
    val updateDate: String?=null
)
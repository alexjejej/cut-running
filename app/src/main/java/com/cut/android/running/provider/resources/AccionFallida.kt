package com.cut.android.running.provider.resources

data class AccionFallida(
    val tipo: String,
    val payload: String, // JSON representation of the data needed for the action
    val timestamp: Long = System.currentTimeMillis()
)


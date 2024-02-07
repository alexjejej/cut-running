package com.cut.android.running.common.response

data class IResponse<T> (
    val data: T? = null,
    val statusCode: Int,
    val isSuccess: Boolean,
    val isFailure: Boolean,
    val message: String? = null,
    val messages: List<String>? = null
)
package com.cut.android.running.provider

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitInstance {
    companion object {
        // Creacion de instancia Retrofit 2
        fun getRetrofit(): Retrofit = Retrofit.Builder()
            .baseUrl("http://200.39.173.48:8008/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
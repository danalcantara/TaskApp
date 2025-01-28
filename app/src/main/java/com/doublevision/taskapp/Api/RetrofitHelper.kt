package com.doublevision.taskapp.Api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitHelper {
    companion object {
        val retrofit = Retrofit.Builder().baseUrl("https://viacep.com.br/ws/")
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

}
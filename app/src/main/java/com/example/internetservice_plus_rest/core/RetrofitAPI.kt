package com.example.internetservice_plus_rest.core

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitAPI {
    companion object {
        const val BASE_URL = "http://192.168.100.38:45456/dedec/"
    }
    private var retrofit: Retrofit? = null

    fun getRetrofitApi(): PeticionesAPI {
        return getClient()!!.create(
            PeticionesAPI::class.java
        )
    }

    fun getClient(): Retrofit? {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return retrofit
    }

}
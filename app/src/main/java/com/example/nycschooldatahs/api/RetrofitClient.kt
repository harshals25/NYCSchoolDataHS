package com.example.nycschooldatahs.api

import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitClient {

    private const val BASE_URL = "https://data.cityofnewyork.us/resource/"

    private var retrofitService: RestClient? = null
    fun getRetrofitInstance(): RestClient {
        if (retrofitService == null) {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            retrofitService = retrofit.create(RestClient::class.java)
        }
        return retrofitService!!
    }

}
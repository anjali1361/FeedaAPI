package com.example.stockmaintenanceapp.services

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {

    private const val URL="https://feedbackend1361.herokuapp.com/api/"

    //create logger
    private val logger =HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    //create OkHttp Client
    private val okHttp=OkHttpClient.Builder().addInterceptor(logger)

    //create Retrofit Builder
    private val builder by lazy {
        Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttp.build()).build()
    }

    fun <T> buildService(serviceType:Class<T>):T{
        return builder.create(serviceType)
    }
}

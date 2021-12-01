package com.example.stockmaintenanceapp.services

import com.example.stockmaintenanceapp.model.AvailableProducts
import com.example.stockmaintenanceapp.model.AvailableProductsList
import retrofit2.Call
import retrofit2.http.*

//define functions which will map to our web-service end point URLs
interface ProductService {

    @GET("products")
    suspend fun getAvailableProductsList():AvailableProducts

//    @GET("products")
//    fun getAvailableProductsList(): Call<AvailableProducts>

//    @GET("products")
//    fun getFilteredAvailableProductsList(@Query("country") country: String): Call<AvailableProducts>

    @GET("products/{id}")
    fun getAvailableProduct(@Path("id") id: String): Call<AvailableProductsList>

    @POST("products")
    fun addAvailableProduct(@Body newProduct: AvailableProductsList): Call<AvailableProductsList>

    @PUT("products/{id}")
    fun updateAvailableProduct(
        @Path("id") id: String,
        @Body newProduct: AvailableProductsList
    ): Call<AvailableProductsList>

    @DELETE("products/{id}")
    fun deleteProduct(@Path("id") id: String): Call<Unit>
}
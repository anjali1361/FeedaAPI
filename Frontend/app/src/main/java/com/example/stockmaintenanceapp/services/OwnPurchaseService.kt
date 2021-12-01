package com.example.stockmaintenanceapp.services

import com.example.stockmaintenanceapp.model.OwnPurchase
import com.example.stockmaintenanceapp.model.OwnPurchaseList
import retrofit2.Call
import retrofit2.http.*

interface OwnPurchaseService {

//    @GET("ownPurchases")
//    fun getOwnPurchaseList(): Call<OwnPurchase>

    @GET("ownPurchases")
    suspend fun getOwnPurchaseList(): OwnPurchase

    @GET("ownPurchases/{id}")
    fun getProduct(@Path("id") id: String): Call<OwnPurchaseList>

    @POST("ownPurchases")
    fun addProduct(@Body newProduct: OwnPurchaseList): Call<OwnPurchaseList>

    @PUT("ownPurchases/{id}")
    fun updateProduct(
        @Path("id") id: String,
        @Body newProduct: OwnPurchaseList
    ): Call<OwnPurchaseList>

    @DELETE("ownPurchases/{id}")
    fun deletePurchasedProduct(@Path("id") id: String): Call<Unit>
}
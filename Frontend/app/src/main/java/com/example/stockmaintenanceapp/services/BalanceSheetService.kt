package com.example.stockmaintenanceapp.services

import com.example.stockmaintenanceapp.model.BalanceSheetModel
import retrofit2.Call
import retrofit2.http.*

interface BalanceSheetService {

//    @GET("balancesheet")
//    fun getBalanceSheetList(): Call<ArrayList<BalanceSheetModel>>

    @GET("balancesheet")
    suspend fun getBalanceSheetList(): ArrayList<BalanceSheetModel>

//    @GET("balancesheet")
//    fun getReportCardComments(): Call<ArrayList<BalanceSheetModel>>

    @POST("balancesheet")
    fun addBalanceSheetItem(@Body newComment: BalanceSheetModel): Call<BalanceSheetModel>

    @PUT("balancesheet/{id}")
    fun updateBalanceSheetItem(
        @Path("id") id: String,
        @Body newProduct: BalanceSheetModel
    ): Call<BalanceSheetModel>

    @DELETE("balancesheet/{id}")
    fun deleteBalanceSheetItem(@Path("id") id: String): Call<Unit>
}
package com.example.stockmaintenanceapp.services

import com.example.stockmaintenanceapp.model.*
import retrofit2.Call
import retrofit2.http.*

interface CustomerAndSaleService {

//    @GET("users")
//    fun getAllCustomer(): Call<ArrayList<Customers>>

    @GET("users")
   suspend fun getAllCustomer(): ArrayList<Customers>

//    @GET("users/sales")
//    fun getAllCustomerDetail(): Call<ArrayList<CustomerDetail>>

    @GET("users/sales")
   suspend fun getAllCustomerDetail(): ArrayList<CustomerDetail>

    @POST("users")
    fun addNewCustomer(@Body newProduct: Customers): Call<Customers>

    @PUT("users/{id}")
    fun updateCustomerDetails(
        @Path("id") id: String,
        @Body newCustomer: Customers
    ): Call<Customers>

    @DELETE("users/{id}")
    fun deleteCustomer(@Path("id") id: String): Call<Unit>

//    @GET("customerAndSalesRoutes")
//    fun getCustomerSalesList(@Body newProduct: CustomerSales): Call<CustomerSales>

    @POST("customerAndSalesRoutes")
    fun addCustomerSalesList(@Body newProduct: CustomerSales): Call<CustomerSales>

    @PUT("customerAndSalesRoutes/{id}")
    fun updateProductToBeBought(
        @Path("id") id: String,
        @Body updatedProduct: CustomerSales
    ): Call<CustomerSales>

    @DELETE("customerAndSalesRoutes/{id}")
    fun deleteProductToBeBought(@Path("id") id: String): Call<Unit>
}
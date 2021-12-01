package com.example.stockmaintenanceapp.repository

import com.example.stockmaintenanceapp.model.AvailableProducts
import com.example.stockmaintenanceapp.model.AvailableProductsList
import com.example.stockmaintenanceapp.services.ProductService
import com.example.stockmaintenanceapp.services.ServiceBuilder

class AvailableProductsRepository {

    //fetch data in background
    val productService = ServiceBuilder.buildService(ProductService::class.java)
    suspend fun getAvailableProduct():AvailableProducts = productService.getAvailableProductsList()
}
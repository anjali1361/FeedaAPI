package com.example.stockmaintenanceapp.repository

import com.example.stockmaintenanceapp.model.AvailableProducts
import com.example.stockmaintenanceapp.model.OwnPurchase
import com.example.stockmaintenanceapp.services.OwnPurchaseService
import com.example.stockmaintenanceapp.services.ProductService
import com.example.stockmaintenanceapp.services.ServiceBuilder

class OwnPurchaseRepository {
    //fetch data in background
    val ownPurchaseService = ServiceBuilder.buildService(OwnPurchaseService::class.java)
    suspend fun getOwnPurchaseList(): OwnPurchase = ownPurchaseService.getOwnPurchaseList()
}
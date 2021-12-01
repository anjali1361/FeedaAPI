package com.example.stockmaintenanceapp.repository

import com.example.stockmaintenanceapp.model.AvailableProducts
import com.example.stockmaintenanceapp.model.BalanceSheetModel
import com.example.stockmaintenanceapp.services.BalanceSheetService
import com.example.stockmaintenanceapp.services.ProductService
import com.example.stockmaintenanceapp.services.ServiceBuilder

class BalanceSheetRepository {

    //fetch data in background
    val balanceSheetRepository = ServiceBuilder.buildService(BalanceSheetService::class.java)
    suspend fun getBalanceSheet(): ArrayList<BalanceSheetModel> = balanceSheetRepository.getBalanceSheetList()
}
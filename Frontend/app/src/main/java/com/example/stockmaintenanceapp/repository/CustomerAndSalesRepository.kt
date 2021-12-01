package com.example.stockmaintenanceapp.repository

import com.example.stockmaintenanceapp.model.AvailableProducts
import com.example.stockmaintenanceapp.model.Customers
import com.example.stockmaintenanceapp.services.CustomerAndSaleService
import com.example.stockmaintenanceapp.services.ProductService
import com.example.stockmaintenanceapp.services.ServiceBuilder

class CustomerAndSalesRepository {

    //fetch data in background
    val customerAndSalesService = ServiceBuilder.buildService(CustomerAndSaleService::class.java)
    suspend fun getCustomerAndSales():ArrayList<Customers> = customerAndSalesService.getAllCustomer()
}
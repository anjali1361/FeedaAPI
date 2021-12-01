package com.example.stockmaintenanceapp.repository

import com.example.stockmaintenanceapp.model.CustomerDetail
import com.example.stockmaintenanceapp.model.Customers
import com.example.stockmaintenanceapp.services.CustomerAndSaleService
import com.example.stockmaintenanceapp.services.ServiceBuilder

class CustomerDetailRepository {
    val customerAndSalesService = ServiceBuilder.buildService(CustomerAndSaleService::class.java)
    suspend fun getCustomerDetail(): ArrayList<CustomerDetail> = customerAndSalesService.getAllCustomerDetail()
}
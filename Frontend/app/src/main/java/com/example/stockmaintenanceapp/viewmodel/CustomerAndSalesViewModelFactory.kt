package com.example.stockmaintenanceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stockmaintenanceapp.repository.CustomerAndSalesRepository

class CustomerAndSalesViewModelFactory(private val customerAndSalesRepository: CustomerAndSalesRepository):
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return CustomerAndSalesViewModel(customerAndSalesRepository) as T
    }
}
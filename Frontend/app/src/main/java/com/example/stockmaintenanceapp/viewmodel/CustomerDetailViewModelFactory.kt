package com.example.stockmaintenanceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stockmaintenanceapp.repository.CustomerDetailRepository

class CustomerDetailViewModelFactory(private val customerDetailRepository: CustomerDetailRepository):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

       return CustomerDetailViewModel(customerDetailRepository) as T
    }

}
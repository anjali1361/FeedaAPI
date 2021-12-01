package com.example.stockmaintenanceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stockmaintenanceapp.repository.AvailableProductsRepository

class AvailableProductViewModelFactory(private val availableProductsRepository: AvailableProductsRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AvailableProductsViewModel(availableProductsRepository) as T
    }


}
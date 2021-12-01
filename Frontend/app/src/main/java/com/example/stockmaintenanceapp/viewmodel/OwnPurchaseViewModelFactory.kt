package com.example.stockmaintenanceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stockmaintenanceapp.repository.OwnPurchaseRepository

class OwnPurchaseViewModelFactory(private val ownPurchaseRepository: OwnPurchaseRepository) :
    ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return OwnPurchaseViewModel(ownPurchaseRepository) as T
    }
}
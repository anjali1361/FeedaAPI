package com.example.stockmaintenanceapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.stockmaintenanceapp.repository.BalanceSheetRepository

class BalanceSheetViewModelFactory(private val balanceSheetRepository: BalanceSheetRepository) :ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        return BalanceSheetViewModel(balanceSheetRepository) as T
    }
}
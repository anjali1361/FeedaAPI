package com.example.stockmaintenanceapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockmaintenanceapp.model.AvailableProducts
import com.example.stockmaintenanceapp.model.BalanceSheetModel
import com.example.stockmaintenanceapp.repository.BalanceSheetRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class BalanceSheetViewModel(private val balanceSheetRepository: BalanceSheetRepository) :
    ViewModel() {

    val balanceSheetMutableLiveData: MutableLiveData<ArrayList<BalanceSheetModel>> =
        MutableLiveData()

    //fetch data from repository to shpw in UI
    fun getProduct() {

        //to return to UI from background thread
        try{
            viewModelScope.launch {
                val response = balanceSheetRepository.getBalanceSheet()
                balanceSheetMutableLiveData.value = response


            }
        } catch (e: Exception) {
            e.message?.let { Log.d("Balance", it) }
        }
    }
}
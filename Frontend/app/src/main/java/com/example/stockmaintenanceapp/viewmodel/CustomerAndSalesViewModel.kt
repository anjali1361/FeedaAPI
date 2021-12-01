package com.example.stockmaintenanceapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockmaintenanceapp.model.Customers
import com.example.stockmaintenanceapp.repository.AvailableProductsRepository
import com.example.stockmaintenanceapp.repository.CustomerAndSalesRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class CustomerAndSalesViewModel(private val customerAndSalesRepository: CustomerAndSalesRepository):ViewModel() {
    val customerAndSalesMutableLiveData: MutableLiveData<List<Customers>> = MutableLiveData()

    fun getCustomerAndSales(){

        try{
            viewModelScope.launch {
                val response=customerAndSalesRepository.getCustomerAndSales()
                customerAndSalesMutableLiveData.value=response

            }
        }
        catch (e: Exception){
            e.message?.let { Log.d("Gallery", it) }
        }
    }
}
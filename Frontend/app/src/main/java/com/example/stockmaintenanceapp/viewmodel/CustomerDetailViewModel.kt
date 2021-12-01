package com.example.stockmaintenanceapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockmaintenanceapp.model.CustomerDetail
import com.example.stockmaintenanceapp.model.Customers
import com.example.stockmaintenanceapp.repository.CustomerDetailRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class CustomerDetailViewModel(private val customerDetailRepository: CustomerDetailRepository):ViewModel() {

    val customerdetailMutableLiveData: MutableLiveData<List<CustomerDetail>> = MutableLiveData()

    fun getCustomerDetail(){

        try {
            viewModelScope.launch {
                val response=customerDetailRepository.getCustomerDetail()
                customerdetailMutableLiveData.value=response
        } }catch (e: Exception){
            e.message?.let { Log.d("Gallery", it) }
        }

        }
    }

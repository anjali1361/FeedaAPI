package com.example.stockmaintenanceapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockmaintenanceapp.model.AvailableProducts
import com.example.stockmaintenanceapp.model.AvailableProductsList
import com.example.stockmaintenanceapp.model.Customers
import com.example.stockmaintenanceapp.repository.AvailableProductsRepository
import kotlinx.coroutines.launch
import java.lang.Exception

//viewmodel to show the data fetched in background to UI,
//whenever viewmodel takes in parameter(here availableProductsRepository), we have to create viewModelFactory
class AvailableProductsViewModel(private val availableProductsRepository: AvailableProductsRepository):ViewModel() {

    val productMutableLiveData:MutableLiveData<AvailableProducts> = MutableLiveData()

    //fetch data from repository to shpw in UI
    fun getProduct(){

        //to return to UI from background thread
        try{
            viewModelScope.launch {
                val response=availableProductsRepository.getAvailableProduct()
                productMutableLiveData.value=response


            }
        }
        catch (e:Exception){
            e.message?.let { Log.d("Home", it) }
        }
    }
}
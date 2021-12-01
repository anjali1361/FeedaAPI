package com.example.stockmaintenanceapp.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stockmaintenanceapp.model.AvailableProducts
import com.example.stockmaintenanceapp.model.OwnPurchase
import com.example.stockmaintenanceapp.repository.OwnPurchaseRepository
import kotlinx.coroutines.launch
import java.lang.Exception

class OwnPurchaseViewModel(private val ownPurchaseRepository: OwnPurchaseRepository):ViewModel() {

    val ownPurchaseMutableLiveData: MutableLiveData<OwnPurchase> = MutableLiveData()

    fun getOwnPurchase(){
        //to return to UI from background thread
        try {
            viewModelScope.launch {
                val response=ownPurchaseRepository.getOwnPurchaseList()
                ownPurchaseMutableLiveData.value=response

            }
        } catch (e: Exception){
            e.message?.let { Log.d("Slideshow", it) }
        }
    }
}
package com.example.stockmaintenanceapp.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stockmaintenanceapp.R
import com.example.stockmaintenanceapp.services.OwnPurchaseService
import com.example.stockmaintenanceapp.services.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateItemToBuyActivity : AppCompatActivity() {

    var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_item_to_buy)

        val bundle: Bundle? = intent.extras

        if (bundle?.containsKey(ARG_ITEM_ID)!!) {

            id = intent.getStringExtra(ARG_ITEM_ID).toString()
            Log.d("Update", id!!)
        }

        initDeleteButton(id)
    }

    private fun initDeleteButton(id: String?) {

        val productService = ServiceBuilder.buildService(OwnPurchaseService::class.java)
        val requestCall = id?.let { productService.deletePurchasedProduct(it) }

        requestCall?.enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) {
                    finish()
                    Toast.makeText(
                        this@UpdateItemToBuyActivity, "Item deleted sucessfully",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    Toast.makeText(
                        this@UpdateItemToBuyActivity,
                        "Error Ocuured",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(
                    this@UpdateItemToBuyActivity, "Error Ocuured" + t.toString(),
                    Toast.LENGTH_SHORT
                ).show()

            }

        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {

        const val ARG_ITEM_ID = "item_id"
    }
}
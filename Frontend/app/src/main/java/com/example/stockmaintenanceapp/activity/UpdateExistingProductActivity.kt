package com.example.stockmaintenanceapp.activity

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.stockmaintenanceapp.R
import com.example.stockmaintenanceapp.model.AvailableProductsList
import com.example.stockmaintenanceapp.services.ProductService
import com.example.stockmaintenanceapp.services.ServiceBuilder
import com.facebook.shimmer.ShimmerFrameLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateExistingProductActivity : AppCompatActivity() {

    lateinit var etAddProduct: EditText
    lateinit var etQuantity: EditText
    lateinit var etRatePerUnit: EditText
    lateinit var shimmer_frame_layout: ShimmerFrameLayout
    lateinit var llet: LinearLayout
    lateinit var delete: Button
    lateinit var update: Button
    lateinit var toolbar: Toolbar
    lateinit var product: AvailableProductsList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_existing_product)

        initViews()

        val bundle: Bundle? = intent.extras

        if (bundle?.containsKey(ARG_ITEM_ID)!!) {

            val id = intent.getStringExtra(ARG_ITEM_ID)
            Log.d("HomeAdapter", id.toString())

            if (id != null) {
                loadDetails(id)
                initUpdateButton(id)
                initDeleteButton(id)
            }

        }
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        etAddProduct = findViewById(R.id.etAddProduct)
        etQuantity = findViewById(R.id.etQuantity)
        etRatePerUnit = findViewById(R.id.etRatePerUnit)
        delete = findViewById(R.id.delete)
        update = findViewById(R.id.update)
        shimmer_frame_layout = findViewById(R.id.shimmer_frame_layout)
        llet = findViewById(R.id.llet)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Update Product")

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun loadDetails(id: String) {
        //get reference to ProductServiceBuilder
        val productService = ServiceBuilder.buildService(ProductService::class.java)
        val requestCall = productService.getAvailableProduct(id)

        //making network call in background using the enque function
        requestCall.enqueue(object : Callback<AvailableProductsList> {
            override fun onResponse(
                call: Call<AvailableProductsList>,
                response: Response<AvailableProductsList>
            ) {
                //  progressLayout.visibility = View.GONE
                shimmer_frame_layout.stopShimmer()
                shimmer_frame_layout.hideShimmer()
                shimmer_frame_layout.visibility = View.GONE
                llet.visibility = View.VISIBLE

                if (response.isSuccessful) {
                    product = response.body()!!
                    product.let {
                        etAddProduct.setText(product.productName)
                        etRatePerUnit.setText(product.costPerUnit!!.toString())
                        etQuantity.setText(product.totalQuantity!!.toString())

                    }
                } else {
                    Toast.makeText(
                        this@UpdateExistingProductActivity,
                        "Failed to retrieve details",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<AvailableProductsList>, t: Throwable) {
                Toast.makeText(
                    this@UpdateExistingProductActivity,
                    "Failed to retrieve details",
                    Toast.LENGTH_SHORT
                ).show()
            }

        })
    }

    private fun initUpdateButton(id: String) {

        update.setOnClickListener {

            if (etAddProduct.text.toString() != "" && etQuantity.text.toString() != "" && etRatePerUnit.text.toString() != "") {
                val updatedProduct = AvailableProductsList()
                updatedProduct.productName = etAddProduct.text.toString()
                updatedProduct.totalQuantity = etQuantity.text.toString().toInt()
                updatedProduct.costPerUnit = etRatePerUnit.text.toString().toInt()
                updatedProduct.qtyType=product.qtyType

                val productService = ServiceBuilder.buildService(ProductService::class.java)
                val requestCall = productService.updateAvailableProduct(id, updatedProduct)

                requestCall.enqueue(object : Callback<AvailableProductsList> {
                    override fun onResponse(
                        call: Call<AvailableProductsList>,
                        response: Response<AvailableProductsList>
                    ) {
                        if (response.isSuccessful) {
                            finish()
                            val updatedProductObject = response.body()
                            Toast.makeText(
                                this@UpdateExistingProductActivity,
                                "Item updated sucessfully",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            Toast.makeText(
                                this@UpdateExistingProductActivity,
                                "Error Occured",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<AvailableProductsList>, t: Throwable) {
                        Toast.makeText(
                            this@UpdateExistingProductActivity,
                            "Error Occured" + t.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })

            } else {
                Toast.makeText(
                    this@UpdateExistingProductActivity,
                    "Empty fields are not allowed",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun initDeleteButton(id: String) {

        delete.setOnClickListener {
            val productService = ServiceBuilder.buildService(ProductService::class.java)
            val requestCall = productService.deleteProduct(id)

            requestCall.enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        finish()
                        Toast.makeText(
                            this@UpdateExistingProductActivity,
                            "Item deleted sucessfully",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Toast.makeText(
                            this@UpdateExistingProductActivity,
                            "Error Ocuured",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Toast.makeText(
                        this@UpdateExistingProductActivity,
                        "Error Ocuured" + t.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                }

            })
        }
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
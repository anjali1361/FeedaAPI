package com.example.stockmaintenanceapp.activity

import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.stockmaintenanceapp.R
import com.example.stockmaintenanceapp.model.AvailableProductsList
import com.example.stockmaintenanceapp.services.ProductService
import com.example.stockmaintenanceapp.services.ServiceBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddNewProductAtivity : AppCompatActivity() {

    lateinit var etAddProduct: EditText
    lateinit var etQuantity: EditText
    lateinit var etRatePerUnit: EditText
    lateinit var exit: Button
    lateinit var save: Button
    lateinit var toolbar: Toolbar
    lateinit var spinner_quantity_unit:Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_product)

        intiView()
        saveNewProduct()

        exit.setOnClickListener {
            finish()
        }
    }

    private fun saveNewProduct() {
        save.setOnClickListener {
            if (etAddProduct.text.toString() != "" && etQuantity.text.toString() != "" && etRatePerUnit.text.toString() != "" && spinner_quantity_unit.selectedItemPosition != 0) {
                val newProduct = AvailableProductsList()
                newProduct.productName = etAddProduct.text.toString()
                newProduct.costPerUnit = etRatePerUnit.text.toString().toInt()
                newProduct.totalQuantity = etQuantity.text.toString().toInt()
                newProduct.qtyType=spinner_quantity_unit.selectedItem.toString()

                val product = ServiceBuilder.buildService(ProductService::class.java)
                val requestCall = product.addAvailableProduct(newProduct)

                requestCall.enqueue(object : Callback<AvailableProductsList> {
                    override fun onResponse(
                        call: Call<AvailableProductsList>,
                        response: Response<AvailableProductsList>
                    ) {
                        if (response.isSuccessful) {
                            finish()
                            var newlyAddedProduct = response.body()
                        } else {
                            Toast.makeText(
                                this@AddNewProductAtivity,
                                "Error adding details, fill all the data correctly",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                    }

                    override fun onFailure(call: Call<AvailableProductsList>, t: Throwable) {
                        Toast.makeText(
                            this@AddNewProductAtivity,
                            "Failed to retrieve details",
                            Toast.LENGTH_SHORT
                        ).show()

                    }

                })

            } else {
                Toast.makeText(this@AddNewProductAtivity, "Enter all fields", Toast.LENGTH_SHORT)
                    .show()
            }

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

    private fun intiView() {
        toolbar = findViewById(R.id.toolbar)
        etAddProduct = findViewById(R.id.etAddProduct)
        etQuantity = findViewById(R.id.etQuantity)
        etRatePerUnit = findViewById(R.id.etRatePerUnit)
        exit = findViewById(R.id.exit)
        save = findViewById(R.id.save)
        spinner_quantity_unit=findViewById(R.id.spinner_quantity_unit)

        val spinner_quantity_type_list =
            arrayListOf("Quantity", "Kg", "Packet", "Litre", "Quintal", "Dozen")

        val arrayAdapter =
            this.let {
                ArrayAdapter(
                    it,
                    android.R.layout.simple_spinner_item,
                    spinner_quantity_type_list
                )
            }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_quantity_unit.adapter = arrayAdapter

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Add Product")

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }
}
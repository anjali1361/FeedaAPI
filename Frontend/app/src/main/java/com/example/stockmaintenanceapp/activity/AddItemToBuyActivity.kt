package com.example.stockmaintenanceapp.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.size
import androidx.lifecycle.ViewModelProvider
import com.example.stockmaintenanceapp.R
import com.example.stockmaintenanceapp.adapter.AutoCompleteAdapter
import com.example.stockmaintenanceapp.model.*
import com.example.stockmaintenanceapp.repository.AvailableProductsRepository
import com.example.stockmaintenanceapp.services.CustomerAndSaleService
import com.example.stockmaintenanceapp.services.ServiceBuilder
import com.example.stockmaintenanceapp.viewmodel.AvailableProductViewModelFactory
import com.example.stockmaintenanceapp.viewmodel.AvailableProductsViewModel
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AddItemToBuyActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var layout_list: LinearLayout
    lateinit var autoCompleteTextView: AutoCompleteTextView
    lateinit var toolbar: Toolbar
    lateinit var btnSave: Button
    lateinit var progressBar: ProgressBar
    lateinit var btnTotal: Button
    lateinit var etTotal: TextView
    lateinit var etDue: EditText
    lateinit var etFare:EditText
    lateinit var productListObject: AvailableProducts
    lateinit var availableProductsViewModel: AvailableProductsViewModel
    val orderedItemList: ArrayList<OrderedItems> = arrayListOf()
    var productList = arrayListOf<AvailableProductsList>()
    var customerId: String? = null
    val customerSales = CustomerSales()
    var total: Int = 0
    lateinit var adapter: AutoCompleteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item_to_buy)

        initView()
        getCutstomerID()
        loadAvailableProducts()

        btnSave.setOnClickListener(this)
        btnTotal.setOnClickListener(this)
    }

    private fun setAdapter() {
        if (productList.size != 0) {
            Log.d("xyz", productList.toString())
            adapter = AutoCompleteAdapter(this, productList)
            autoCompleteTextView.setAdapter(adapter)

            autoCompleteTextView.setOnItemClickListener { adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->

                val selectedProduct: AvailableProductsList? = adapter.getItem(i)
                addViewManually(selectedProduct)
                Log.d("abc", selectedProduct.toString())
            }

        } else {
            Toast.makeText(this, "Product list is empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getCutstomerID() {
        val bundle: Bundle? = intent.extras
        if (bundle?.containsKey(CustomerDetailActivity.ARG_SALES_ID)!!) {

            customerId = intent.getStringExtra(CustomerDetailActivity.ARG_SALES_ID)
            Log.d("Gallery", customerId.toString())

            if (customerId != null) {
                Log.d("Gallery", customerId!!)
            } else {
                Log.d("Gallery", "Customer ID is NUll")
            }

        }
    }

    private fun loadAvailableProducts(
    ) {
        val availableProductsRepository= AvailableProductsRepository()
        val availableProductViewModelFactory= AvailableProductViewModelFactory(availableProductsRepository)
        availableProductsViewModel=
            ViewModelProvider(this,availableProductViewModelFactory)[AvailableProductsViewModel::class.java]
        availableProductsViewModel.getProduct()

        availableProductsViewModel.productMutableLiveData.observe(this, androidx.lifecycle.Observer {
            productListObject=it
            productList=productListObject.products as ArrayList<AvailableProductsList>

            setAdapter()
        })
    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbar)
        layout_list = findViewById(R.id.layout_list)
        btnTotal = findViewById(R.id.btnTotal)
        autoCompleteTextView = findViewById(R.id.autoCompleteTextView)
        btnSave = findViewById(R.id.btnSave)
        etTotal = findViewById(R.id.etTotal)
        etDue = findViewById(R.id.etDue)
        etFare=findViewById(R.id.etFare)
        progressBar = findViewById(R.id.progressBar)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Add Sales")

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun saveSalesDetails() {

        if (etDue.text.toString() != "" && etTotal.text.toString() != "" && etFare.text.toString()!="") {
            etTotal.setText(total.toString())
            customerSales.user = customerId
            customerSales.total = etTotal.text.toString().toInt()
            customerSales.orderItems = orderedItemList
            customerSales.due = etDue.text.toString().toInt()
            customerSales.fare=etFare.text.toString().toInt()
            Log.d("Gallery", customerSales.toString())

            val product = ServiceBuilder.buildService(CustomerAndSaleService::class.java)
            val requestCall = product.addCustomerSalesList(customerSales)

            requestCall.enqueue(object : Callback<CustomerSales> {
                override fun onResponse(
                    call: Call<CustomerSales>,
                    response: Response<CustomerSales>
                ) {
                    btnSave.visibility = View.VISIBLE
                    progressBar.visibility = View.INVISIBLE
                    if (response.isSuccessful) {
                        finish()
                        val newlyAddedProduct = response.body()
                        finish()
                    } else {
                        Toast.makeText(
                            this@AddItemToBuyActivity,
                            "Error adding details, fill all the data correctly",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

                override fun onFailure(call: Call<CustomerSales>, t: Throwable) {
                    Toast.makeText(
                        this@AddItemToBuyActivity,
                        "Failed to retrieve details",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            })
        } else {
            Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addItems() {
        Log.d("Gallery", layout_list.size.toString())
        orderedItemList.clear()
        for (i in 0 until layout_list.childCount) {
            val salesView = layout_list.getChildAt(i)
            // val autoCompleteTextView=salesView.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
            val etName: TextView = salesView.findViewById(R.id.etName)
            val etQuantity: EditText = salesView.findViewById(R.id.etQuantity)
            val etPrice: EditText = salesView.findViewById(R.id.etPrice)

            val orderedItems = OrderedItems()

            val productName = etName.text.toString()
            val quantity = etQuantity.text.toString().toInt()
            val price = etPrice.text.toString().toInt()

            orderedItems.productName = productName
            orderedItems.qty = quantity
            orderedItems.rate = price

            for (j in 0 until productList.size) {
                val product: AvailableProductsList = productList.get(j)
                if (product.productName?.let { productName.compareTo(it) } ==0)
                 {
                    orderedItems.productId = product._id
                    orderedItems.qtytype = product.qtyType
                    break
                }
            }

            orderedItemList.add(orderedItems)
            this.total += (orderedItems.qty!! * orderedItems.rate!!)
            Log.d("Gallery", orderedItems.toString())
            Log.d("Gallery", orderedItemList.toString())
        }

    }

    private fun addViewManually(
        selectedProduct: AvailableProductsList?,
    ) {
        val salesView = layoutInflater.inflate(R.layout.add_sale_item_row, null, false)

        val etName = salesView.findViewById(R.id.etName) as TextView
        val etPrice = salesView.findViewById(R.id.etPrice) as EditText
        val etQuantity = salesView.findViewById<EditText>(R.id.etQuantity)
        val imgclose = salesView.findViewById(R.id.imgclose) as ImageView

        imgclose.setOnClickListener {
            layout_list.removeView(salesView)
            if(layout_list.childCount==0){
                etTotal.setText(0.toString())
            }
            else{
                Toast.makeText(this,"click total button to update total amount",Toast.LENGTH_SHORT).show()
            }
        }

        layout_list.addView(salesView)

        etName.setText(selectedProduct?.productName)
        etPrice.setText(selectedProduct?.costPerUnit.toString())
        etQuantity.setText("1")

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.btnSave -> {
                if (productList.size != 0) saveSalesDetails() else Toast.makeText(
                    this,
                    "Enter products to buy",
                    Toast.LENGTH_SHORT
                ).show()
            }
            R.id.btnTotal -> {
                if (layout_list.childCount != 0) {
                    total = 0
                    addItems()
                    etTotal.visibility = View.VISIBLE
                    etTotal.setText(total.toString())
                } else {
                    Toast.makeText(this, "Enter products to buy", Toast.LENGTH_SHORT).show()
                }

            }
        }
    }

}
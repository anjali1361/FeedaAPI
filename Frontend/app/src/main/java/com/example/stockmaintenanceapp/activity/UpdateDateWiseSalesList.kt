package com.example.stockmaintenanceapp

import android.annotation.SuppressLint
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmaintenanceapp.activity.CustomerDetailActivity
import com.example.stockmaintenanceapp.adapter.AutoCompleteAdapter
import com.example.stockmaintenanceapp.adapter.UpdateDateWiseSalesAdapter
import com.example.stockmaintenanceapp.model.*
import com.example.stockmaintenanceapp.repository.AvailableProductsRepository
import com.example.stockmaintenanceapp.services.CustomerAndSaleService
import com.example.stockmaintenanceapp.services.ProductService
import com.example.stockmaintenanceapp.services.ServiceBuilder
import com.example.stockmaintenanceapp.viewmodel.AvailableProductViewModelFactory
import com.example.stockmaintenanceapp.viewmodel.AvailableProductsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class UpdateDateWiseSalesList : AppCompatActivity() {
    var dateWiseSales: DateWiseSales? = null
    var salesList: ArrayList<SalesInfo>? = null
    var customerId: String? = null
    lateinit var toolbar: Toolbar
    lateinit var btnTotal: Button
    lateinit var productListObject: AvailableProducts
    lateinit var availableProductsViewModel: AvailableProductsViewModel
    var productList = arrayListOf<AvailableProductsList>()
    lateinit var updateDateWiseListRecyclerView: RecyclerView
    lateinit var updateDateWiseSalesAdapter: UpdateDateWiseSalesAdapter
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var etDue: EditText
    lateinit var etFare: EditText

    lateinit var fabUpdateDateWiseSales: FloatingActionButton
    lateinit var btnDelete: Button
    lateinit var btnUpdate: Button
    lateinit var adapter: AutoCompleteAdapter

    val pattern = "dd-MM-yyyy"
    val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
    val dateToday = simpleDateFormat.format(Date())

    var total = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_date_wise_sales_list)

        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        initView()
        loadAndValidateProducts()
        getDateWiseSalesListToBeUpdated()
        setUpRecyclerView()

        fabUpdateDateWiseSales.setOnClickListener {
            openDialog()
        }

        btnTotal.setOnClickListener {
            total = 0
            if (salesList?.size != 0) {
                for (i in 0..salesList!!.size - 1) {
                    val salesInfo: SalesInfo = salesList!!.get(i)
                    total += salesInfo.qty!! * salesInfo.rate!!
                }
                btnTotal.setText(total.toString())
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun openDialog() {
        val builder = AlertDialog.Builder(this)
        val inflater = LayoutInflater.from(this)
        builder.setTitle("Add Products")
        val layoutView: View = inflater.inflate(R.layout.date_wise_sales_list_item_dialog, null)
        builder.setView(layoutView)

        val autoCompleteTextView: AutoCompleteTextView =
            layoutView.findViewById(R.id.autoCompleteTextView)
        val etProductName: TextView = layoutView.findViewById(R.id.etProductName)
        val etProductQuantity: EditText = layoutView.findViewById(R.id.etProductQuantity)
        val etProductRate: EditText = layoutView.findViewById(R.id.etProductRate)

//        val selectedProduct=setAdapter(autoCompleteTextView)

        var selectedProduct:AvailableProductsList?=null
        if (productList.size != 0) {
            Log.d("xyz", productList.toString())
            adapter = AutoCompleteAdapter(this, productList)
            autoCompleteTextView.setAdapter(adapter)


            autoCompleteTextView.setOnItemClickListener { adapterView: AdapterView<*>, view1: View, i: Int, l: Long ->
                selectedProduct = adapter.getItem(i)
                etProductName.setText(selectedProduct?.productName.toString())
                etProductRate.setText(selectedProduct?.costPerUnit.toString())
                etProductQuantity.setText("1")
                Log.d("abc", selectedProduct.toString())
            }
        } else {
            Toast.makeText(this, "Product list is empty", Toast.LENGTH_SHORT).show()
        }

        builder.setPositiveButton("Ok") { dialog, which -> // get the edit text values here and pass them back via the listener

            val salesInfo = SalesInfo()
            val productName = etProductName.text.toString()
            val qty = etProductQuantity.text.toString()
            val rate = etProductRate.text.toString()

            if (productName != "" && qty != "" && rate != "") {

                for (j in 0 until productList.size) {
                    val product: AvailableProductsList = productList.get(j)
                    if (product.productName?.compareTo(productName, true) == 0) {
                        salesInfo.productId = product._id
                        salesInfo.productName = productName
                        salesInfo.rate = rate.toInt()
                        salesInfo.qty = qty.toInt()
                        salesInfo.qtytype = product.qtyType
                        salesList?.add(salesInfo)
                    }

                }
            } else {
                Toast.makeText(this, "Empty field are not allowed", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

        }
        builder.setNeutralButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun loadAndValidateProducts(
    ) {

        val availableProductsRepository= AvailableProductsRepository()
        val availableProductViewModelFactory= AvailableProductViewModelFactory(availableProductsRepository)
        availableProductsViewModel=
            ViewModelProvider(this,availableProductViewModelFactory)[AvailableProductsViewModel::class.java]
        availableProductsViewModel.getProduct()

        availableProductsViewModel.productMutableLiveData.observe(this, androidx.lifecycle.Observer {
            productListObject=it
            productList=productListObject.products as ArrayList<AvailableProductsList>
        })
    }

    private fun setUpRecyclerView() {

        dateWiseSales?.total?.let { btnTotal.setText(it.toString()) }
        dateWiseSales?.due?.let { etDue.setText(it.toString()) }
        dateWiseSales?.fare?.let { etFare.setText(it.toString()) }

        updateDateWiseSalesAdapter = UpdateDateWiseSalesAdapter(
            this,
            salesList!!
        )
        updateDateWiseListRecyclerView.adapter = updateDateWiseSalesAdapter
        updateDateWiseListRecyclerView.layoutManager = layoutManager
    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbar)
        etDue = findViewById(R.id.etDue)
        etFare = findViewById(R.id.etFare)
        btnTotal = findViewById(R.id.btnTotal)
        updateDateWiseListRecyclerView = findViewById(R.id.updateDateWiseSalesRecyclerView)
        layoutManager = LinearLayoutManager(this)
        fabUpdateDateWiseSales = findViewById(R.id.fabUpdateDateWiseSales)
        btnDelete = findViewById(R.id.btnDelete)
        btnUpdate = findViewById(R.id.btnUpdate)
//        etTotal = findViewById(R.id.etTotal)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Update Sales")

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getDateWiseSalesListToBeUpdated() {

        val bundle: Bundle? = intent.extras
        if (bundle?.containsKey("parentInfo")!!) {

            customerId = intent.getStringExtra("customerId")
            dateWiseSales = intent.getParcelableExtra("parentInfo")

            if (dateWiseSales != null && customerId != null) {
                Log.d("UpdateDateWiseSales", dateWiseSales.toString())
                customerId?.let { Log.d("UpdateDateWiseSales", it) }

                salesList = dateWiseSales?.sales?.orderItems
                Log.d("UpdateDateWiseSales", salesList.toString())
                initUpdateButton(customerId!!)
                initDeleteButton(customerId!!)
            }

        }
    }

    private fun initDeleteButton(customerId: String) {
        btnDelete.setOnClickListener {
            val productService = ServiceBuilder.buildService(CustomerAndSaleService::class.java)
            val requestCall = dateWiseSales?._id?.let { it1 ->
                productService.deleteProductToBeBought(
                    it1
                )
            }
            requestCall?.enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        finish()
                        Toast.makeText(
                            this@UpdateDateWiseSalesList,
                            "Item deleted sucessfully",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Toast.makeText(
                            this@UpdateDateWiseSalesList,
                            "Error Ocuured",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Toast.makeText(
                        this@UpdateDateWiseSalesList,
                        "Error Ocuured" + t.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                }
            })
        }
    }

    private fun initUpdateButton(customerId: String) {
        btnUpdate.setOnClickListener {

            if (etDue.text.toString() != "" && btnTotal.text.toString() != "" && etFare.text.toString()!="") {
                val customerSales = CustomerSales()
                customerSales.user = customerId
                customerSales.orderItems = salesList as java.util.ArrayList<OrderedItems>
                customerSales.due = etDue.text.toString().toInt()
                customerSales.fare=etFare.text.toString().toInt()
                customerSales.total = btnTotal.text.toString().toInt()

                Log.d("UpdateDateWiseSales", customerSales.toString())

                val productService = ServiceBuilder.buildService(CustomerAndSaleService::class.java)
                val requestCall = dateWiseSales?._id?.let { it1 ->
                    productService.updateProductToBeBought(
                        it1, customerSales
                    )
                }

                requestCall?.enqueue(object : Callback<CustomerSales> {
                    override fun onResponse(
                        call: Call<CustomerSales>,
                        response: Response<CustomerSales>
                    ) {
                        if (response.isSuccessful) {
                            finish()
                            val updatedProductObject = response.body()
                            Toast.makeText(
                                this@UpdateDateWiseSalesList,
                                "Item updated sucessfully",
                                Toast.LENGTH_SHORT
                            ).show()

                        } else {
                            Toast.makeText(
                                this@UpdateDateWiseSalesList,
                                "Error Occured",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<CustomerSales>, t: Throwable) {
                        Toast.makeText(
                            this@UpdateDateWiseSalesList,
                            "Error Occured" + t.toString(),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
            } else {
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

package com.example.stockmaintenanceapp.activity

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.stockmaintenanceapp.R
import com.example.stockmaintenanceapp.UpdateDateWiseSalesList
import com.example.stockmaintenanceapp.adapter.CustomerDetailAdapter
import com.example.stockmaintenanceapp.model.*
import com.example.stockmaintenanceapp.repository.CustomerDetailRepository
import com.example.stockmaintenanceapp.services.CustomerAndSaleService
import com.example.stockmaintenanceapp.services.ServiceBuilder
import com.example.stockmaintenanceapp.util.ConnectionManager
import com.example.stockmaintenanceapp.viewmodel.CustomerDetailViewModel
import com.example.stockmaintenanceapp.viewmodel.CustomerDetailViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CustomerDetailActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var customerRecyclerAdapter: CustomerDetailAdapter
    lateinit var expandableListView: ExpandableListView
    lateinit var toolbar: Toolbar
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    lateinit var fabCustomerDetail: FloatingActionButton
    lateinit var customerDetailViewModel: CustomerDetailViewModel
    lateinit var btnInvoice: Button
    lateinit var btnRepeat: Button
    lateinit var etDate: EditText
    lateinit var btnAll:LinearLayout
    lateinit var textInput:TextInputLayout

    lateinit var customerDetailList: ArrayList<CustomerDetail>
    lateinit var individualCustomer: CustomerDetail
    var dateWiseSales: DateWiseSales = DateWiseSales()
    lateinit var allSales: ArrayList<DateWiseSales>
    lateinit var customer: Customers

//    val pattern = "dd-MM-yyyy"
//    val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
//    val date = simpleDateFormat.format(Date())

    var outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    var inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US)
    var total_due=0;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_detail)

        initView()
        getCustomerDetails()
        loadDetails()

        btnInvoice.setOnClickListener(this)
        btnRepeat.setOnClickListener(this)
        fabCustomerDetail.setOnClickListener(this)

        expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
            val parentInfo = allSales.get(groupPosition)
            var childInfo = parentInfo.sales?.orderItems?.get(childPosition)

            val intent=Intent(this,UpdateDateWiseSalesList::class.java)
            intent.putExtra("customerId",customer._id)
            intent.putExtra("parentInfo",parentInfo)
            startActivity(intent)

            //Toast.makeText(this, "Child Item Selected", Toast.LENGTH_SHORT).show()
            return@setOnChildClickListener false
        }

        expandableListView.setOnGroupClickListener { parent, v, groupPosition, id ->
            var parentInfo = allSales.get(groupPosition)
            //Toast.makeText(this, "Parent Item Selected", Toast.LENGTH_SHORT).show()
            return@setOnGroupClickListener false
        }
    }

    private fun getCustomerDetails() {

        val bundle: Bundle? = intent.extras
        if (bundle?.containsKey(CustomerDetailActivity.ARG_SALES_ID)!!) {

            customer= intent.getParcelableExtra<Customers>(CustomerDetailActivity.ARG_SALES_ID)!!
            Log.d("Detail", customer.toString())

            Log.d("Detail", "debug: launching job1: ${Thread.currentThread().name}")
            loadDetails()

        }
    }

    private fun createInvoiceAndRepeat(item: Int) {

        total_due=0
        loadDataForInvoiceAndRepeat(item)

    }

    private fun loadDataForInvoiceAndRepeat(item: Int) {

        val etDate = etDate.text.toString()
        for (i in 0..allSales.size - 1) {
            val dateFormatted =
                inputFormat.parse(allSales.get(i).updatedAt!!)
            val strDate = outputFormat.format(dateFormatted)
            Log.d("Detail", etDate + " " + date)
            total_due+= allSales.get(i).due!!
            if (etDate == strDate) {
                Log.d("Detail", etDate + " " + strDate)
                dateWiseSales = allSales.get(i)

                //  Log.d("Detail", dateWiseSales.toString())
                total_due-= (allSales.get(i).due!!)

                if (item == R.id.btnInvoice) {
                    createPDF(dateWiseSales,individualCustomer)
                }
                if (item == R.id.btnRepeat) {
                    saveSalesDetails(dateWiseSales)
                }
            } else {
                Toast.makeText(this@CustomerDetailActivity,"Date not matched, format must be same",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createPDF(dateWiseSales: DateWiseSales, individualCustomerInvoice: CustomerDetail) {
        var totalBags=0
        val salesOfADay=dateWiseSales.sales?.orderItems
        if (salesOfADay != null) {
            for(i in 0..(salesOfADay.size.minus(1))){
                totalBags+= salesOfADay.get(i).qty!!
            }
        }
        val myPdfDocument = PdfDocument()
        val paint = Paint()

        val myPageInfo = PdfDocument.PageInfo.Builder(1200, 2010, 1).create()
        val myPage1 = myPdfDocument.startPage(myPageInfo)
        val canva = myPage1.canvas

        paint.textSize = 80F
        canva.drawText("Company Name", 30F, 80F, paint)

        paint.textSize = 30F
        canva.drawText("Address of Company", 30F, 130F, paint)

        paint.textAlign = Paint.Align.RIGHT
        canva.drawText("Invoice Date", (canva.width - 40).toFloat(), 40F, paint)
        canva.drawText(date, (canva.width - 40).toFloat(), 80F, paint)
        paint.textAlign = Paint.Align.LEFT

        paint.setColor(Color.rgb(150, 150, 150))
        canva.drawRect(30F, 160F, (canva.width - 30).toFloat(), 160F, paint)//first line

        paint.setColor(Color.rgb(150, 150, 150))
        canva.drawRect(30f, 250f, 250f, 300f, paint)//second broad line

        paint.setColor(Color.WHITE)
        canva.drawText("Bill To:", 50F, 285F, paint)

        paint.setColor(Color.BLACK)
        canva.drawText("Customer Name", 30F, 350F, paint)
        individualCustomerInvoice.name.let {
            if (it != null) {
                canva.drawText(it, 280f, 350f, paint)
            }
        }

        canva.drawText("Phone#", 680f, 350f, paint)
        paint.textAlign = Paint.Align.RIGHT
        canva.drawText(customer.number, (canva.width - 40).toFloat(), 350f, paint)
        paint.textAlign = Paint.Align.LEFT

        paint.setColor(Color.rgb(150, 150, 150))
        canva.drawRect(30f, 400f, (canva.width - 30).toFloat(), 450f, paint)

        paint.setColor(Color.WHITE)
        canva.drawText("Item", 50f, 435f, paint)
        canva.drawText("Qty", 550f, 435f, paint)
        paint.textAlign = Paint.Align.RIGHT
        canva.drawText("Amount", (canva.width - 40).toFloat(), 435f, paint)
        paint.textAlign = Paint.Align.LEFT

        paint.setColor(Color.BLACK)
        if (dateWiseSales.sales!!.orderItems?.size != 0) {
            for (i in 0..dateWiseSales.sales!!.orderItems?.size!! - 1) {
                dateWiseSales.sales!!.orderItems?.get(i)?.productName?.let {
                    canva.drawText(
                        it,
                        50f,
                        (480 + (i * 45)).toFloat(),
                        paint
                    )
                }
                canva.drawText(
                    dateWiseSales.sales!!.orderItems?.get(i)?.qty.toString(),
                    550f,
                    (480 + (i * 45)).toFloat(),
                    paint
                )
                paint.textAlign = Paint.Align.RIGHT
                canva.drawText(
                    (dateWiseSales.sales!!.orderItems?.get(
                            i
                        )?.rate!!)

                        .toString(), (canva.width - 40).toFloat(), (480 + (i * 45)).toFloat(), paint
                )
                paint.textAlign = Paint.Align.LEFT
            }
        } else {
            Log.d("Detail", "No Products Bought")
        }

        paint.setColor(Color.BLACK)
        canva.drawText(
            "Total Bags",
            550f,
            ((550 + (dateWiseSales.sales!!.orderItems?.size?.times(45)!!)).toFloat()),
            paint
        )
        canva.drawText(
            "Today's Total",
            550f,
            ((590 + (dateWiseSales.sales!!.orderItems?.size?.times(45)!!)).toFloat()),
            paint
        )
        canva.drawText(
            "Previous Due",
            550f,
            ((630 + (dateWiseSales.sales!!.orderItems?.size?.times(45)!!)).toFloat()),
            paint
        )
        canva.drawText(
            "Fare Price",
            550f,
            ((670 + (dateWiseSales.sales!!.orderItems?.size?.times(45)!!)).toFloat()),
            paint
        )
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
        canva.drawText(
            "All TOTAL",
            550f,
            ((710 + (dateWiseSales.sales!!.orderItems?.size?.times(45)!!)).toFloat()),
            paint
        )
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))

        paint.textAlign = Paint.Align.RIGHT
        canva.drawText(
            totalBags.toString()+" kg",
            970f,
            ((550 + (dateWiseSales.sales!!.orderItems?.size?.times(45)!!)).toFloat()),
            paint
        )
        canva.drawText(
            ((dateWiseSales.total!!-dateWiseSales.due!!).toString()),
            970f,
            ((590 + (dateWiseSales.sales!!.orderItems?.size?.times(45)!!)).toFloat()),
            paint
        )
        canva.drawText(
            total_due.toString(),
            970f,
            ((630 + (dateWiseSales.sales!!.orderItems?.size?.times(45)!!)).toFloat()),
            paint
        )
        canva.drawText(
            dateWiseSales.fare.toString(),
            970f,
            ((670 + (dateWiseSales.sales!!.orderItems?.size?.times(45)!!)).toFloat()),
            paint
        )
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
        canva.drawText(
            (((dateWiseSales.total- dateWiseSales.due!!).plus(total_due + dateWiseSales.fare!!)).toString()),
            970f,
            ((710 + (dateWiseSales.sales!!.orderItems?.size?.times(45)!!)).toFloat()),
            paint
        )

        paint.textAlign = Paint.Align.LEFT
        canva.drawText(
            "Make all checks payable to \"Company Name\"",
            30f,
            ((790 + (dateWiseSales.sales!!.orderItems?.size?.times(45)!!)).toFloat()),
            paint
        )
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL))

        canva.drawText(
            "Thank you so much, Come back again",
            30f,
            ((830 + (dateWiseSales.sales!!.orderItems?.size?.times(45)!!)).toFloat()),
            paint
        )
        myPdfDocument.finishPage(myPage1)

        val file = File(this.getExternalFilesDir("/"), "Invoice on "+date + " Company Name.pdf")

        try {
            Log.d("Invoice", file.toString())
            myPdfDocument.writeTo(FileOutputStream(file))
           // Toast.makeText(this, "Invoice Generated Sucessfully", Toast.LENGTH_SHORT).show()
            etDate.setText("")
            viewPDF(file.toString())

        } catch (e: Exception) {
            e.printStackTrace()
        }

        myPdfDocument.close()
    }

    private fun viewPDF(file: String) {

        val intent=Intent(this,ViewPDFActivity::class.java)
        intent.putExtra("filePath",file)
        startActivity(intent)
    }

    private fun loadDetails() {
        if (ConnectionManager().checkConnectivity(this)) {

            val customerDetailRepository=CustomerDetailRepository()
            val customerDetailViewModelFactory= CustomerDetailViewModelFactory(customerDetailRepository)
            customerDetailViewModel=
                ViewModelProvider(this,customerDetailViewModelFactory)[CustomerDetailViewModel::class.java]
            customerDetailViewModel.getCustomerDetail()

            customerDetailViewModel.customerdetailMutableLiveData.observe(this, androidx.lifecycle.Observer {
                customerDetailList= it as ArrayList<CustomerDetail>

                for (i in 0..customerDetailList.size - 1) {
                    Log.d("Detail", customerDetailList.size.toString())
                    individualCustomer = customerDetailList.get(i)
                    if (individualCustomer._id == customer._id) {
                        allSales = individualCustomer.allSales!!
                        break
                    }
                }

                Log.d("Detail", allSales.toString())
                customerRecyclerAdapter= CustomerDetailAdapter(this@CustomerDetailActivity,allSales)
                expandableListView.setAdapter(customerRecyclerAdapter)

            }
            )

//            progressLayout.visibility=View.INVISIBLE
        }
        else {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this)
            }
            dialog.create()
            dialog.show()
        }
    }

    private fun saveSalesDetails(dateWiseSales: DateWiseSales) {

        val customerSales = CustomerSales()
        customerSales.user = customer._id
        customerSales.total = dateWiseSales.total
        customerSales.orderItems = dateWiseSales.sales?.orderItems as ArrayList<OrderedItems>
        customerSales.due = dateWiseSales.due
        //    Log.d("Gallery", customerSales.toString())
        val product = ServiceBuilder.buildService(CustomerAndSaleService::class.java)
        val requestCall = product.addCustomerSalesList(customerSales)

        requestCall.enqueue(object : Callback<CustomerSales> {
            override fun onResponse(
                call: Call<CustomerSales>,
                response: Response<CustomerSales>
            ) {
                if (response.isSuccessful) {
                    val newlyAddedProduct = response.body()
                    Toast.makeText(
                        this@CustomerDetailActivity,
                        "Order Repeated Sucessfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    onResume()
                }
            }

            override fun onFailure(call: Call<CustomerSales>, t: Throwable) {
                Toast.makeText(
                    this@CustomerDetailActivity,
                    "Failed to retrieve details",
                    Toast.LENGTH_SHORT
                ).show()

            }

        })
    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbar)
        expandableListView = findViewById(R.id.expandableListView)
        fabCustomerDetail = findViewById(R.id.fabCustomerDetail)
        btnAll=findViewById(R.id.btnAll)
        textInput=findViewById(R.id.textInput)
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        btnInvoice = findViewById(R.id.btnInvoice)
        btnRepeat = findViewById(R.id.btnRepeat)
        etDate = findViewById(R.id.etDate)

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Customer Detail")

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }

        if(id==R.id.updateCustomer){

            val builder = AlertDialog.Builder(this)
            val inflater = LayoutInflater.from(this)
            builder.setTitle("Update Customer")
            val layoutView: View = inflater.inflate(R.layout.add_customers_dialog, null)
            builder.setView(layoutView)

            val etCustomerNmae: EditText = layoutView.findViewById(R.id.etCustomerName)
            val etCustomerNumber: EditText = layoutView.findViewById(R.id.etCustomerNumber)
            val etCustomerAddress: EditText = layoutView.findViewById(R.id.etCustomerAddress)

            etCustomerNmae.setText(customer.name)
            etCustomerNumber.setText(customer.number)
            etCustomerAddress.setText(customer.address)

            builder.setPositiveButton("Ok") { dialog, which -> // get the edit text values here and pass them back via the listener

                if (etCustomerNmae.text.toString() != "" && etCustomerNumber.text.toString() != "" && etCustomerAddress.text.toString() != "") {
                    val updatedCustomers=Customers()
                    updatedCustomers.name=etCustomerNmae.text.toString()
                    updatedCustomers.number=etCustomerNumber.text.toString()
                    updatedCustomers.address=etCustomerAddress.text.toString()

                    val productService = ServiceBuilder.buildService(CustomerAndSaleService::class.java)
                    val requestCall =
                        customer._id?.let { productService.updateCustomerDetails(it,updatedCustomers) }

                    requestCall?.enqueue(object : Callback<Customers> {
                        override fun onResponse(
                            call: Call<Customers>,
                            response: Response<Customers>
                        ) {
                            if (response.isSuccessful) {
                                finish()
                                val updatedProductObject = response.body()
                                Toast.makeText(
                                    this@CustomerDetailActivity,
                                    "Item updated sucessfully",
                                    Toast.LENGTH_SHORT
                                ).show()

                            } else {
                                Toast.makeText(
                                    this@CustomerDetailActivity,
                                    "Error Occured",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<Customers>, t: Throwable) {
                            Toast.makeText(
                                this@CustomerDetailActivity,
                                "Error Occured" + t.toString(),
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    })

                } else {
                    Toast.makeText(
                        this@CustomerDetailActivity,
                        "Empty fields are not allowed",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            builder.setNeutralButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.setCancelable(false)
            dialog.show()
        }
        if(id==R.id.deleteCustomer){
            val productService = ServiceBuilder.buildService(CustomerAndSaleService::class.java)
            val requestCall = customer._id?.let { productService.deleteCustomer(it) }

            requestCall?.enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        finish()
                        Toast.makeText(this@CustomerDetailActivity,
                            "Customer deleted sucessfully",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Toast.makeText(
                            this@CustomerDetailActivity,
                            "Error Ocuured",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Toast.makeText(
                        this@CustomerDetailActivity,
                        "Error Ocuured" + t.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                }

            })
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        loadDetails()
    }

    companion object {

        const val ARG_SALES_ID = "sales_id"
    }

    override fun onClick(v: View?) {
        val item = v?.id
        when (item) {
            R.id.btnInvoice, R.id.btnRepeat -> {
                if(!etDate.text.toString().equals("")){
                    createInvoiceAndRepeat(item)
                }
                else {
                    Toast.makeText(
                        this@CustomerDetailActivity,
                        "Enter Date",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            R.id.fabCustomerDetail -> {
                val intent = Intent(this, AddItemToBuyActivity::class.java)
                intent.putExtra(ARG_SALES_ID, customer._id)
                startActivity(intent)
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflator=menuInflater
        inflator.inflate(R.menu.main,menu)
        return true

    }

}
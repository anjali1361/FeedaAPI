package com.example.stockmaintenanceapp.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmaintenanceapp.R
import com.example.stockmaintenanceapp.adapter.ReportCardAdapter
import com.example.stockmaintenanceapp.model.BalanceSheetModel
import com.example.stockmaintenanceapp.model.Comment
import com.example.stockmaintenanceapp.repository.BalanceSheetRepository
import com.example.stockmaintenanceapp.services.BalanceSheetService
import com.example.stockmaintenanceapp.services.ServiceBuilder
import com.example.stockmaintenanceapp.util.ConnectionManager
import com.example.stockmaintenanceapp.viewmodel.BalanceSheetViewModel
import com.example.stockmaintenanceapp.viewmodel.BalanceSheetViewModelFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ReportCardActivity : AppCompatActivity() {

    lateinit var toolbar: Toolbar
    lateinit var recyclerReportCard:RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var reportCardAdapter:ReportCardAdapter
    lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    lateinit var balanceSheetViewModel: BalanceSheetViewModel
    lateinit var balanceSheetList: ArrayList<BalanceSheetModel>
    lateinit var balanceSheet: BalanceSheetModel
    lateinit var commentList:ArrayList<Comment>
//    lateinit var commentListFiltered:ArrayList<Comment>
    var date:String?=null
    var id:String?=null

    var outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    @RequiresApi(Build.VERSION_CODES.N)
    var inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_card)

        getDate()
        initView()
        loadData()
    }

    private fun getDate() {

        val bundle: Bundle? = intent.extras
        if (bundle?.containsKey(ARG_ITEM_ID)!!) {
            balanceSheet = intent.getParcelableExtra(ARG_ITEM_ID)!!
            val inputDate=inputFormat.parse(balanceSheet.createdAt!!)
            date=outputFormat.format(inputDate!!)
            Log.d("Balance", date.toString())
            if (date != null) {
                Log.d("Balance", date!!)
            } else {
                Log.d("Balance", "Customer ID is NUll")
            }

        }
    }

    private fun loadData() {
        if (ConnectionManager().checkConnectivity(this)) {

            val balanceSheetRepository= BalanceSheetRepository()
            val balanceSheetViewModelFactory= BalanceSheetViewModelFactory(balanceSheetRepository)
            balanceSheetViewModel=
                ViewModelProvider(this,balanceSheetViewModelFactory)[BalanceSheetViewModel::class.java]
            balanceSheetViewModel.getProduct()

            balanceSheetViewModel.balanceSheetMutableLiveData.observe(this, androidx.lifecycle.Observer {
                balanceSheetList= it

                for(i in 0..balanceSheetList.size-1){
                    val dateTodayFromList=inputFormat.parse(balanceSheetList.get(i).createdAt!!)
                    val dateFormatted=outputFormat.format(dateTodayFromList!!)
                    if(date==dateFormatted){
                        commentList= balanceSheetList.get(i).Comments!!
                        for (comment in commentList){
                            if(comment==null){
                                commentList.remove(comment)
                            }
                        }
                        id=balanceSheetList.get(i)._id
                        Log.d("ReportCard",commentList.toString())
                        break
                    }
                }
                Log.d("comment",commentList.toString())
                reportCardAdapter =
                    ReportCardAdapter(this@ReportCardActivity, commentList)
                recyclerReportCard.adapter = reportCardAdapter
                recyclerReportCard.layoutManager = layoutManager
                progressLayout.visibility=View.INVISIBLE
                recyclerReportCard.visibility=View.VISIBLE
            }
            )
        } else {
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
            return true
        }
        if(id==R.id.action_delete){
            deleteReport()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteReport() {
        val productService = ServiceBuilder.buildService(BalanceSheetService::class.java)
        val requestCall = id?.let { productService.deleteBalanceSheetItem(it) }

        if (requestCall != null) {
            requestCall.enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        finish()
                        Toast.makeText(
                            this@ReportCardActivity,
                            "Item deleted sucessfully",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else {
                        Toast.makeText(
                            this@ReportCardActivity,
                            "Error Ocuured",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Toast.makeText(
                        this@ReportCardActivity,
                        "Error Ocuured" + t.toString(),
                        Toast.LENGTH_SHORT
                    ).show()

                }

            })
        }
    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbar)
        recyclerReportCard=findViewById(R.id.recyclerReportCard)
        reportCardAdapter= ReportCardAdapter(this,ArrayList())
        recyclerReportCard.apply {
            layoutManager = LinearLayoutManager(context)
            adapter=reportCardAdapter
        }
        layoutManager=LinearLayoutManager(this)
        progressLayout=findViewById(R.id.progressLayout)
        progressBar=findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE

        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Report Card")

        // Show the Up button in the action bar.
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflator=menuInflater
        inflator.inflate(R.menu.menu_delete,menu)
        return true

    }


    companion object {

        const val ARG_ITEM_ID = "item_id"
    }
}
package com.example.stockmaintenanceapp.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmaintenanceapp.R
import com.example.stockmaintenanceapp.adapter.CustomerRecyclerAdapter
import com.example.stockmaintenanceapp.model.Customers
import com.example.stockmaintenanceapp.repository.CustomerAndSalesRepository
import com.example.stockmaintenanceapp.services.CustomerAndSaleService
import com.example.stockmaintenanceapp.services.ServiceBuilder
import com.example.stockmaintenanceapp.util.ConnectionManager
import com.example.stockmaintenanceapp.viewmodel.CustomerAndSalesViewModelFactory
import com.example.stockmaintenanceapp.viewmodel.CustomerAndSalesViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


class GalleryFragment : Fragment() {

    var mContext: Context? = null
    lateinit var fabCustomer: FloatingActionButton
    lateinit var recyclerCustomer: RecyclerView
    lateinit var customerAndSalesViewModel: CustomerAndSalesViewModel
    lateinit var recyclerAdapter: CustomerRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    var customerList = arrayListOf<Customers>()

    var ratingComparator = Comparator<Customers> { customer1, customer2 ->
        customer2.name?.let { customer1.name?.compareTo(it, true) }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)

        initView(root)
        loadDestinations()

        fabCustomer.setOnClickListener { view ->

            openAlertDialog()
        }
        return root
    }

    private fun openAlertDialog() {

        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        builder.setTitle("Add Customer")
        val layoutView: View = inflater.inflate(R.layout.add_customers_dialog, null)
        builder.setView(layoutView)
        builder.setPositiveButton("Ok") { dialog, which -> // get the edit text values here and pass them back via the listener

            val etCustomerNmae: EditText = layoutView.findViewById(R.id.etCustomerName)
            val etCustomerNumber: EditText = layoutView.findViewById(R.id.etCustomerNumber)
            val etCustomerAddress: EditText = layoutView.findViewById(R.id.etCustomerAddress)
            val customer = etCustomerNmae.text.toString()
            val mobile =etCustomerNumber.text.toString()
            val add=etCustomerAddress.text.toString()
            if (customer != "") {
                addCustomer(customer,mobile,add)
            } else {
                Toast.makeText(context, "Enter customer name", Toast.LENGTH_SHORT).show()
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

    private fun addCustomer(customer: String, mobile: String, add: String) {

        val newCustomer = Customers()
        newCustomer.name = customer
        newCustomer.number=mobile
        newCustomer.address=add

        val product = ServiceBuilder.buildService(CustomerAndSaleService::class.java)
        val requestCall = product.addNewCustomer(newCustomer)

        requestCall.enqueue(object : Callback<Customers> {
            override fun onResponse(
                call: Call<Customers>,
                response: Response<Customers>
            ) {
                if (response.isSuccessful) {
                    var newlyAddedProduct = response.body()
                    loadDestinations()
                } else {
                    Toast.makeText(
                        context,
                        "Error adding details, fill all the data correctly",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

            override fun onFailure(call: Call<Customers>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Failed to retrieve details",
                    Toast.LENGTH_SHORT
                ).show()

            }

        })
    }

    private fun loadDestinations() {
        if (ConnectionManager().checkConnectivity(mContext!!)) {

            val customerAndSalesRepository= CustomerAndSalesRepository()
            val customerAndSalesViewModelFactory= CustomerAndSalesViewModelFactory(customerAndSalesRepository)
            customerAndSalesViewModel=
                ViewModelProvider(this,customerAndSalesViewModelFactory)[CustomerAndSalesViewModel::class.java]
            customerAndSalesViewModel.getCustomerAndSales()

            customerAndSalesViewModel.customerAndSalesMutableLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                customerList= it as ArrayList<Customers>
                recyclerAdapter.setData(customerList)
                progressLayout.visibility=View.GONE
                recyclerCustomer.visibility=View.VISIBLE
                fabCustomer.visibility=View.VISIBLE
            }
            )

        } else {
            val dialog = AlertDialog.Builder(mContext!!)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }

            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }
    }

    private fun initView(root: View) {
        fabCustomer = root.findViewById(R.id.fabCustomer)
        progressLayout = root.findViewById(R.id.progressLayout)
        progressBar = root.findViewById(R.id.progressBar)
        recyclerCustomer = root.findViewById(R.id.recyclerCustomer)//initialisation of recycler
        recyclerAdapter= CustomerRecyclerAdapter(mContext!!,ArrayList())
        recyclerCustomer.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter=recyclerAdapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.sort) {
            Collections.sort(customerList, ratingComparator)
        }

        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        loadDestinations()
    }

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onDetach() {
        mContext = null
        super.onDetach()
    }
}
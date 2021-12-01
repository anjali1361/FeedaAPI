package com.example.stockmaintenanceapp.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmaintenanceapp.R
import com.example.stockmaintenanceapp.activity.AddNewProductAtivity
import com.example.stockmaintenanceapp.adapter.HomeRecyclerAdapter
import com.example.stockmaintenanceapp.model.AvailableProducts
import com.example.stockmaintenanceapp.model.AvailableProductsList
import com.example.stockmaintenanceapp.repository.AvailableProductsRepository
import com.example.stockmaintenanceapp.services.ProductService
import com.example.stockmaintenanceapp.services.ServiceBuilder
import com.example.stockmaintenanceapp.util.ConnectionManager
import com.example.stockmaintenanceapp.viewmodel.AvailableProductViewModelFactory
import com.example.stockmaintenanceapp.viewmodel.AvailableProductsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    var mContext: Context? = null
    lateinit var recyclerHome: RecyclerView
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var fabHome: FloatingActionButton
    lateinit var productListObject: AvailableProducts
    lateinit var availableProductsViewModel: AvailableProductsViewModel
    lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    var productList = arrayListOf<AvailableProductsList>()

    var ratingComparator = Comparator<AvailableProductsList> { product1, product2 ->

        // sort according to name if rating is same
        product2.productName?.let { product1.productName?.compareTo(it, true) }!!

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val root = inflater.inflate(R.layout.fragment_home, container, false)

        initView(root)
        loadAvailableProducts()

        fabHome.setOnClickListener { view ->
            startActivity(Intent(context, AddNewProductAtivity::class.java))
        }

        return root
    }

    private fun initView(root: View) {
        //       searchHome = root.findViewById(R.id.searchHome)
        fabHome = root.findViewById(R.id.fabHome)
        progressLayout = root.findViewById(R.id.progressLayout)
        progressBar = root.findViewById(R.id.progressBar)
        recyclerHome = root.findViewById(R.id.recyclerHome)//initialisation of recycler
        recyclerAdapter = HomeRecyclerAdapter(mContext!!, ArrayList())
        recyclerHome.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = recyclerAdapter
        }

    }

    override fun onResume() {
        super.onResume()
        loadAvailableProducts()
    }

    private fun loadAvailableProducts() {
        if (ConnectionManager().checkConnectivity(mContext!!)) {

            val availableProductsRepository = AvailableProductsRepository()
            val availableProductViewModelFactory =
                AvailableProductViewModelFactory(availableProductsRepository)
            availableProductsViewModel = ViewModelProvider(
                this,
                availableProductViewModelFactory
            )[AvailableProductsViewModel::class.java]
            availableProductsViewModel.getProduct()

            availableProductsViewModel.productMutableLiveData.observe(
                viewLifecycleOwner,
                androidx.lifecycle.Observer {
                    productListObject = it
                    productList = productListObject.products!!
                    Log.d("Home", productListObject.toString())
                    Log.d("Home", productList.toString())
                    recyclerAdapter.setData(productList)
                    progressLayout.visibility = View.GONE
                    recyclerHome.visibility = View.VISIBLE
                    fabHome.visibility = View.VISIBLE
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.sort) {
            Collections.sort(productList, ratingComparator)
            // productList.reverse()
        }

        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
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
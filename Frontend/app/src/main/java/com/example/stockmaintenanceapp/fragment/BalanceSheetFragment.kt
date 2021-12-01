package com.example.stockmaintenanceapp.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmaintenanceapp.R
import com.example.stockmaintenanceapp.adapter.BalancedSheetAdapter
import com.example.stockmaintenanceapp.model.BalanceSheetModel
import com.example.stockmaintenanceapp.model.Comment
import com.example.stockmaintenanceapp.repository.BalanceSheetRepository
import com.example.stockmaintenanceapp.services.BalanceSheetService
import com.example.stockmaintenanceapp.services.ServiceBuilder
import com.example.stockmaintenanceapp.util.ConnectionManager
import com.example.stockmaintenanceapp.viewmodel.BalanceSheetViewModel
import com.example.stockmaintenanceapp.viewmodel.BalanceSheetViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class BalanceSheetFragment : Fragment() {

    var mContext: Context? = null
    lateinit var recyclerBalanceSheet: RecyclerView
    lateinit var fabBalanceSheet: FloatingActionButton
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: BalancedSheetAdapter
    lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    lateinit var balanceSheetList: ArrayList<BalanceSheetModel>
    lateinit var balanceSheetViewModel: BalanceSheetViewModel
    val commentList: java.util.ArrayList<Comment> = arrayListOf()
    val balanceSheetModel = BalanceSheetModel()
    var initialDrawerMoney: String = ""
    var finalDrawerMoney: String = ""

    var outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    var inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US)

    var ratingComparator = Comparator<BalanceSheetModel> { finalMoney1, finalMoney2 ->

        finalMoney2.FinalDrawerMoney?.let {
            finalMoney1.FinalDrawerMoney?.compareTo(it)
        }!!

    }

    val pattern = "dd-MM-yyyy"
    val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
    val date = simpleDateFormat.format(Date())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_balance_sheet, container, false)

        initView(view)
        loadBalanceSheetList()

        fabBalanceSheet.setOnClickListener {
            openAlertDialogToAddItem()
        }

        return view
    }

    private fun openAlertDialogToAddItem() {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        builder.setTitle("Add Comment & FinalMoney")
        val layoutView: View =
            inflater.inflate(R.layout.enter_comment_for_balance_sheet_dialog, null)
        builder.setView(layoutView)

        val etDateToday: TextView = layoutView.findViewById(R.id.etDateToday)
        etDateToday.setText(date)
        val layout_list: LinearLayout = layoutView.findViewById(R.id.layout_list)
        val btnAdd: Button = layoutView.findViewById(R.id.btnAdd)
        val etFinalDrawerMoney: EditText = layoutView.findViewById(R.id.etFinalDrawerMoney)

        btnAdd.setOnClickListener {
            addViewManually(layout_list)
        }

        builder.setPositiveButton("Ok") { dialog, which -> // get the edit text values here and pass them back via the listener
            initialDrawerMoney = etFinalDrawerMoney.text.toString()
            if (!initialDrawerMoney.equals("")) {
                checkIfValidAndRead(
                    layout_list,
                    initialDrawerMoney,
                    etDateToday.text.toString()
                )
            } else {
                Toast.makeText(mContext, "Enter Final Amount", Toast.LENGTH_SHORT).show()
            }

        }
        builder.setNeutralButton("Cancel") { dialog, which ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
    }

    private fun checkIfValidAndRead(
        layout_list: LinearLayout,
        finalDrawerMoney: String,
        dateToday: String
    ) {
        commentList.clear()
        for (i in 0 until layout_list.childCount) {
            val commentView = layout_list.getChildAt(i)
            val etComment: EditText = commentView.findViewById(R.id.etComment)

            val comm = etComment.text.toString()

            val comment = Comment()

            if (!comm.equals("")) {

                comment.comment = comm
                commentList.add(comment)

            } else {
                break
            }

        }

        if (!finalDrawerMoney.equals("")) {
            balanceSheetModel.Comments = commentList

            addOrUpdateByValidating(dateToday, finalDrawerMoney.toInt())
//            addComment(balanceSheetModel)
//            updateComment()
        }



        if (commentList.size == 0) {
            Toast.makeText(mContext, "List is null or Product Not Available", Toast.LENGTH_SHORT)
                .show()
        }

    }

    private fun addOrUpdateByValidating(dateToday: String, finalDrawerMoney: Int) {
        var res=true
        balanceSheetModel.InitialDrawerMoney = initialDrawerMoney.toInt()
        balanceSheetModel.FinalDrawerMoney = finalDrawerMoney
        if (balanceSheetList.size == 0) {
            addComment(balanceSheetModel, dateToday)
        } else {
            for (i in 0..balanceSheetList.size - 1) {
                val date = inputFormat.parse(balanceSheetList[i].createdAt!!)
                val formattedDate = outputFormat.format(date!!)
                if (formattedDate == dateToday) {
                    Log.d("BalanceSheet", formattedDate + "  " + dateToday)
                    updateComment(balanceSheetModel, dateToday, finalDrawerMoney)
                    res=false
                    break
                }
            }
            if(res){
                addComment(balanceSheetModel,dateToday)
            }
        }

    }

    private fun updateComment(
        newComment: BalanceSheetModel,
        dateToday: String,
        finalDrawerMoney: Int
    ) {
        var id = ""
        for (i in 0..balanceSheetList.size - 1) {
            val date = inputFormat.parse(balanceSheetList[i].createdAt!!)
            val formattedDate = outputFormat.format(date!!)
            if (formattedDate == dateToday) {
                val balanceSheetModel = balanceSheetList.get(i)
                balanceSheetModel.FinalDrawerMoney = finalDrawerMoney
                id = balanceSheetModel._id.toString()
            }
            val productService = ServiceBuilder.buildService(BalanceSheetService::class.java)
            val requestCall = productService.updateBalanceSheetItem(id, balanceSheetModel)

            requestCall.enqueue(object : Callback<BalanceSheetModel> {
                override fun onResponse(
                    call: Call<BalanceSheetModel>,
                    response: Response<BalanceSheetModel>
                ) {
                    if (response.isSuccessful) {
                        val updatedProductObject = response.body()
                        onResume()
                        Toast.makeText(
                            mContext,
                            "Item updated sucessfully",
                            Toast.LENGTH_SHORT
                        ).show()

                    }
//                    else {
//                        Toast.makeText(
//                            mContext,
//                            "Error Occured",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
                }

                override fun onFailure(call: Call<BalanceSheetModel>, t: Throwable) {
                    Toast.makeText(
                        mContext,
                        "Error Occured" + t.toString(),
                        Toast.LENGTH_SHORT
                    ).show()
                }

            })

        }
    }


    private fun addComment(newComment: BalanceSheetModel, dateToday: String) {
        val product = ServiceBuilder.buildService(BalanceSheetService::class.java)
        val requestCall = product.addBalanceSheetItem(newComment)

        requestCall.enqueue(object : Callback<BalanceSheetModel> {
            override fun onResponse(
                call: Call<BalanceSheetModel>,
                response: Response<BalanceSheetModel>
            ) {
                progressLayout.visibility = View.GONE
                fabBalanceSheet.visibility = View.VISIBLE
                if (response.isSuccessful) {
                    var newlyAddedProduct = response.body()
                    onResume()
                } else {
                    Toast.makeText(
                        context,
                        "Error adding details, fill all the data correctly",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

            override fun onFailure(call: Call<BalanceSheetModel>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Failed to retrieve details",
                    Toast.LENGTH_SHORT
                ).show()

            }

        })
    }

    private fun loadBalanceSheetList() {

        if (ConnectionManager().checkConnectivity(mContext!!)) {
            val balanceSheetRepository=BalanceSheetRepository()
            val balanceSheetViewModelFactory=BalanceSheetViewModelFactory(balanceSheetRepository)
            balanceSheetViewModel=
                ViewModelProvider(this,balanceSheetViewModelFactory)[BalanceSheetViewModel::class.java]
            balanceSheetViewModel.getProduct()

            balanceSheetViewModel.balanceSheetMutableLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                balanceSheetList= it
                recyclerAdapter.setData(balanceSheetList)
                progressLayout.visibility=View.GONE
                    recyclerBalanceSheet.visibility=View.VISIBLE
                fabBalanceSheet.visibility=View.VISIBLE
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

//        if (ConnectionManager().checkConnectivity(mContext!!)) {
//            val productService = ServiceBuilder.buildService(BalanceSheetService::class.java)
//            val requestCall = productService.getBalanceSheetList()
//            requestCall.enqueue(object : Callback<ArrayList<BalanceSheetModel>> {
//
//                //status code will decide if your Http Response is a Sucess or Error
//                override fun onResponse(
//                    call: Call<ArrayList<BalanceSheetModel>>,
//                    response: Response<ArrayList<BalanceSheetModel>>
//                ) {
//                    progressLayout.visibility = View.GONE
//                    fabBalanceSheet.visibility = View.VISIBLE
//
//                    if (response.isSuccessful) {
//                        balanceSheetList = response.body()!!
//                        Log.d("OwnPurchase", balanceSheetList.toString())
//
//                        recyclerAdapter =
//                            BalancedSheetAdapter(mContext!!, balanceSheetList)
//                        recyclerBalanceSheet.adapter = recyclerAdapter
//                        recyclerBalanceSheet.layoutManager = layoutManager
//
//                    } else {
//                        Toast.makeText(
//                            mContext!!,
//                            "Error loading the products",
//                            Toast.LENGTH_SHORT
//                        ).show()
//                    }
//                }
//
//                override fun onFailure(
//                    call: Call<ArrayList<BalanceSheetModel>>,
//                    t: Throwable
//                ) {
//                    Toast.makeText(
//                        mContext!!,
//                        "Error occured" + t.toString(),
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//            })
//        } else {
//            val dialog = AlertDialog.Builder(mContext!!)
//            dialog.setTitle("Error")
//            dialog.setMessage("Internet Connection is not Found")
//            dialog.setPositiveButton("Open Settings") { text, listener ->
//                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
//                startActivity(settingsIntent)
//                activity?.finish()
//            }
//
//            dialog.setNegativeButton("Exit") { text, listener ->
//                ActivityCompat.finishAffinity(activity as Activity)
//            }
//            dialog.create()
//            dialog.show()
//        }
    }

    private fun initView(view: View) {
        fabBalanceSheet = view.findViewById(R.id.fabBalanceSheet)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        layoutManager = LinearLayoutManager(activity)
        progressLayout.visibility = View.VISIBLE
        fabBalanceSheet.visibility = View.GONE
        recyclerBalanceSheet = view.findViewById(R.id.recyclerBalanceSheet)
        recyclerAdapter= BalancedSheetAdapter(mContext!!,ArrayList())
        recyclerBalanceSheet.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter=recyclerAdapter
        }

        balanceSheetModel.createdAt=date
        balanceSheetModel.updatedAt=date

    }

    private fun addViewManually(layout_list: LinearLayout) {
        val salesView = layoutInflater.inflate(R.layout.enter_comment_dialog, null, false)

        val etComment = salesView.findViewById(R.id.etComment) as EditText
        val imgclose = salesView.findViewById(R.id.imgclose) as ImageView


        imgclose.setOnClickListener {
            layout_list.removeView(salesView)
        }


        layout_list.addView(salesView)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.sort) {
            Collections.sort(balanceSheetList, ratingComparator)
            // productList.reverse()
        }

        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        loadBalanceSheetList()
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




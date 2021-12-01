package com.example.stockmaintenanceapp.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmaintenanceapp.R
import com.example.stockmaintenanceapp.activity.ViewPDFActivity
import com.example.stockmaintenanceapp.adapter.ItemToBuyAdapter
import com.example.stockmaintenanceapp.model.OwnPurchase
import com.example.stockmaintenanceapp.model.OwnPurchaseList
import com.example.stockmaintenanceapp.repository.OwnPurchaseRepository
import com.example.stockmaintenanceapp.services.OwnPurchaseService
import com.example.stockmaintenanceapp.services.ServiceBuilder
import com.example.stockmaintenanceapp.util.ConnectionManager
import com.example.stockmaintenanceapp.viewmodel.OwnPurchaseViewModel
import com.example.stockmaintenanceapp.viewmodel.OwnPurchaseViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class SlideshowFragment : Fragment() {

    var mContext: Context? = null
    lateinit var recyclerItemToPurchase: RecyclerView
    lateinit var fabItemToPurchase: FloatingActionButton
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: ItemToBuyAdapter
    lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    lateinit var productListObject: OwnPurchase
    lateinit var ownPurchaseViewModel: OwnPurchaseViewModel
    var productList = arrayListOf<OwnPurchaseList>()
    lateinit var productListObjectToPrint: OwnPurchase
    var productListToPrint = arrayListOf<OwnPurchaseList>()
    lateinit var btnPrint: Button

    val pattern = "dd-MM-yyyy"
    val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
    val date = simpleDateFormat.format(Date())


    var ratingComparator = Comparator<OwnPurchaseList> { item1, item2 ->

        item2.productName?.let { item1.productName?.compareTo(it, true) }!!

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)

        initView(root)
        loadPurchasedProducts()

        fabItemToPurchase.setOnClickListener {
            openAlertDialogToAdd()
        }

        btnPrint.setOnClickListener {
            createPdf(productList)
        }
        return root
    }
    private fun openAlertDialogToAdd() {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        builder.setTitle("Add Products")
        val layoutView: View = inflater.inflate(R.layout.add_own_purchase_dialog, null)
        builder.setView(layoutView)

        val etProductName: EditText = layoutView.findViewById(R.id.etProductName)
        val etProductQuantity: EditText = layoutView.findViewById(R.id.etProductQuantity)
        val spinner_quantity_unit: AppCompatSpinner =
            layoutView.findViewById(R.id.spinner_quantity_unit)

        val spinner_quantity_type_list =
            arrayListOf("Quantity", "Kg", "Packet", "Litre", "Quintal", "Dozen")

        val arrayAdapter =
            context?.let {
                ArrayAdapter(
                    it,
                    android.R.layout.simple_spinner_item,
                    spinner_quantity_type_list
                )
            }
        arrayAdapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_quantity_unit.adapter = arrayAdapter

        builder.setPositiveButton("Ok") { dialog, which -> // get the edit text values here and pass them back via the listener
            val product = etProductName.text.toString()
            val qty = etProductQuantity.text.toString()
            if (spinner_quantity_unit.selectedItemPosition != 0 && product != "" && qty != "") {
                addProduct(product, qty.toInt(), spinner_quantity_unit.selectedItem.toString())
            } else {
                Toast.makeText(context, "Empty field are not allowed", Toast.LENGTH_SHORT).show()
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

    private fun addProduct(productName: String, qty: Int, qtyType: String) {
        progressLayout.visibility = View.VISIBLE
        fabItemToPurchase.visibility = View.GONE

        val newProduct = OwnPurchaseList()
        newProduct.productName = productName
        newProduct.quantity = qty
        newProduct.qtyType = qtyType

        val product = ServiceBuilder.buildService(OwnPurchaseService::class.java)
        val requestCall = product.addProduct(newProduct)

        requestCall.enqueue(object : Callback<OwnPurchaseList> {
            override fun onResponse(
                call: Call<OwnPurchaseList>,
                response: Response<OwnPurchaseList>
            ) {
                progressLayout.visibility = View.GONE
                fabItemToPurchase.visibility = View.VISIBLE
                if (response.isSuccessful) {

                    var newlyAddedProduct = response.body()
                    loadPurchasedProducts()
                } else {
                    Toast.makeText(
                        context,
                        "Error adding details, fill all the data correctly",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

            override fun onFailure(call: Call<OwnPurchaseList>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Failed to retrieve details",
                    Toast.LENGTH_SHORT
                ).show()

            }

        })
    }

    private fun createPdf(productListToPrint: ArrayList<OwnPurchaseList>) {
        val pdfDocument = PdfDocument()//create a new documet
        val paint = Paint()

        val myPageInfo =
            PdfDocument.PageInfo.Builder(1200, 2010, 1).create()//creating page description
        val myPage = pdfDocument.startPage(myPageInfo)//start a page
        val canva = myPage.canvas

        paint.textSize = 80F
        canva.drawText("List Of Products", 30F, 80F, paint)

        paint.textSize = 30F

        paint.textAlign = Paint.Align.RIGHT
        canva.drawText("Date", (canva.width - 40).toFloat(), 40F, paint)
        canva.drawText(date, (canva.width - 40).toFloat(), 80F, paint)
        paint.textAlign = Paint.Align.LEFT

        paint.setColor(Color.rgb(150, 150, 150))
        canva.drawRect(30F, 160F, (canva.width - 30).toFloat(), 160F, paint)//first line

        paint.setColor(Color.rgb(150, 150, 150))
        canva.drawRect(30f, 250f, (canva.width - 30).toFloat(), 300f, paint)

        paint.setColor(Color.WHITE)
        canva.drawText("Item", 50f, 285f, paint)
        paint.textAlign = Paint.Align.RIGHT
        canva.drawText("Qty", (canva.width - 40).toFloat(), 285f, paint)
        paint.textAlign = Paint.Align.LEFT

        paint.setColor(Color.BLACK)
        if (productListToPrint.size != 0) {
            for (i in 0..productListToPrint.size - 1) {
                productListToPrint.get(i).productName?.let {
                    canva.drawText(
                        it,
                        50f,
                        (380 + (i * 45)).toFloat(),
                        paint
                    )
                }
                paint.textAlign = Paint.Align.RIGHT
                canva.drawText(
                    productListToPrint.get(i).quantity.toString(),
                    (canva.width - 40).toFloat(),
                    (380 + (i * 45)).toFloat(),
                    paint
                )
                paint.textAlign = Paint.Align.LEFT
            }
        } else {
            Log.d("Detail", "No Products To Be Bought")
        }

        pdfDocument.finishPage(myPage)
        val file = File(activity?.getExternalFilesDir("/"), "$date Products List.pdf")

        try {
            pdfDocument.writeTo(FileOutputStream(file))
            Toast.makeText(
                mContext!!,
                "Document Generated Sucessfully",
                Toast.LENGTH_SHORT
            ).show()
            viewPDF(file.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }

        pdfDocument.close()
    }

    private fun viewPDF(file: String) {
        val intent=Intent(mContext, ViewPDFActivity::class.java)
        intent.putExtra("filePathOwnPurchase",file)
        startActivity(intent)
    }

    private fun loadPurchasedProducts() {
        if (ConnectionManager().checkConnectivity(mContext!!)) {

            val ownPurchaseRepository= OwnPurchaseRepository()
            val ownPurchaseViewModelFactory= OwnPurchaseViewModelFactory(ownPurchaseRepository)
            ownPurchaseViewModel=
                ViewModelProvider(this,ownPurchaseViewModelFactory)[OwnPurchaseViewModel::class.java]
            ownPurchaseViewModel.getOwnPurchase()

            ownPurchaseViewModel.ownPurchaseMutableLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                productListObject = it
                productList= productListObject.ownPurchases!!
                recyclerAdapter.setData(productList)
                progressLayout.visibility=View.GONE
                recyclerItemToPurchase.visibility=View.VISIBLE
                fabItemToPurchase.visibility=View.VISIBLE
                btnPrint.visibility=View.VISIBLE
            }
            )

//            val productService = ServiceBuilder.buildService(OwnPurchaseService::class.java)
//            val requestCall = productService.getOwnPurchaseList()
//            try {
//                progressLayout.visibility = View.GONE
//                fabItemToPurchase.visibility = View.VISIBLE
//                val response=requestCall.execute()
//                if (response.isSuccessful) {
//                    productListObject = response.body()!!
//                    Log.d("OwnPurchase", productListObject.toString())
//                    productList = productListObject.ownPurchases!!
//                    Log.d("OwnPurchase", productList.toString())
//
//                    recyclerAdapter = ItemToBuyAdapter(mContext!!, productList)
//                    recyclerItemToPurchase.adapter = recyclerAdapter
//                    recyclerItemToPurchase.layoutManager = layoutManager
//                } else {
//                    Toast.makeText(
//                        mContext!!,
//                        "Error loading the products",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//            } catch (e: java.lang.Exception) {
//                e.printStackTrace()
//            //    Toast.makeText(mContext, "Some Exception Occured", Toast.LENGTH_SHORT).show()
//
//            }
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
        fabItemToPurchase = root.findViewById(R.id.fabItemToPurchase)
        progressLayout = root.findViewById(R.id.progressLayout)
        progressBar = root.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        layoutManager = LinearLayoutManager(activity)
        btnPrint = root.findViewById(R.id.btnPrint)
        recyclerItemToPurchase = root.findViewById(R.id.recyclerItemToPurchase)
        recyclerAdapter= ItemToBuyAdapter(mContext!!,ArrayList())
        recyclerItemToPurchase.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter=recyclerAdapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if (id == R.id.sort) {
            Collections.sort(productList, ratingComparator)
            // productList.reverse()
        }
//        if(id==R.id.action_view){
//
//        }

        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        loadPurchasedProducts()
    }

    override fun onAttach(context: Context) {
        mContext = context
        super.onAttach(context)
    }

    override fun onDetach() {
        mContext = null
        super.onDetach()

    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.menu_dashboard,menu)
//        super.onCreateOptionsMenu(menu, inflater)
//    }
}

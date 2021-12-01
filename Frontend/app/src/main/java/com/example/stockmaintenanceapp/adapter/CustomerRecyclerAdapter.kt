package com.example.stockmaintenanceapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmaintenanceapp.R
import com.example.stockmaintenanceapp.activity.CustomerDetailActivity
import com.example.stockmaintenanceapp.model.AvailableProductsList
import com.example.stockmaintenanceapp.model.Customers
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CustomerRecyclerAdapter(private val context: Context, private var itemList: java.util.ArrayList<Customers>) :
    RecyclerView.Adapter<CustomerRecyclerAdapter.CustomerViewHolder>() {

    var outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    var inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US)

//    val pattern = "dd-MM-yyyy"
//    val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
//    val date = simpleDateFormat.format(Date())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.customer_single_row, parent, false
        )
        return CustomerViewHolder(view)
    }

    override fun onBindViewHolder(holder: CustomerViewHolder, position: Int) {

        holder.customer = itemList[position]
        holder.txtCustomerName.text = itemList[position].name
        val dateParssed=inputFormat.parse(itemList[position].createdAt)
        val dateFormatted=outputFormat.format(dateParssed)
        holder.txtCustomerDate.text = dateFormatted

        holder.itemView.setOnClickListener { v ->
            val context = v.context
            val intent = Intent(context, CustomerDetailActivity::class.java)
            intent.putExtra(CustomerDetailActivity.ARG_SALES_ID, holder.customer)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class CustomerViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val txtCustomerName: TextView
        val txtCustomerDate: TextView

        var customer: Customers? = null

        init {
            txtCustomerName = itemView.findViewById(R.id.txtCustomerName)
            txtCustomerDate = itemView.findViewById(R.id.txtCustomerDate)

        }

    }

    fun setData(itemList:List<Customers>){
        this.itemList= itemList as ArrayList<Customers>
        notifyDataSetChanged()
    }
}
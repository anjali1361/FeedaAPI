package com.example.stockmaintenanceapp.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmaintenanceapp.R
import com.example.stockmaintenanceapp.activity.UpdateExistingProductActivity
import com.example.stockmaintenanceapp.model.AvailableProductsList
import org.w3c.dom.Text

class HomeRecyclerAdapter(private val context: Context, private var itemList: List<AvailableProductsList>) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.available_product_single_row, parent, false
        )
        return HomeViewHolder(view)

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {

        holder.product = itemList[position]
        holder.txtProductName.text = itemList[position].productName
        holder.txtProductCost.text = itemList[position].costPerUnit.toString()
        holder.txtQuantity.text = itemList[position].totalQuantity.toString()
        holder.txtQuantityType.text=itemList[position].qtyType

        holder.itemView.setOnClickListener { v ->
            val context = v.context
            val intent = Intent(context, UpdateExistingProductActivity::class.java)
            intent.putExtra(UpdateExistingProductActivity.ARG_ITEM_ID, holder.product!!._id)
            holder.product!!._id?.let { Log.d("HomeAdapter", it) }
            context.startActivity(intent)

        }

    }

    class HomeViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val txtProductName: TextView
        val txtProductCost: TextView
        val txtQuantity:TextView
        val txtQuantityType: TextView

        var product: AvailableProductsList? = null

        init {
            txtProductName = itemView.findViewById(R.id.txtProductName)
            txtProductCost = itemView.findViewById(R.id.txtProductCost)
            txtQuantity=itemView.findViewById(R.id.txtQuantity)
            txtQuantityType = itemView.findViewById(R.id.txtQuantityType)

        }

    }

    fun setData(itemList: List<AvailableProductsList>){
        this.itemList=itemList
        notifyDataSetChanged()
    }
}





















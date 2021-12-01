package com.example.stockmaintenanceapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmaintenanceapp.R
import com.example.stockmaintenanceapp.activity.UpdateItemToBuyActivity
import com.example.stockmaintenanceapp.model.OwnPurchaseList

class ItemToBuyAdapter(private val context: Context, private var itemList: ArrayList<OwnPurchaseList>) :
    RecyclerView.Adapter<ItemToBuyAdapter.ItemToBuyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemToBuyViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_to_buy_single_row, parent, false
        )
        return ItemToBuyViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemToBuyViewHolder, position: Int) {

        holder.product = itemList[position]
        holder.txtProductName.text = itemList[position].productName
        holder.txtQuantity.text = itemList[position].quantity.toString()
        holder.txtQtyType.text = itemList[position].qtyType

        holder.imgdelete.setOnClickListener { v ->
            val context = v.context
            val intent = Intent(context, UpdateItemToBuyActivity::class.java)
            intent.putExtra(UpdateItemToBuyActivity.ARG_ITEM_ID, holder.product!!._id)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }


    class ItemToBuyViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val txtProductName: TextView
        val txtQuantity: TextView
        val txtQtyType: TextView
        val imgdelete: ImageView

        var product: OwnPurchaseList? = null

        init {
            txtProductName = itemView.findViewById(R.id.txtProductName)
            txtQuantity = itemView.findViewById(R.id.txtQuantity)
            txtQtyType = itemView.findViewById(R.id.txtQtyType)
            imgdelete = itemView.findViewById(R.id.imgdelete)

        }

    }

    fun setData(itemList:ArrayList<OwnPurchaseList>){
        this.itemList=itemList
        notifyDataSetChanged()
    }
}
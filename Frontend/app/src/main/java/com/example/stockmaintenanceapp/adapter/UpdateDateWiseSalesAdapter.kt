package com.example.stockmaintenanceapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmaintenanceapp.R
import com.example.stockmaintenanceapp.model.DateWiseSales
import com.example.stockmaintenanceapp.model.OrderedItems
import com.example.stockmaintenanceapp.model.OwnPurchaseList
import com.example.stockmaintenanceapp.model.SalesInfo

class UpdateDateWiseSalesAdapter(context: Context, itemList: ArrayList<SalesInfo>) :
    RecyclerView.Adapter<UpdateDateWiseSalesAdapter.UpdateDateWiseSalesViewHolder>() {

    var context: Context
    var itemList: ArrayList<SalesInfo>

    init {
        this.context = context
        this.itemList = itemList
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UpdateDateWiseSalesViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.update_date_wise_sales_single_row, parent, false
        )
        return UpdateDateWiseSalesViewHolder(view)
    }

    override fun onBindViewHolder(holder: UpdateDateWiseSalesViewHolder, position: Int) {

        holder.product = itemList[position]
        holder.etItem.setText(itemList[position].productName)
        holder.etItemQuantity.setText(itemList[position].qty.toString())
        holder.etItemPrice.setText( itemList[position].rate.toString())
        holder.imgClose.setOnClickListener{
            itemList.remove(itemList[position])
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class UpdateDateWiseSalesViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val etItem:EditText
        val etItemQuantity: EditText
        val etItemPrice:EditText
        val imgClose:ImageView

        var product: SalesInfo? = null

        init {
            etItem = itemView.findViewById(R.id.etItem)
            etItemQuantity = itemView.findViewById(R.id.etItemQuantity)
            etItemPrice = itemView.findViewById(R.id.etItemPrice)
            imgClose=itemView.findViewById(R.id.imgclose)
        }

    }
}
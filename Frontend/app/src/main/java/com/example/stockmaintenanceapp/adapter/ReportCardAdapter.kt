package com.example.stockmaintenanceapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.stockmaintenanceapp.R
import com.example.stockmaintenanceapp.activity.ReportCardActivity
import com.example.stockmaintenanceapp.model.BalanceSheetModel
import com.example.stockmaintenanceapp.model.Comment
import java.text.SimpleDateFormat
import java.util.*

class ReportCardAdapter(private val context: Context, private var itemList: ArrayList<Comment>) :
    RecyclerView.Adapter<ReportCardAdapter.ReportCardViewHolder>() {

//    val pattern = "dd-MM-yyyy"
//    val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
//    val date = simpleDateFormat.format(Date())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportCardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.report_card_single_row, parent, false
        )
        return ReportCardViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReportCardViewHolder, position: Int) {
        holder.history = itemList[position]
        holder.txtComment.text = itemList[position].comment
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class ReportCardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val txtComment: TextView

        var history: Comment? = null

        init {
            txtComment = itemView.findViewById(R.id.txtComment)
        }

    }

    fun setData(itemList: ArrayList<Comment>){
        this.itemList=itemList
        notifyDataSetChanged()
    }

}
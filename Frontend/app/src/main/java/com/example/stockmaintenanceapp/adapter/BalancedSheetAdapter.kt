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
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class BalancedSheetAdapter(private val context: Context,private var itemList: ArrayList<BalanceSheetModel>) :
    RecyclerView.Adapter<BalancedSheetAdapter.BalancedSheetViewHolder>() {

//    val pattern = "dd-MM-yyyy"
//    val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
//    val date = simpleDateFormat.format(Date())

    var outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    var inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BalancedSheetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.balance_sheet_fragment_single_view, parent, false
        )
        return BalancedSheetViewHolder(view)
    }

    override fun onBindViewHolder(holder: BalancedSheetViewHolder, position: Int) {
        holder.history = itemList[position]
        holder.txtInitialMoney.text = itemList[position].InitialDrawerMoney.toString()
        val inputDate=inputFormat.parse(itemList[position].createdAt!!)
        val outputDate=outputFormat.format(inputDate!!)
        holder.txtTodayDate.setText(outputDate)
        holder.txtFinalMoney.text = itemList[position].FinalDrawerMoney.toString()

        holder.itemView.setOnClickListener { v ->
               val context = v.context
               val intent = Intent(context, ReportCardActivity::class.java)
               intent.putExtra(ReportCardActivity.ARG_ITEM_ID,holder.history)
               context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    class BalancedSheetViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val txtInitialMoney: TextView
        val txtFinalMoney: TextView
        val txtTodayDate: TextView

        var history: BalanceSheetModel? = null

        init {
            txtInitialMoney = itemView.findViewById(R.id.txtInitialMoney)
            txtFinalMoney = itemView.findViewById(R.id.txtFinalMoney)
            txtTodayDate = itemView.findViewById(R.id.txtTodayDate)

        }
    }

    fun setData(itemList:ArrayList<BalanceSheetModel>){
        this.itemList=itemList
        notifyDataSetChanged()
    }
}
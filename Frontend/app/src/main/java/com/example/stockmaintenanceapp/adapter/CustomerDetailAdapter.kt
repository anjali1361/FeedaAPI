package com.example.stockmaintenanceapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import com.example.stockmaintenanceapp.R
import com.example.stockmaintenanceapp.model.DateWiseSales
import com.example.stockmaintenanceapp.model.SalesInfo
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CustomerDetailAdapter(context: Context, childList: ArrayList<DateWiseSales>) :
    BaseExpandableListAdapter() {

    var context: Context
    var childList: ArrayList<DateWiseSales>

//    val pattern = "dd-MM-yyyy"
//    val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
//    val date = simpleDateFormat.format(Date())

    var outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.US)
    var inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US)


    init {
        this.context = context
        this.childList = childList
    }

    override fun getGroupCount(): Int {
        return childList.size
    }

    override fun getChildrenCount(parentPosition: Int): Int {
        val itemList: ArrayList<SalesInfo>? = childList.get(parentPosition).sales?.orderItems
        if (itemList != null) {
            return itemList.size
        };

        return 0
    }

    override fun getGroup(parentPosition: Int): Any {
        return childList.get(parentPosition)
    }

    override fun getChild(parentPosition: Int, childPosition: Int): SalesInfo? {
        val itemList = childList.get(parentPosition).sales?.orderItems
        if (itemList != null) {
            return itemList.get(childPosition)
        }

        return null
    }

    override fun getGroupId(parentPosition: Int): Long {
        return parentPosition.toLong()
    }

    override fun getChildId(parentPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(
        parentPosition: Int,
        isExpanded: Boolean,
        v: View?,
        parent: ViewGroup?
    ): View {
        val parentInfo = getGroup(parentPosition) as DateWiseSales
        var view: View? = null

        if (view == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.customer_detail_parent_item, null)
        }


        val txtDate: TextView = view?.findViewById(R.id.txtDate)!!
        val dateFormatted = inputFormat.parse(parentInfo.updatedAt)
        val dateParsed = outputFormat.format(dateFormatted)
        txtDate.setText(dateParsed)
        val txtDue: TextView = view.findViewById(R.id.txtDue)
        txtDue.setText(parentInfo.due!!.toString())
        val txtTotal: TextView = view.findViewById(R.id.txtTotal)
        txtTotal.setText(parentInfo.total!!.toString())


        return view
    }

    override fun getChildView(
        parentPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        v: View?,
        parent: ViewGroup?
    ): View {
        val childInfo = getChild(parentPosition, childPosition) as SalesInfo
        var view: View? = null
        if (view == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.customer_detail_child_item, null)
        }

        val txtItem: TextView = view?.findViewById(R.id.txtItem)!!
        txtItem.setText(childInfo.productName)
        val txtItemQuantity: TextView = view.findViewById(R.id.txtItemQuantity)
        txtItemQuantity.setText(childInfo.qty!!.toString())
        val txtItemPrice: TextView = view.findViewById(R.id.txtItemPrice)
        txtItemPrice.setText(childInfo.rate!!.toString())
        val txtQtyType:TextView=view.findViewById(R.id.txtQtyType)
        txtQtyType.setText(childInfo.qtytype)
        //  val subCategoryCheckbox:CheckBox=view.findViewById(R.id.subCategoryCheckbox)

        return view
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

}
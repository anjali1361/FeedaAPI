package com.example.stockmaintenanceapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.example.stockmaintenanceapp.R
import com.example.stockmaintenanceapp.model.AvailableProductsList
import java.util.*
import kotlin.collections.ArrayList

class AutoCompleteAdapter(
    context: Context?,
    items: List<AvailableProductsList>
) : ArrayAdapter<AvailableProductsList?>(context!!, 0, items) {

    private val items: ArrayList<AvailableProductsList>
    private val itemsAll: ArrayList<AvailableProductsList>
    private val suggestions: ArrayList<AvailableProductsList>
    private val viewResourceId: Int

    init {
        this.items = items as ArrayList<AvailableProductsList>
        itemsAll = items.clone() as ArrayList<AvailableProductsList>
        suggestions = ArrayList<AvailableProductsList>()
        this.viewResourceId = 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v = convertView
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.autocomplete_row,parent,false)
        }
        val product: AvailableProductsList = items[position]
        val productLabel = v!!.findViewById<View>(R.id.productName) as TextView
        productLabel.setText(product.productName)
        return v
    }

    override fun getFilter(): Filter {
        return nameFilter
    }

    var nameFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): String {
            return (resultValue as AvailableProductsList).productName!!
        }

        override fun performFiltering(constraint: CharSequence): FilterResults {
            return run {
                suggestions.clear()
                for (product in itemsAll) {
                    if (product.productName?.toLowerCase(Locale.ROOT)
                            ?.startsWith(constraint.toString().toLowerCase(Locale.ROOT)) == true
                    ) {
                        suggestions.add(product)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                filterResults
            }
        }

        override fun publishResults(
            constraint: CharSequence,
            results: FilterResults
        ) {
            val filteredList: ArrayList<AvailableProductsList> =
                results.values as ArrayList<AvailableProductsList>
            if (results.count > 0) {
                clear()
                for (c in filteredList) {
                    add(c)
                }
                notifyDataSetChanged()
            }
        }
    }

}
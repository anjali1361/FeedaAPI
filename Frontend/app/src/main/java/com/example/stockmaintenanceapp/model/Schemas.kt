package com.example.stockmaintenanceapp.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import org.w3c.dom.Comment
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

val pattern = "dd-MM-yyyy"
val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
val date = simpleDateFormat.format(Date())

//Available Product List
data class AvailableProducts(
    var products: ArrayList<AvailableProductsList>? = null
)
data class AvailableProductsList(
    var _id: String? = null,
    var productName: String? = null,
    var costPerUnit: Int? = null,
    var totalQuantity: Int? = null,
    var qtyType:String?=null
)

//Own Purchase List
data class OwnPurchase(
    var ownPurchases: ArrayList<OwnPurchaseList>? = null
)
data class OwnPurchaseList(
    var _id: String? = null,
    var productName: String? = null,
    var quantity: Int? = null,
    var qtyType: String? = null
)


//Adding New Customers
@Parcelize
data class Customers(
    var _id: String? = null,
    var name: String? = null,
    var number:String="",
    var address:String="",
    var createdAt:String?=null,
    var updatedAt:String?=null
): Parcelable

//Adding Sales By Post Request
@Parcelize
data class CustomerSales(//DateWiseSales
    var id: String? = null,
    var user: String? = null,
    var orderItems: ArrayList<OrderedItems>? = null,
    var due: Int? = null,
    var total: Int? = null,
    var fare:Int?=null

): Parcelable
@Parcelize
data class OrderedItems(//SalesInfo
    var productId: String? = null,
    var productName: String? = null,
    var qty: Int? = null,
    var rate: Int? = null,
    var qtytype: String? = null
): Parcelable

//Getting all the transaction of a user
@Parcelize
data class CustomerDetail(
    var _id: String? = null,
    var name: String? = null,
    var allSales: ArrayList<DateWiseSales>? = null
): Parcelable
@Parcelize
data class DateWiseSales(//parent
    var _id: String? = null,
    var user: String? = null,
    var sales: OrderItemObject? = null,
    val total: Int? = null,
    var due: Int? = null,
    var fare:Int?=null,
    var createdAt:String?=null,
    var updatedAt:String?=null
): Parcelable
@Parcelize
data class OrderItemObject(//child
    var orderItems: ArrayList<SalesInfo>? = null
): Parcelable
@Parcelize
data class SalesInfo(
    var productId: String? = null,
    var productName: String? = null,
    var qty: Int? = null,
    var rate: Int? = null,
    var qtytype: String? = null,
    var _id: String? = null
): Parcelable


//BalanceSheet Model
@Parcelize
data class BalanceSheetModel(
    var _id: String?=null,
    var Comments: ArrayList<com.example.stockmaintenanceapp.model.Comment>?=null,
    var InitialDrawerMoney: Int? = null,
    var FinalDrawerMoney:Int?=null,
    var createdAt:String?=null,
    var updatedAt:String?=null
):Parcelable
@Parcelize
data class Comment(
    var _id:String?=null,
    var comment:String?=null
):Parcelable

import mongoose from "mongoose";

const ownPurchasesSchema = mongoose.Schema({
  productName: {
    type: String,
    required: true,
  },
  quantity: {
    type: Number,
    
  },
  qtyType: {
    type: String,
  },
});

const OwnPurchases = mongoose.model("OwnPurchases", ownPurchasesSchema);

export default OwnPurchases;



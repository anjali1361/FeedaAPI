import mongoose from "mongoose";

const productSchema = mongoose.Schema(
  {
    productName: {
      type: String,
      required: true,
    },
    costPerUnit: {
      type: Number,
      required: true,
    },

    totalQuantity: {
      type: Number,
      required: true,
    },
    qtyType: {
      type: String,
      required: true,
    },
  },
  {
    timestamps: true,
  }
);

const Product = mongoose.model("Product", productSchema);

export default Product;

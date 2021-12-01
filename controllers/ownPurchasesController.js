import asyncHandler from "express-async-handler";
import OwnPurchases from "../models/ownPurchases.js";

const getOwnPurchases = asyncHandler(async (req, res) => {
  const ownPurchases = await OwnPurchases.find({});

  res.status(200).json({ ownPurchases });
});

// @desc    Fetch single product
// @route   GET /api/products/:id
// @access  Public
const getOwnPruchaseById = asyncHandler(async (req, res) => {
  const ownPurchase = await OwnPurchases.findById(req.params.id);

  if (ownPurchase) {
    res.json(ownPurchase);
  } else {
    res.status(404);
    throw new Error("Product not found");
  }
});

// @desc    Delete a product
// @route   DELETE /api/products/:id
// @access  Private/Admin
const deleteOwnPurchase = asyncHandler(async (req, res) => {
  const ownPurchase = await OwnPurchases.findById(req.params.id);

  if (ownPurchase) {
    await ownPurchase.remove();
    res.json({ message: "Product removed" });
  } else {
    res.status(404);
    throw new Error("Product not found");
  }
});

// @desc    Create a product
// @route   POST /api/products
// @access  Private/Admin
const createOwnPurchases = asyncHandler(async (req, res) => {
  const { productName, quantity, qtyType } = req.body;
  const ownPurchase = new OwnPurchases({
    productName,
    quantity,
    qtyType,
  });

  const createdOwnPurchases = await ownPurchase.save();
  res.status(201).json(createdOwnPurchases);
});

// @desc    Update a product
// @route   PUT /api/products/:id
// @access  Private/Admin
const updateOwnPurchase = asyncHandler(async (req, res) => {
  const { productName, quantity, qtyType } = req.body;

  const ownPurchase = await OwnPurchases.findById(req.params.id);

  if (ownPurchase) {
    ownPurchase.productName = productName;
    ownPurchase.quantity = quantity;
    ownPurchase.qtyType = qtyType;

    const updatedOwnPurchase = await ownPurchase.save();
    res.json(updatedOwnPurchase);
  } else {
    res.status(404);
    throw new Error("Product not found");
  }
});

export {
  getOwnPurchases,
  createOwnPurchases,
  deleteOwnPurchase,
  //   createProduct,
  getOwnPruchaseById,
  updateOwnPurchase,
};

import asyncHandler from "express-async-handler";
import BalanceSheet from "../models/balanceSheetModel.js";

const getBalanceSheetInfo = asyncHandler(async (req, res) => {
  const balanceSheetInfo = await BalanceSheet.find({});
  res.json(balanceSheetInfo);
});

const getBalanceSheetInfoById = asyncHandler(async (req, res) => {
  const balanceSheetInfoById = await BalanceSheet.findById(req.params.id);
  if (balanceSheetInfoById) {
    res.json(balanceSheetInfoById);
  } else {
    res.status(404);
    throw new Error("Product not found");
  }
});

const addBalance = asyncHandler(async (req, res) => {
  const { FinalDrawerMoney, Comments, InitialDrawerMoney } = req.body;

  const balance = await BalanceSheet.create({
    FinalDrawerMoney,
    Comments,
    InitialDrawerMoney,
  });

  if (balance) {
    res.status(201).json({
      balance
    });
  } else {
    res.status(400);
    throw new Error("Invalid user data");
  }
});

const updateACustomerBalanceById = asyncHandler(async (req, res) => {
  const balance = await BalanceSheet.findById(req.params.id);
  console.log(balance.Comments);
  if (balance) {
    balance.FinalDrawerMoney =
      req.body.FinalDrawerMoney || balance.FinalDrawerMoney;
    balance.Comments.push(req.body.Comments[0]);

    const updatedBalance = await balance.save();

    res.json({
      updatedBalance
    });
  } else {
    res.status(404);
    throw new Error("Balance Id not found");
  }
});

const deleteACustomerBalanceById = asyncHandler(async (req, res) => {
  const balance = await BalanceSheet.findById(req.params.id);

  if (balance) {
    await balance.remove();
    res.json({
      message: "Balance removed",
    });
  } else {
    res.status(404);
    throw new Error("Custommer balance not found");
  }
});

export {
  getBalanceSheetInfo,
  getBalanceSheetInfoById,
  addBalance,
  deleteACustomerBalanceById,
  updateACustomerBalanceById,
};

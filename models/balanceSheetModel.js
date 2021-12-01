import mongoose from "mongoose";

const balanceSheetSchema = mongoose.Schema(
  {
    FinalDrawerMoney: {
      type: Number,
      // required: true,
    },
    Comments: [
      {
        comment: {
          type: String,
           required: true
        },
      },
      {
        timestamps: true,
      },
    ],
    InitialDrawerMoney: {
      type: Number,
      // required: true
    },
  },
  {
    timestamps: true,
  }
);

const BalanceSheet = mongoose.model("BalanceSheet", balanceSheetSchema);

export default BalanceSheet;

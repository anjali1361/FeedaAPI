import mongoose from "mongoose";
// import bcrypt from "bcryptjs";

// )Add customer name me 2 r fields add hoga number
// and address (number and address user dal v shakta he nhi v )

const userSchema = mongoose.Schema(
  {
    name: {
      type: String,
      required: true,
    },
    number: {
      type: String,
      // required: true,
    },
    address: {
      type: String,
      // required: true,
    },
  },
  {
    timestamps: true,
  }
);

const User = mongoose.model("User", userSchema);

export default User;

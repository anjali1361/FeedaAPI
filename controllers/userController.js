import asyncHandler from "express-async-handler";
// import generateToken from "../utils/generateToken.js";
import User from "../models/userModel.js";

// @desc    Register a new user
// @route   POST /api/users
// @access  Public
const addUser = asyncHandler(async (req, res) => {
  const { name, number, address } = req.body;

  const userExists = await User.findOne({ name });

  if (userExists) {
    res.status(400);
    throw new Error("User already exists");
  }

  const user = await User.create({
    name,
    number,
    address,
  });

  if (user) {
    res.status(201).json({
      _id: user._id,
      name: user.name,
      number: user.number,
      address: user.address,
    });
  } else {
    res.status(400);
    throw new Error("Invalid user data");
  }
});

// @desc    Update user profile
// @route   PUT /api/users/profile
// @access  Private
// const updateUserProfile = asyncHandler(async (req, res) => {
//   const user = await User.findById(req.user._id);

//   if (user) {
//     user.name = req.body.name || user.name;
//     user.email = req.body.email || user.email;
//     if (req.body.password) {
//       user.password = req.body.password;
//     }

//     const updatedUser = await user.save();

//     res.json({
//       _id: updatedUser._id,
//       name: updatedUser.name,
//       email: updatedUser.email,
//       isAdmin: updatedUser.isAdmin,
//       token: generateToken(updatedUser._id),
//     });
//   } else {
//     res.status(404);
//     throw new Error("User not found");
//   }
// });

// @desc    Get all users
// @route   GET /api/users
// @access  Private/Admin
const getUsers = asyncHandler(async (req, res) => {
  const users = await User.find({});
 ; res.json(users);
});

// @desc    Delete user
// @route   DELETE /api/users/:id
// @access  Private/Admin
const deleteUser = asyncHandler(async (req, res) => {
  const user = await User.findById(req.params.id);

  if (user) {
    await user.remove();
    res.json({ message: "User removed" });
  } else {
    res.status(404);
    throw new Error("User not found");
  }
});

// @desc    Get user by ID
// @route   GET /api/users/:id
// @access  Private/Admin
const getUserById = asyncHandler(async (req, res) => {
  const user = await User.findById(req.params.id).select("-password");

  if (user) {
    res.json(user);
  } else {
    res.status(404);
    throw new Error("User not found");
  }
});

// @desc    Update user
// @route   PUT /api/users/:id
// @access  Private/Admin
const updateUser = asyncHandler(async (req, res) => {
  const user = await User.findById(req.params.id);

  if (user) {
    user.name = req.body.name || user.name;
    user.address = req.body.address || user.address;
    user.number = req.body.number || user.number;

    const updatedUser = await user.save();

    res.json({
      updatedUser
    });
  } else {
    res.status(404);
    throw new Error("User not found");
  }
});

const getUsersAndSales = asyncHandler(async (req, res) => {
  // const users = await User.find({});
  const users = await User.aggregate(
    [
      {
        $lookup: {
          from: "customersandsales",
          localField: "_id",
          foreignField: "user",
          as: "allSales",
        },
      },
    ],
    "createdAt updatedAt _id name allSales"
  );

  res.json(users);
});

// const deleteUserSales = asyncHandler(async (req, res) => {
//   // const users = await User.find({});
//   const users = await User.aggregate(
//     [
//       {
//         $lookup: {
//           from: "customersandsales",
//           localField: "_id",
//           foreignField: "user",
//           as: "allSales",
//         },
//       },
//     ],
//     "createdAt updatedAt _id name allSales"
//   );

//   res.json(users);
// });

async function getPostData() {
  const walkDataDb = await db.post.aggregate([
    {
      $project: { user: 0 },
    },

    {
      $lookup: {
        from: "replies",
        localField: "_id",
        foreignField: "postId",
        as: "userReply",
      },
    },
    { $addFields: { userId: { $toObjectId: "$userId" } } },

    {
      $lookup: {
        from: "users",
        localField: "userId",
        foreignField: "_id",
        as: "user",
      },
    },

    {
      $unwind: {
        path: "$user",
      },
    },
  ]);

  return walkDataDb;
}

export {
  addUser,
  // updateUserProfile,
  getUsers,
  deleteUser,
  getUserById,
  updateUser,
  getUsersAndSales,
  // deleteUserSales,
};

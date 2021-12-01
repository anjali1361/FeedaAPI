import express from "express";
const router = express.Router();

import {
  getCustomersAndSales,
  getCustomersAndSalesById,
  deleteCustomersAndSales,
  createCustomersAndSales,
  updateCustomersAndSales,
  deleteaUsersSales,
} from "../controllers/customersAndSalesController.js";

router.route("/").get(getCustomersAndSales).post(createCustomersAndSales);
router.route("/user/:id").delete(deleteaUsersSales);

router
  .route("/:id")
  .get(getCustomersAndSalesById)
  .delete(deleteCustomersAndSales)
  .put(updateCustomersAndSales);

export default router;

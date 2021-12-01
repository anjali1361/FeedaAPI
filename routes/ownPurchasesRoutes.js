import express from "express";
const router = express.Router();

import {
  getOwnPurchases,
  getOwnPruchaseById,
  deleteOwnPurchase,
  createOwnPurchases,
  updateOwnPurchase,
} from "../controllers/ownPurchasesController.js";

router.route("/").get(getOwnPurchases).post(createOwnPurchases);

router
  .route("/:id")
  .put(updateOwnPurchase)
  .get(getOwnPruchaseById)
  .delete(deleteOwnPurchase);

export default router;

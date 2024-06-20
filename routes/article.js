import express from "express";
// {checkToken} = require('../middlewares/auth');
import {getAll, getById, getByUserId, postOne, updateById, deleteById, getByTitleId, searchArticle} from "../controllers/article.controller"

const router = express.Router();

router.post("/", postOne);
router.post("/search", searchArticle);
router.get("/", getAll);
router.get("/user/:id", getByUserId);
router.get("/:titleid", getByTitleId);
router.put("/:id", updateById);
router.delete("/:id", deleteById);

export default router;
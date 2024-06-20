const express = require("express"),
  router = express.Router(),
  recipeRouter = require("./recipe"),
  userRouter = require("./user"),
  imageRouter = require("./image");

  
router.use("/recipe", recipeRouter);
router.use("/user", userRouter);
router.use("/image", imageRouter)

module.exports = router;

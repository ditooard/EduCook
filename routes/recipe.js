const express = require("express"),
  router = express.Router(),
  recipeController = require("../controllers/recipe.controller"),
  imageController = require("../controllers/image.controller.js"),
  imageBucket = require("../controllers/gcloudBucket.js"),
  {checkToken} = require("../middlewares/auth.js"),
  multerLib = require("multer")();

  router.get("/getUser", 
    checkToken,  
    recipeController.getByUser);
router.get('/ingredients', recipeController.predict)
router.get("/", recipeController.getAllrecipes);
router.get("/search", recipeController.searchRecipe);
// router.get("/search/ingredients", recipeController.searchRecipeByIngredients);
router.get("/:id" ,recipeController.getrecipeById);

// router.post("/",  
//   checkToken,
  // multerLib.single('image'), imageController.create, recipeController.postOne);
router.post("/",  
  checkToken,
  multerLib.single('image'), imageBucket.bucketUpload, recipeController.postOne);


router.put(
  "/:id",
  checkToken,
  multerLib.single('image'),
  recipeController.updateById,
  imageController.updateImage
);

// router.get("/search", recipeController.searchRecipe);
// router.get("/:judul" ,recipeController.getrecipeByTitle);
router.delete("/:id", 
  checkToken,  
  recipeController.deleteById, imageController.deleteImage);

// SEARCH
// localhost:3000/api/v1/recipe/search/modul

module.exports = router;
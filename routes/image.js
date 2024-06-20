const express = require("express"),
  router = express.Router(),
  imageController = require("../controllers/image.controller.js");

router.get("/:imageId" ,imageController.getImageById);

module.exports = router;
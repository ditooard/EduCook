const { Images } = require("../models");
const {imageKit}  = require("../utils");

module.exports = {
    create: async (req, res, next) => {
        try {
            let stringFile = undefined;
            if(req.file) stringFile = req.file.buffer.toString('base64');

            if(stringFile){
                const uploadFile = await imageKit.upload({
                    fileName: Date.now() + '-' + req.file.originalname,
                    file: stringFile,
                })

                const image = await Images.create({
                    data: {
                        url: uploadFile.url,
                        title: req.file.originalname,
                        metadata: JSON.stringify(uploadFile)
                    }
                })

                res.locals.data = image;
                next();
                return;
            }

            throw new Error("failed to upload image");
        } catch (error) {
            console.log(error.message);
            return res.status(500).json({
                error : "Something went wrong were trying to upload"
            })
        }
    },

    getImageById: async (req, res, next) => {
        try {
            const image = await Images.findUnique({
                where: {
                    id: req.params.imageId
                }
            })

            // res.locals.data = image;
            // next();   
            return res.status(200).json({
                message: "success",
                data: image
            }) 
        } catch (error) {
            console.log(error.message);
            return res.status(500).json({
                error : "Something went wrong"
            })           
        }
    },

    updateImage: async (req, res) => {
        try {
            const recipe = res.locals.data;
            
            const imageId = recipe.imageId;

            const imageData = await Images.findUnique({
                where: {
                    id: imageId
                }
            })

            let stringFile = undefined;
            if(req.file) stringFile = req.file.buffer.toString('base64');

            if(stringFile){
                const deletedFile = await imageKit.deleteFile(JSON.parse(imageData.metadata).fileId, (err, res) => {
                    if(err){
                        throw new Error(err.message);
                    }
                })
                const uploadFile = await imageKit.upload({
                    fileName: Date.now() + '-' + req.file.originalname,
                    file: stringFile
                })
                const image = await Images.update({
                    data: {
                        url: uploadFile.url,
                        title: req.file.originalname,
                        metadata: JSON.stringify(uploadFile)
                    },
                    where: {
                        id: imageId
                    }
                })
            }
            return res.status(200).json({
              recipe
            });            
        } catch (error) {
            console.log(error);
            return res.status(500).json({
                error: "Something went wrong"
            })            
        }
    },

    deleteImage: async (req, res, next) => {
        try {
            const {data, image} = res.locals.data;
            let deletedFile;
            if(image){
                deletedFile = await imageKit.deleteFile(image.metadata.fileId, (err, res) => {
                    if(err){
                        throw err;
                    }
                })
            }
            
            return res.status(204).json({
                status: "deleted",
                data: data,
                metadata: deletedFile
            })
        } catch (error) {
            return res.status(204).json({
                status: "deleted",
                data : "successfully deleted "
            })
        }
    }
}
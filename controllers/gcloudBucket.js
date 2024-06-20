const { Storage } = require("@google-cloud/storage");
const { Images } = require("../models");
const uuid = require('uuid');
// const { Extensions } = require("@prisma/client/runtime/library");
const storage = new Storage({
  projectId: "test-api-425700",
  keyFilename: "/root/tempat/storage_api/dollar-426415-63eecbb75032.json",
});
const bucketName = 'educooks'
const bucket = storage.bucket(bucketName)

// Sending the upload request
// req.file.buffer
module.exports = {
    bucketUpload: async (req, res, next) => {
        try {
            let urlGambar = '';
            const namaFile = uuid.v4();
            const extension = req.file.mimetype.split('/')[1];
            const fileName = `${namaFile}.${extension}`;
            console.log(fileName);
            const file = bucket.file(fileName);

            // Upload the buffer to Google Cloud Storage
            await file.save(req.file.buffer, {
                contentType: req.file.mimetype,
                resumable: false,
                metadata: {
                metadata: {
                    originalname: req.file.originalname
                }
                }
            });
            const downloadUrl = `https://storage.googleapis.com/${bucketName}/${fileName}`;
            urlGambar = downloadUrl;
            // bucket.upload(
            //     req.file.buffer,{
            //         destination: namaFile+extenstion.substring(extenstion.indexOf('/'),extenstion.length),
            //     },
            //     function (err, file) {
            //         if (err) {
            //             console.error('Error uploading image image_to_upload.jpeg: '+err)
            //         } else {
            //             const downloadUrl = `https://storage.googleapis.com/${bucketName}/${file.name}`;
            //             console.log('Image image_to_upload.jpeg uploaded to '+bucketName)
            //         //   console.log('Image image_to_upload.jpeg uploaded to', bucketName);
            //             console.log('Download link:', downloadUrl);
            //             urlGambar = downloadUrl;

            //         }
            //     }
            // )

        

            const image = await Images.create({
                data: {
                    url: urlGambar,
                    title: req.file.originalname,
                    metadata: "{tes}"
                }
            })
            res.locals.data = image;
            next();
            return;

        }
        catch(error) {
            console.log(error.message);
            return res.status(500).json({
                error : "Something went wrong were trying to upload"
            })
        }
    }
}

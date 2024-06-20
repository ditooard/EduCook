const bcrypt = require("bcrypt");
const { Users, Profiles, Images, Transactions } = require("../models");
const { imageKit } = require("../utils");

async function hashPassword(plaintextPassword) {
  const hash = await bcrypt.hash(plaintextPassword, 10);
  return hash;
}


const services = {
  getDetail: async (userId) =>
    await Users.findUnique({
      where: {
        id: userId,
      },
      select: {
        id: true,
        email: true,
        phone: true,
        role :{
          select : {
            id : true,
            name : true
          }
        },
        profile: {
          select: {
            name: true,
            country: true,
            city: true,
            image: true,
          },
        },
      },
    }),
  updateUser: async ({ userId, phone, email, password }) =>
    await Users.update({
      where: {
        id: userId,
      },
      data: {
        phone,
        email,
        password,
      },
    }),
  updateProfile: async ({ userId, name, country, city, imageId }) =>
    await Profiles.update({
      where: {
        userId,
      },
      data: {
        name,
        country,
        city,
        imageId,
      },
    }),
  updateProfileImage: async ({ profileId, imageId }) =>
    await Profiles.update({ where: { id: profileId }, data: { imageId } }),
  getImage: async ({ id }) =>
    await Images.findUnique({
      where: {
        id,
      },
    }),
  deleteImage: async ({ id }) =>
    await Images.delete({
      where: {
        id,
      },
    }),
  createImage: async ({ url, title, imagekit_id, size, type }) =>
    await Images.create({
      data: {
        url,
        title,
        metadata: {
          imagekit_id,
          size,
          type,
        },
      },
    }),
  findUserByEmail: async ({ email }) => Users.findUnique({ where: { email } }),
  findUserById: async ({ id }) => Users.findUnique({ where: { id } }),
  getProfileById: async (profileId) =>
    await Profiles.findUnique({
      where: { id: profileId },
      include: {
        image: {
          select: {
            url: true,
          },
        },
      },
    }),
};

module.exports = {
  getUserById: async (req, res) => {
    try {
      const { id: userId } = res.user;


      const user = await Users.findUnique({where: {id:userId}})
      
      if (!user) {
        return res.status(404).json({ error: "Profile not found!" });
      }

      return res.status(200).json({ message: "success", data: user });
    } catch (err) {
      console.log(err);
      return res.status(500).json({ error: "Something went wrong" });
    }
  },
  updateUserById: async (req, res) => {
    try {
      const { name, email } = req.body;
      const { id: userId } = res.user;

      if (email) {
        const user = await Users.findUnique({where:{email}})

        if (user.id !== userId && user.email === email) {
          return res.status(400).json({ error: "email already used!" });
        }
      }

      const profile = await Users.update({
        where: {
          id: userId,
        },
        data: {
          name,
          email,
        },
      })

      if (req.file) {
        let uploadFile;
        const fileToString = req.file.buffer.toString("base64");

        // Hapus gambar sebelumnya jika ada
        if (profile.imageId) {
          const image = await Images.findUnique({
            where: {
              id: profile.imageId,
            },
          });
          await services.deleteImage({ id: profile.imageId });
          await imageKit.deleteFile(JSON.parse(image.metadata).imagekit_id, (err, res) => {
            if (err) console.log(err);
            else console.log(res);
          });
        }

        uploadFile = await imageKit.upload({
          fileName: req.file.originalname,
          file: fileToString,
        });
        const metadata = {
          imagekit_id: uploadFile.fileId,
          size: req.file.size,
          type: req.file.mimetype,
        }
        // Simpan data image ke tabel images
        const createdImage = await Images.create({
          data: {
            url: uploadFile.url,
            title: req.file.originalname,
            metadata: JSON.stringify(metadata),
          },
        });

        await Users.update({ where: { id: profile.id }, data: { 
          Images: {
            connect : {id: createdImage.id}
          }
         }});
      }

      const updatedProfile = await Users.findUnique({
        where: { id: profile.id },
        include: {
          Images: {
            select: {
              url: true,
            },
          },
        },
      });

      return res.status(200).json({
        message: "Profile updated successfully",
        profile: updatedProfile,
      });
    } catch (err) {
      console.log(err);
      return res.status(500).json({ error: "Something went wrong" });
    }
  },
  changePwd: async (req, res) => {
    try {
      const { oldPassword, newPassword } = req.body;
      const { id: userId } = res.user;

      if (oldPassword === newPassword) {
        return res.status(400).json({
          error: "The old password cannot be the same as the new password!",
        });
      }

      if (!oldPassword || !newPassword) {
        return res.status(400).json({
          error: "oldPassword, newPassword are required!",
        });
      }

      const user = await services.findUserById({ id: userId });

      if (!bcrypt.compareSync(oldPassword, user.password)) {
        return res.status(400).json({
          error: "Old password not same!",
        });
      }
      const hashed = await hashPassword(newPassword);

      await services.updateUser({ userId, password: hashed });

      return res.status(200).json({
        message: "Password updated!",
      });
    } catch (err) {
      return res.status(500).json({ error: "Something went wrong" });
    }
  },

  getUserTransaction: async (req, res) => {
    try {
      const userId = res.locals.userId;

      // Temukan semua transaksi yang dilakukan oleh pengguna
      const transaction = await Transactions.findMany({
        where: {
          userId,
        },
        include: {
          course: {
            select: {
              id: true,
              title: true,
              instructor: true,
              courseType: true,
              level : true,
              image : {
                select : {
                  url : true,
                }
              },
              category : {
                select : {
                  name : true
                }
              },
              review : {
                select : {
                  score : true
                }
              }
            },
          },
        },
      });

      if (transaction.length === 0) {
        return res.status(400).json({
          error : "You haven't purchased any courses yet",
          transaction
        });
      } else {
        return res.json({
          transaction,
        });
      }
    } catch (error) {
      console.log(error);
      return res.status(500).json({ error : "Something went wrong" });
    }
  },

};

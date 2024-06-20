const { Users, Profiles } = require("../models");
const bcrypt = require("bcrypt");
const jwt = require("jsonwebtoken");
const nodemailer = require("nodemailer");
const { revokeToken } = require("../middlewares/auth");
const axios = require("axios");
const https = require("https");


async function hashPassword(plaintextPassword) {
  const hash = await bcrypt.hash(plaintextPassword, 10);
  return hash;
}

const cryptPassword = async (password) => {
  const salt = await bcrypt.genSalt(5);
  const hash = await bcrypt.hash(password, salt);
  const encrypted = hash.replace(/\//g, "");
  return encrypted;
};

function generateOTP() {
  var digits = "0123456789";
  let OTP = "";
  for (let i = 0; i < 6; i++) {
    OTP += digits[Math.floor(Math.random() * 10)];
  }
  return OTP;
}

const agent = new https.Agent({
  rejectUnauthorized: false,
});

module.exports = {
  getAllRole: async (req, res) => {
    try {
      const roles = await Roles.findMany();
      return res.status(201).json({
        roles,
      });
    } catch (error) {
      console.log(error);
      return res.status(500).json({ error: "Something went wrong" });
    }
  },

  createRole: async (req, res) => {
    try {
      const role = await Roles.create({
        data: {
          name: req.body.name,
        },
      });
      return res.status(201).json({
        role,
      });
    } catch (error) {
      console.log(error);
      return res.status(500).json({ error: "Something went wrong" });
    }
  },

  register: async (req, res) => {
    try {
      const existUser = await Users.findUnique({
        where: {
          email: req.body.email,
        },
      });

      if (existUser) {
        return res.status(400).json({
          error: "Email already registered",
        });
      }

      let date = new Date();
      date.setMinutes(date.getMinutes() + 5);
      date.toISOString();
      const hashed = await hashPassword(req.body.password);
      const user = await Users.create({
        data: {
          email: req.body.email,
          password: hashed,
          name: req.body.name,
          status: "active",
          role: "USER",
        },
      });

      const { password, ...userWithoutPassword } = user;


        return res.status(201).json({
          message: "account is created",
          user : userWithoutPassword,})

    } catch (error) {
      console.log(error.message);
      return res.status(500).json({
        error: "Something went wrong",
      });
    }
  },


  login: async (req, res) => {
    try {
      const findUser = await Users.findFirst({
        where: {
          email: req.body.email,
        },
      });

      if (!findUser) {
        return res.status(404).json({
          error: "Email not registered",
        });
      }

      if (findUser.status === "inactive") {
        return res.status(401).json({
          error: "Account is not activated, please enter OTP",
        });
      }

      if (bcrypt.compareSync(req.body.password, findUser.password)) {
        const token = jwt.sign(
          {
            id: findUser.id,
            role: findUser.role,
          },
          "secret_key",
          {
            expiresIn: "24h",
          }
        );
        return res.status(200).json({
          data: {
            token,
          },
          id: findUser.id,
          role: findUser.role,
        });
      }

      return res.status(400).json({
        error: "Invalid credentials",
      });
    } catch (error) {
      console.log(error.message);
      res.status(500).json({
        error: "Something went wrong",
      });
    }
  },

  /*
  loginGoogle: async (req, res) => {
    try {
      const { access_token } = req.body;

      if (!access_token) {
        return res.status(400).json({ error: "Access Token is required" });
      }

      const {data} = await axios.get(
        `https://www.googleapis.com/oauth2/v3/userinfo?access_token=${access_token}`,
        { httpsAgent: agent },
      );

      // Validasi apakah user telah membuat akun
      let user = await Users.findFirst({
        where : {
          email : data.email,
        },
        include : {
          profile: true,
          role : true
        }
      })

      // Jika user belum memiliki akun pada email tersebut
      if (!user) {
         user = await Users.create({
          data: {
            email : data.email,
            password: "",
            status: "active",
            phone: "",
            profile: {
              create: {
                name: data.name,
              },
            },
            role: 2,
          },
          include: {
            profile: true,
          },
        });
      }

      const { password, ...userWithoutPassword } = user;

      const token = jwt.sign(
        {
          id: user.id,
          role: user.role,
        },
        "secret_key",
        {
          expiresIn: "24h",
        }
      );

      return res.status(200).json({
        user: userWithoutPassword,
        token,
      });
    } catch (error) {
      console.log(error);
      res.status(500).json({
        error: "Something went wrong",
      });
    }
  },*/
  
  logout: async (req, res) => {
    const token = req.headers.authorization;
    try {
      if (!token) {
        return res.status(400).json({
          error: "please provide a token",
        });
      }

      let tokenValue = token;

      if (tokenValue.toLowerCase().startsWith("bearer")) {
        tokenValue = tokenValue.slice(6).trim();
      }

      revokeToken(tokenValue);

      return res.status(200).json({
        message: "Logout successful",
      });
    } catch (error) {
      console.log(error);
      res.status(500).json({
        error: "Something went wrong",
      });
    }
  },

  resetPassword: async (req, res) => {
    try {
      const findUser = await Users.findFirst({
        where: {
          email: req.body.email,
        },
        include: {
          profile: true,
        },
      });

      if (!findUser) {
        return res.status(400).json({
          error: "User not found",
        });
      }

      const encrypt = await cryptPassword(req.body.email);

      await Users.update({
        data: {
          resetToken: encrypt,
        },
        where: {
          id: findUser.id,
        },
      });

      const transporter = nodemailer.createTransport({
        host: "smtp.gmail.com",
        port: 465,
        secure: true,
        auth: {
          user: process.env.EMAIL_USER,
          pass: process.env.EMAIL_PASSWORD,
        },
      });

      const mailOptions = {
        from: "system@gmail.com",
        to: req.body.email,
        subject: "Reset Password",
        html: `<div
        style="
          text-align: center;
          padding: 1rem;
          border-radius: 5px;
          background-color: #6148ff;
          color: white;
          font-family: 'Montserrat', Tahoma, Geneva, Verdana, sans-serif;
        "
      >
        <h1>Reset Password</h1>
        <img
          src="https://i.imgur.com/nhNpkBd.png"
          alt="One Academy"
          style="width: 15dvw"
        />
        <div
          style="
            background-color: white;
            border-radius: 10px;
            padding: 1rem;
            margin-bottom: 20px;
            color: black;
            max-width: 60dvw;
            max-height: 60dvh;
            margin-top: 10px;
            margin: 0 auto;
          "
        >
          <p>Hello <span style="font-weight: 700">${findUser.profile.name},</span></p>
  
          <p style="margin-bottom: 15px">
            We received a request to reset your account password. To proceed with
            the password reset, please click reset password button bellow:
          </p>
  
          <a
            href="https://EduCook.pemudasukses.tech/forgot/${encrypt}"
            style="
              background-color: #6148ff;
              color: white;
              padding: 10px;
              border-radius: 5px;
              text-decoration: none;
            "
            ><strong>Reset Password</strong></a
          >
  
          <p>
            Please note that this verification code is valid for a limited time.
            If you did not initiate this password reset or have any concerns,
            please contact our support team immediately.
          </p>
        </div>
  
        <p>
          Thank you for choosing EduCook!<br />
          © 2023, One Academy. All rights reserved.
        </p>
      </div>`,
      };

      transporter.sendMail(mailOptions, (err) => {
        if (err) {
          console.log(err);
          return res.status(400).json({
            error: "Something went wrong",
          });
        }

        return res.status(200).json({
          message: "email sent"
        });
      });
    } catch (error) {
      console.log(error);
      return res.status(500).json({
        error: "Something went wrong",
      });
    }
  },

  setPassword: async (req, res) => {
    try {
      const findUser = await Users.findFirst({
        where: {
          resetToken: req.body.key,
        },
      });

      if (!findUser) {
        return res.status(400).json({
          error: "User not found",
        });
      }

      await Users.update({
        data: {
          password: await cryptPassword(req.body.password),
          resetToken: null,
        },
        where: {
          id: findUser.id,
        },
      });

      return res.status(200).json({
        message: "Password has changed",
      });
    } catch (error) {
      console.log(error);
      return res.status(500).json({
        error: "Something went wrong",
      });
    }
  },
};

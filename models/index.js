const { PrismaClient } = require("@prisma/client");

const prisma = new PrismaClient();

module.exports = {
  Users: prisma.users,
  Recipes: prisma.recipes,
  Likes: prisma.likes,
  Images: prisma.images,
};

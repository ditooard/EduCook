FROM node:20

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

ENV PORT=5000
ENV DATABASE_URL="postgresql://postgres:bayu@localhost:5432/bangkit"
ENV IMAGEKIT_PUBLIC_KEY="public_YtieyBkImxy+vq4oDgUF+3I84mc="
ENV IMAGEKIT_SECRET_KEY="private_OMzH3CDuZZlxOdLMgGYxRiItZWk="
ENV IMAGEKIT_URL_ENDPOINT="https://ik.imagekit.io/r5tbc6e6r"
ENV EMAIL_USER="bayuuabdur2903@gmail.com"
ENV EMAIL_PASSWORD="ppzd elto uqpf twsm"

EXPOSE 5000

CMD ["npm", "start"]
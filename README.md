# EduCook
![PPT Capstone Project](https://github.com/ditooard/EduCook/assets/91383806/9601f77f-cd05-4d05-8032-140fd4af3ce9)
Welcome to the EduCook App! The project leverages image recognition technology to provide users with personalized recipe recommendations based on available ingredients. Whether you're a culinary novice or an experienced chef, this app will help you discover new and interesting dishes to try.

## Authors
> Machine Learning
- [Varell Anthonio](https://github.com/VarellAnthonio)
- [Muhammad Daffa Fahreza](https://github.com/daffafahreza4)
- [Zolly Citra Prayogi](https://github.com/zollycp87)
> Mobile Development
- [Dito Ardi Pratama](https://github.com/ditooard)
- [Meida Dela Risyafa Auliya](https://github.com/meidadela)
> Cloud Computing
- [Muhamad Abdullah](https://github.com/AexonJP)
- [Bayu Abdurrosyid](https://github.com/TehBotolBayu)
  
## Built With
- TensorFlow Lite: Lightweight library for deploying machine learning models on mobile devices.
- TensorFlow Keras: Library for building and training deep learning models.
- Retrofit: HTTP client for Android and Kotlin.
- Glide: Image loading and caching library for Android.

## Features
- **Image Recognation**: Upload an image of ingredient, and the application will identify them and suggest recipes.
- **Recommendation**: Get recipe suggestions tailored to your tastes and available ingredients.
- **Add Recipe**: Users can add their own recipes.
- **Search**: Find the recipe you want.
- **Detail**: View detailed information about each recipe, including ingredients and steps.
- **Bookmark**: Save recipes to your bookmarks for easy access later.

## API Reference

#### Get recipe by page

```http
  GET /api/recipe?page=0
```

| Parameter | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `page` | `integer` | Retrieve a paginated list of recipes |

#### Get recipe by id

```http
  GET /api/recipe/${id}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `string` | Retrieve recipe by its unique ID |

#### Get recipe by ingredients

```http
  GET /api/recipe/ingredients?query=${ingredients}
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `ingredients`      | `string` | Retrieve recipe by ingredients |

#### Get recipe by user

```http
  GET /api/recipe/getUser
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
|       |  | Retrieve recipe the currently authenticated user |

#### Get image by id

```http
  GET /api/image
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
|       |  | Retrieve image by id |

#### Post recipe

```http
  POST /api/recipe
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
|       |  | Add a new recipe to the database |

#### Post register

```http
  POST /api/user/register
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `name`     | `string` | The new name of the user |
| `email`     | `string` | The new email address of the user |
| `password`     | `string` | The new password of the user. |

#### Post login

```http
  POST /api/user/login
```

| Parameter | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `email`     | `string` | The email address of the user |
| `password`     | `string` | The password of the user |

## Demo
Demo application:
https://youtu.be/FXUIHZT0dJk?si=dSO2yNpBRQ7_d2hI

Happy cooking! 🥘👨‍🍳👩‍🍳

package com.bangkit2024.educook.data.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable
import java.util.*

data class RecipeResponse(
    @SerializedName("data") val data: List<Recipe>,
    @SerializedName("pagination") val pagination: Pagination
)

data class Recipe(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("directions") val directions: String,
    @SerializedName("ingredients") val ingredients: String,
    @SerializedName("createdAt") val createdAt: Date,
    @SerializedName("updatedAt") val updatedAt: Date,
    @SerializedName("imageId") val imageId: String,
    @SerializedName("idUser") val idUser: String,
    var imageUrl: String? = null // Tambahkan properti ini
) : Serializable

data class Pagination(
    @SerializedName("hasNextPage") val hasNextPage: Boolean
)

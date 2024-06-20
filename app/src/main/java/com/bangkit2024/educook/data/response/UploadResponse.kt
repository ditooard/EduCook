package com.bangkit2024.educook.data.response

import com.google.gson.annotations.SerializedName

data class UploadResponse(

    @field:SerializedName("data")
    val data: DataUpload,

    @field:SerializedName("message")
    val message: String
)

data class DataUpload(

    @field:SerializedName("idUser")
    val idUser: String,

    @field:SerializedName("createdAt")
    val createdAt: String,

    @field:SerializedName("imageId")
    val imageId: String,

    @field:SerializedName("directions")
    val directions: String,

    @field:SerializedName("ingredients")
    val ingredients: String,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("title")
    val title: String,

    @field:SerializedName("updatedAt")
    val updatedAt: String
)

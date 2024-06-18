package com.bangkit2024.educook.data.response

data class ImageResponse(
    val message: String,
    val data: ImageData
)

data class ImageData(
    val id: String,
    val title: String,
    val url: String,
    val metadata: String,
    val createdAt: String,
    val updatedAt: String
)

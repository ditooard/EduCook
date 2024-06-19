package com.bangkit2024.educook.data.response

data class RecommendResponse(
	val resep: List<ResepItem>,
	val prediksi: String
)

data class ResepItem(
	val idUser: String,
	val createdAt: String,
	val imageId: String,
	val directions: String,
	val ingredients: String,
	val id: String,
	val title: String,
	val updatedAt: String
)


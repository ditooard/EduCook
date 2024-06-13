package com.bangkit2024.educook.data.response

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
	@field:SerializedName("error")
	val error: String? = null
)

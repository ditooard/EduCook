package com.bangkit2024.educook.data.remote.response

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

	@field:SerializedName("error")
	val error: Boolean?,

	@field:SerializedName("message")
	val message: String?
)

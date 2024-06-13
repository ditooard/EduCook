package com.bangkit2024.educook.api.model

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)
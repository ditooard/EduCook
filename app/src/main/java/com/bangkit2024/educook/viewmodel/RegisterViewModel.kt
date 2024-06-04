package com.bangkit2024.educook.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit2024.educook.data.UserRepository

class RegisterViewModel(private val userRepository: UserRepository): ViewModel() {
    suspend fun register(name: String, email: String, password: String) =
        userRepository.register(name, email, password)
}
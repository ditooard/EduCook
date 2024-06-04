package com.bangkit2024.educook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit2024.educook.data.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {
    suspend fun login(email: String, password: String) =
        userRepository.login(email, password)

    fun saveToken(token: String) {
        viewModelScope.launch {
            userRepository.saveToken(token)
        }
    }
}
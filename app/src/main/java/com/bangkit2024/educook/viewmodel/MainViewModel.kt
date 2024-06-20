package com.bangkit2024.educook.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit2024.educook.data.UserRepository
import kotlinx.coroutines.flow.Flow

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun getToken(): Flow<String> {
        return userRepository.getToken()
    }
}
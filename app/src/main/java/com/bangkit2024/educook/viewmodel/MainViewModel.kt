package com.bangkit2024.educook.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit2024.educook.data.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository): ViewModel() {
    fun getToken(): Flow<String> {
        return userRepository.getToken()
    }
}
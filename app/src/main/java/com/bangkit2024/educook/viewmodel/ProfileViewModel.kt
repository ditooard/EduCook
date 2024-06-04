package com.bangkit2024.educook.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit2024.educook.data.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository): ViewModel() {
    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}
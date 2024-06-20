package com.bangkit2024.educook.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit2024.educook.data.UserRepository
import com.bangkit2024.educook.data.response.RecipeUserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
        }
    }

    suspend fun getRecipesByUser(token: String): LiveData<Result<RecipeUserResponse>> {
        return userRepository.getRecipesByUser(token)
    }

    fun getToken(): Flow<String> {
        return userRepository.getToken()
    }
}

package com.bangkit2024.educook.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit2024.educook.data.UserRepository
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

class AddViewModel(private val userRepository: UserRepository) : ViewModel() {
    fun getToken(): Flow<String> {
        return userRepository.getToken()
    }

    suspend fun addRecipe(token: String, file: MultipartBody.Part, desc: RequestBody) =
        userRepository.addRecipe(token, file, desc)
}
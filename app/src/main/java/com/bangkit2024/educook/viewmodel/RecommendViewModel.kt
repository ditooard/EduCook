package com.bangkit2024.educook.viewmodel

import androidx.lifecycle.ViewModel
import com.bangkit2024.educook.data.UserRepository
import okhttp3.MultipartBody

class RecommendViewModel(private val userRepository: UserRepository): ViewModel() {
    suspend fun predictImage(image: MultipartBody.Part) =
        userRepository.predictImage(image)

    fun getRecipesByPrediction(prediction: String) =
        userRepository.getRecipesByPrediction(prediction)
}
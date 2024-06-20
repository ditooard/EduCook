package com.bangkit2024.educook.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit2024.educook.data.UserRepository
import com.bangkit2024.educook.data.response.RecommendResponse
import okhttp3.MultipartBody

class RecommendViewModel(private val userRepository: UserRepository) : ViewModel() {
    suspend fun predictImage(image: MultipartBody.Part) =
        userRepository.predictImage(image)

    fun getRecipesByPrediction(prediction: String): LiveData<Result<RecommendResponse?>> {
        val resultLiveData = MutableLiveData<Result<RecommendResponse?>>()
        userRepository.getRecipesByPrediction(prediction) { result ->
            resultLiveData.value = result
        }
        return resultLiveData
    }
}
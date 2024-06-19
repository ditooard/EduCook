package com.bangkit2024.educook.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bangkit2024.educook.data.local.UserPreference
import com.bangkit2024.educook.data.response.LoginResponse
import com.bangkit2024.educook.api.ApiService
import com.bangkit2024.educook.api.model.LoginRequest
import com.bangkit2024.educook.api.model.RegisterRequest
import com.bangkit2024.educook.data.response.ErrorResponse
import com.bangkit2024.educook.data.response.PredictResponse
import com.bangkit2024.educook.data.response.RecommendResponse
import com.bangkit2024.educook.data.response.RegisterResponse
import com.bangkit2024.educook.data.response.UploadResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException

class UserRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
) {
    suspend fun register(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> = liveData {
        try {
            val request = RegisterRequest(name, email, password)
            val response = apiService.register(request)
            emit(Result.success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody?.error
            emit(Result.failure(Throwable(errorMessage)))
        }
    }

    suspend fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        try {
            val request = LoginRequest(email, password)
            val response = apiService.login(request)
            emit(Result.success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody?.error
            emit(Result.failure(Throwable(errorMessage)))
        }
    }

    suspend fun addRecipe(
        token: String,
        image: MultipartBody.Part,
        title: RequestBody,
        ingredients: RequestBody,
        directions: RequestBody
    ): LiveData<Result<UploadResponse>> = liveData{
        try {
            val response = apiService.addRecipe("Bearer $token", image, title, ingredients, directions)
            emit(Result.success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, UploadResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.failure(Throwable(errorMessage)))
        }
    }

    suspend fun predictImage(image: MultipartBody.Part): LiveData<Result<PredictResponse>> = liveData {
        try {
            val response = apiService.predictImage(image)
            emit(Result.success(response))
        } catch (e: HttpException) {
            emit(Result.failure(Throwable(e.message)))
        }
    }

    fun getRecipesByPrediction(prediction: String): LiveData<Result<RecommendResponse>> = liveData {
        try {
            val response = apiService.getRecipesByIngredients(prediction)
            emit(Result.success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody?.error
            emit(Result.failure(Throwable(errorMessage)))
        }
    }

    suspend fun saveToken(token: String) {
        userPreference.saveToken(token)
    }

    fun getToken(): Flow<String> {
        return userPreference.getToken()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference,
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, userPreference)
            }.also { instance = it }
    }
}
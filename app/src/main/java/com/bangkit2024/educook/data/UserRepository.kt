package com.bangkit2024.educook.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bangkit2024.educook.api.ApiService
import com.bangkit2024.educook.api.model.LoginRequest
import com.bangkit2024.educook.api.model.RegisterRequest
import com.bangkit2024.educook.data.local.UserPreference
import com.bangkit2024.educook.data.response.ErrorResponse
import com.bangkit2024.educook.data.response.LoginResponse
import com.bangkit2024.educook.data.response.PredictResponse
import com.bangkit2024.educook.data.response.RecipeUserResponse
import com.bangkit2024.educook.data.response.RecommendResponse
import com.bangkit2024.educook.data.response.RegisterResponse
import com.bangkit2024.educook.data.response.UploadResponse
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response

class UserRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
) {
    suspend fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
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
    ): LiveData<Result<UploadResponse>> = liveData {
        try {
            val response =
                apiService.addRecipe("Bearer $token", image, title, ingredients, directions)
            emit(Result.success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, UploadResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.failure(Throwable(errorMessage)))
        }
    }

    suspend fun predictImage(image: MultipartBody.Part): LiveData<Result<PredictResponse>> =
        liveData {
            try {
                val response = apiService.predictImage(image)
                emit(Result.success(response))
            } catch (e: HttpException) {
                emit(Result.failure(Throwable(e.message)))
            }
        }

    fun getRecipesByPrediction(prediction: String, callback: (Result<RecommendResponse?>) -> Unit) {
        apiService.getRecipesByIngredients(prediction)
            .enqueue(object : Callback<RecommendResponse> {
                override fun onResponse(
                    call: Call<RecommendResponse>,
                    response: Response<RecommendResponse>
                ) {
                    if (response.isSuccessful) {
                        callback(Result.success(response.body()))
                    } else {
                        val errorMessage = "Failed to fetch recipes: ${response.code()}"
                        callback(Result.failure(Throwable(errorMessage)))
                    }
                }

                override fun onFailure(call: Call<RecommendResponse>, t: Throwable) {
                    callback(Result.failure(t))
                }
            })
    }

    suspend fun getRecipesByUser(token: String): LiveData<Result<RecipeUserResponse>> = liveData {
        try {
            withContext(Dispatchers.IO) {
                val response = apiService.getUserRecipes("Bearer $token").execute()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (body != null) {
                        emit(Result.success(body))
                    } else {
                        emit(Result.failure(Throwable("Empty response body")))
                    }
                } else {
                    val jsonInString = response.errorBody()?.string()
                    val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
                    val errorMessage = errorBody?.error ?: "Unknown error"
                    emit(Result.failure(Throwable(errorMessage)))
                }
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
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





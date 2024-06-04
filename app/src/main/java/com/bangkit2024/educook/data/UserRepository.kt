package com.bangkit2024.educook.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bangkit2024.educook.data.local.UserPreference
import com.bangkit2024.educook.data.remote.response.LoginResponse
import com.bangkit2024.educook.data.remote.response.RegisterResponse
import com.bangkit2024.educook.data.remote.retrofit.ApiService
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepository(
    private val apiService: ApiService,
    private val userPreference: UserPreference,
) {
    suspend fun register(name: String, email: String, password: String): LiveData<Result<RegisterResponse>> = liveData {
        try {
            val response = apiService.register(name, email, password)
            emit(Result.success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, RegisterResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.failure(Throwable(errorMessage)))
        }
    }
    suspend fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        try {
            val response = apiService.login(email, password)
            emit(Result.success(response))
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, LoginResponse::class.java)
            val errorMessage = errorBody.message
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
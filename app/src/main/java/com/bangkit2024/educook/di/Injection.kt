package com.bangkit2024.educook.di

import android.content.Context
import com.bangkit2024.educook.data.UserRepository
import com.bangkit2024.educook.data.local.UserPreference
import com.bangkit2024.educook.data.local.dataStore
import com.bangkit2024.educook.data.remote.retrofit.ApiConfig

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val apiService = ApiConfig.getApiService()
        return UserRepository.getInstance(apiService, pref)
    }
}
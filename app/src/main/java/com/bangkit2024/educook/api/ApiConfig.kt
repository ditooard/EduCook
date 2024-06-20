package com.bangkit2024.educook.api

import android.util.Log
import com.bangkit2024.educook.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

class ApiConfig {
    companion object {
        private const val TAG = "ApiConfig"

        fun getApiService(): ApiService {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            }

            val authInterceptor = Interceptor { chain ->
                val original: Request = chain.request()
                val request: Request = original.newBuilder()
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS) // Timeout for establishing connection
                .readTimeout(30, TimeUnit.SECONDS) // Timeout for reading response
                .writeTimeout(30, TimeUnit.SECONDS) // Timeout for writing request
                .retryOnConnectionFailure(true) // Retry on connection failures
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }

        // Function to handle specific exceptions and logging
        private fun handleApiExceptions(exception: Throwable) {
            when (exception) {
                is SocketTimeoutException -> {
                    Log.e(TAG, "Timeout exception: ${exception.message}")
                    // Handle timeout exception here
                }

                is IOException -> {
                    Log.e(TAG, "IOException: ${exception.message}")
                    // Handle IOException here
                }

                else -> {
                    Log.e(TAG, "Other exception: ${exception.message}")
                    // Handle other exceptions here
                }
            }
        }
    }
}

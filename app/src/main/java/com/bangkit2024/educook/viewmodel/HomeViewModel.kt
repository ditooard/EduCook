package com.bangkit2024.educook.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bangkit2024.educook.api.ApiConfig
import com.bangkit2024.educook.data.response.DetailMenu
import com.bangkit2024.educook.data.response.MenuResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel : ViewModel() {

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _stories = MutableLiveData<List<DetailMenu>>()
    val stories: LiveData<List<DetailMenu>> get() = _stories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _isError = MutableLiveData<Boolean>()
    val isError: LiveData<Boolean> get() = _isError

    fun fetchStories(token: String) {
        _isLoading.value = true
        ApiConfig.getApiService().fetchStories("Bearer $token")
            .enqueue(object : Callback<MenuResponse> {
                override fun onResponse(
                    call: Call<MenuResponse>,
                    response: Response<MenuResponse>
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        response.body()?.let {
                            _stories.value = it.listStory
                            _message.value = it.message
                            _isError.value = false
                        } ?: run {
                            _message.value = "Response is empty"
                            _isError.value = true
                        }
                    } else {
                        _message.value = response.message()
                        _isError.value = true
                    }
                }

                override fun onFailure(call: Call<MenuResponse>, t: Throwable) {
                    _isLoading.value = false
                    _message.value = t.message
                    _isError.value = true
                }
            })
    }
}

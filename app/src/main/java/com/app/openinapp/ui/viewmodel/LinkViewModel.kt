package com.app.openinapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.openinapp.data.model.ApiResponse
import com.app.openinapp.data.repository.LinkRepositoryImpl
import kotlinx.coroutines.launch

class LinkViewModel : ViewModel() {
    private val _data = MutableLiveData<ApiResponse>()
    val data: LiveData<ApiResponse> get() = _data
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> get() = _loading

    private val linkRepository = LinkRepositoryImpl()

    fun fetchData() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = linkRepository.fetchData()
                _data.value = response

            } catch (e: Exception) {
                Log.e("ViewModel", e.message.toString(), e)
            } finally {
                _loading.value = false
            }
        }
    }
}


package com.app.openinapp.data.repository

import com.app.openinapp.data.model.ApiResponse
import com.app.openinapp.utils.RetrofitClient

class LinkRepositoryImpl: LinkRepository {
    override suspend fun fetchData(): ApiResponse {
        try {
            val response = RetrofitClient.apiService.getData()
            if (response.isSuccessful) {
                return response.body() ?: throw Exception("Response body is null")
            } else {
                throw Exception("Error loading data!")
            }
        } catch (e: Exception) {
            throw Exception(e.message.toString(), e)
        }
    }
}

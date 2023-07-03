package com.app.openinapp.data.repository

import com.app.openinapp.data.model.ApiResponse

interface LinkRepository {
    suspend fun fetchData(): ApiResponse
}
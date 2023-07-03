package com.app.openinapp.utils

import com.app.openinapp.data.model.ApiResponse
import retrofit2.Response
import retrofit2.http.GET

interface ApiService {
    @GET("api/v1/dashboardNew")
    suspend fun getData(): Response<ApiResponse>
}
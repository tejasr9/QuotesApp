package com.tejas.quotesapp.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface QuoteApiService {
    @GET("quotes")
    suspend fun getQuotes(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): QuotesListResponse
} 
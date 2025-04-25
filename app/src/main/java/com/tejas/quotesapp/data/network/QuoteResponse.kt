package com.tejas.quotesapp.data.network

import com.google.gson.annotations.SerializedName
import com.tejas.quotesapp.data.Quote

// Single quote response
data class QuoteResponse(
    @SerializedName("_id") val id: String,
    val content: String,
    val author: String,
    val tags: List<String> = emptyList(),
    val length: Int,
    val dateAdded: String,
    val dateModified: String
) {
    fun toQuote(): Quote {
        return Quote(
            id = 0, // Room will auto-generate ID
            text = content,
            author = author,
            isFavorite = false
        )
    }
}

// List of quotes response
data class QuotesListResponse(
    val count: Int,
    val totalCount: Int,
    val page: Int,
    val totalPages: Int,
    val results: List<QuoteResponse>
) {
    fun toQuoteList(): List<Quote> {
        return results.map { it.toQuote() }
    }
} 
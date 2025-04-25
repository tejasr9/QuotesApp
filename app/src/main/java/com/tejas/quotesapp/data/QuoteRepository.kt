package com.tejas.quotesapp.data

import com.tejas.quotesapp.data.network.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class QuoteRepository(private val quoteDao: QuoteDao) {
    
    private val apiService = RetrofitClient.quoteApiService
    
    val allQuotes: Flow<List<Quote>> = quoteDao.getAllQuotes()
    val favoriteQuotes: Flow<List<Quote>> = quoteDao.getFavoriteQuotes()

    // Fetch quotes from API and store in database
    suspend fun refreshQuotes() {
        withContext(Dispatchers.IO) {
            try {
                val response = apiService.getQuotes(limit = 50)
                val quotes = response.toQuoteList()
                for (quote in quotes) {
                    quoteDao.insertQuote(quote)
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }

    suspend fun insertQuote(quote: Quote) {
        quoteDao.insertQuote(quote)
    }

    suspend fun updateQuote(quote: Quote) {
        quoteDao.updateQuote(quote)
    }

    suspend fun deleteQuote(quote: Quote) {
        quoteDao.deleteQuote(quote)
    }

    suspend fun toggleFavorite(quoteId: Int, isFavorite: Boolean) {
        quoteDao.updateFavoriteStatus(quoteId, isFavorite)
    }
} 
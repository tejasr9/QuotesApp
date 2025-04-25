package com.tejas.quotesapp

import android.app.Application
import android.util.Log
import com.tejas.quotesapp.data.QuoteDatabase
import com.tejas.quotesapp.data.QuoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuotesApplication : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.IO)
    
    // Lazy database instance
    val database by lazy { QuoteDatabase.getDatabase(this) }
    
    // Lazy repository instance
    val repository by lazy { QuoteRepository(database.quoteDao()) }
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize database and repository
        applicationScope.launch {
            try {
                repository.refreshQuotes()
                Log.d("QuotesApplication", "Successfully initialized quotes from API")
            } catch (e: Exception) {
                Log.e("QuotesApplication", "Error initializing quotes: ${e.message}")
            }
        }
    }
} 
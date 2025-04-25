package com.tejas.quotesapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.tejas.quotesapp.data.Quote
import com.tejas.quotesapp.data.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class QuoteViewModel(private val repository: QuoteRepository) : ViewModel() {
    private val _currentTab = MutableStateFlow(QuoteTab.ALL)
    val currentTab: StateFlow<QuoteTab> = _currentTab.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    val quotes = repository.allQuotes
    val favoriteQuotes = repository.favoriteQuotes
    
    init {
        loadQuotes()
    }
    
    fun loadQuotes() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                repository.refreshQuotes()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load quotes: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setCurrentTab(tab: QuoteTab) {
        _currentTab.value = tab
    }

    fun toggleFavorite(quote: Quote) {
        viewModelScope.launch {
            repository.toggleFavorite(quote.id, !quote.isFavorite)
        }
    }

    fun shareQuote(quote: Quote): String {
        return "${quote.text}\n- ${quote.author}"
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}

enum class QuoteTab {
    ALL, FAVORITES
}

class QuoteViewModelFactory(private val repository: QuoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return QuoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
} 
package com.tejas.quotesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tejas.quotesapp.ui.QuoteViewModel
import com.tejas.quotesapp.ui.QuoteViewModelFactory
import com.tejas.quotesapp.ui.screens.QuotesScreen
import com.tejas.quotesapp.ui.theme.QuotesAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val repository = (application as QuotesApplication).repository
        
        setContent {
            QuotesAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: QuoteViewModel = viewModel(
                        factory = QuoteViewModelFactory(repository)
                    )
                    QuotesScreen(viewModel = viewModel)
                }
            }
        }
    }
} 
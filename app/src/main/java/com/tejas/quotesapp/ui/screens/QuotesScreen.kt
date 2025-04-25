package com.tejas.quotesapp.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.tejas.quotesapp.data.Quote
import com.tejas.quotesapp.ui.QuoteTab
import com.tejas.quotesapp.ui.QuoteViewModel
import com.tejas.quotesapp.ui.components.QuoteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuotesScreen(viewModel: QuoteViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val quotes by viewModel.quotes.collectAsState(initial = emptyList())
    val favoriteQuotes by viewModel.favoriteQuotes.collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        viewModel.loadQuotes()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Inspirational Quotes") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { viewModel.loadQuotes() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh quotes"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = currentTab.ordinal,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ) {
                QuoteTab.values().forEach { tab ->
                    Tab(
                        selected = currentTab == tab,
                        onClick = { viewModel.setCurrentTab(tab) },
                        text = {
                            Text(
                                text = when (tab) {
                                    QuoteTab.ALL -> "All Quotes"
                                    QuoteTab.FAVORITES -> "Favorites"
                                }
                            )
                        }
                    )
                }
            }
            
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            
            errorMessage?.let { error ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(onClick = { viewModel.clearError() }) {
                                Text("Dismiss")
                            }
                        }
                    }
                }
            }

            val displayedQuotes = if (currentTab == QuoteTab.ALL) quotes else favoriteQuotes
            
            if (displayedQuotes.isEmpty() && !isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (currentTab == QuoteTab.FAVORITES) 
                            "No favorite quotes yet. Mark some quotes as favorites!" 
                        else 
                            "No quotes available. Tap refresh to load quotes.",
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                items(
                    items = displayedQuotes,
                    key = { it.id }
                ) { quote ->
                    QuoteCard(
                        quote = quote,
                        onFavoriteClick = { viewModel.toggleFavorite(quote) },
                        onShareClick = {
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, viewModel.shareQuote(quote))
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Quote"))
                        }
                    )
                }
            }
        }
    }
} 
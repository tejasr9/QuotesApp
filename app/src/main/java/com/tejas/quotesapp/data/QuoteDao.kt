package com.tejas.quotesapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {
    @Query("SELECT * FROM quotes")
    fun getAllQuotes(): Flow<List<Quote>>

    @Query("SELECT * FROM quotes WHERE isFavorite = 1")
    fun getFavoriteQuotes(): Flow<List<Quote>>

    @Insert
    suspend fun insertQuote(quote: Quote)

    @Update
    suspend fun updateQuote(quote: Quote)

    @Delete
    suspend fun deleteQuote(quote: Quote)

    @Query("UPDATE quotes SET isFavorite = :isFavorite WHERE id = :quoteId")
    suspend fun updateFavoriteStatus(quoteId: Int, isFavorite: Boolean)
} 
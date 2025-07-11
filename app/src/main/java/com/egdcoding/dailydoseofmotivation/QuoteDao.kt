package com.egdcoding.dailydoseofmotivation

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuoteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quote: Quote): Long

    @Query("SELECT * FROM quotes")
    fun getAllQuotes(): Flow<List<Quote>>

    @Query("SELECT * FROM quotes WHERE isFavorite = 1")
    fun getFavoriteQuotes(): Flow<List<Quote>> // Fetch only favorite quotes

    @Query("UPDATE quotes SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Long, isFavorite: Boolean)

    @Query("SELECT * FROM quotes WHERE likeOrWritten = :source")
    fun getQuotesBySource(source: String): Flow<List<Quote>>

    @Delete
    suspend fun delete(quote: Quote)

    @Query("UPDATE quotes SET text = :newText WHERE id = :id")
    suspend fun updateQuote(id: Long, newText: String)

    @Query("UPDATE quotes SET text = :newText WHERE id = :id")
    suspend fun updateQuoteText(id: Long, newText: String)

    @Query("SELECT * FROM quotes WHERE id = :id LIMIT 1")
    fun getQuoteById(id: Long): Flow<Quote?>

}

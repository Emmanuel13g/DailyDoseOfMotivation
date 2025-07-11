package com.egdcoding.dailydoseofmotivation

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class QuoteDatabaseTestTwo {

    private lateinit var db: QuoteDatabase
    private lateinit var quoteDao: QuoteDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            QuoteDatabase::class.java
        ).allowMainThreadQueries().build()

        quoteDao = db.quoteDao()  // Initialize the quoteDao
    }

    @After
    fun tearDown() {
        db.close()  // Close the database after tests
    }

    @Test
    fun removeFromFavoritesTest() = runTest {
        val quote = Quote(text = "Favorite Quote", isFavorite = true, likeOrWritten = "like")
        quoteDao.insert(quote)

        val insertedQuote = quoteDao.getAllQuotes().first().find { it.text == "Favorite Quote" }
        assertNotNull(insertedQuote)

        insertedQuote?.let {
            quoteDao.updateFavoriteStatus(it.id, false)
        }

        val updatedQuote = quoteDao.getAllQuotes().first().find { it.id == insertedQuote?.id }
        assertFalse(updatedQuote?.isFavorite ?: true)
    }
}





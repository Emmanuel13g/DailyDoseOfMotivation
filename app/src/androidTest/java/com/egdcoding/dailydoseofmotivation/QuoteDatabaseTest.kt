package com.egdcoding.dailydoseofmotivation

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.core.app.ApplicationProvider
import androidx.room.Room
import androidx.test.filters.SmallTest
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)  // Ensure Android components are available
@SmallTest
class QuoteDatabaseTest {

    private lateinit var db: QuoteDatabase
    private lateinit var quoteDao: QuoteDao

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            QuoteDatabase::class.java
        ).allowMainThreadQueries().build()

        quoteDao = db.quoteDao()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun insertQuoteTest() = runTest {
        val quote = Quote(text = "Test Quote", isFavorite = false, likeOrWritten = "written")
        quoteDao.insert(quote)

        val fetchedQuotes = quoteDao.getAllQuotes().first()

        assertTrue(fetchedQuotes.any {
            it.text == quote.text &&
                    it.isFavorite == quote.isFavorite &&
                    it.likeOrWritten == quote.likeOrWritten
        })
    }


    @Test
    fun getFavoriteQuotesTest() = runTest {
        val favoriteQuote = Quote(text = "Favorite Quote", isFavorite = true, likeOrWritten = "like")
        quoteDao.insert(favoriteQuote)

        val favoriteQuotes = quoteDao.getFavoriteQuotes().first()

        assertTrue(favoriteQuotes.any { it.text == favoriteQuote.text }) // Compare by text instead of object reference
    }


    @Test
    fun updateQuoteTest() = runTest {
        val quote = Quote(text = "Initial Quote", isFavorite = false, likeOrWritten = "written")
        quoteDao.insert(quote)

        // Fetch the inserted quote to get its correct ID
        val insertedQuote = quoteDao.getAllQuotes().first().find { it.text == "Initial Quote" }
        assertNotNull(insertedQuote)  // Ensure quote is inserted

        // Perform update operation
        insertedQuote?.let {
            quoteDao.updateQuote(it.id, "Updated Quote")
        }

        // Fetch again to verify update
        val updatedQuote = quoteDao.getAllQuotes().first().find { it.id == insertedQuote?.id }
        assertNotNull(updatedQuote)
        assertEquals("Updated Quote", updatedQuote?.text)
    }


    @Test
    fun deleteQuoteTest() = runTest {
        val quote = Quote(text = "Quote to Delete", isFavorite = false, likeOrWritten = "written")
        quoteDao.insert(quote)

        quoteDao.delete(quote)

        val quotes = quoteDao.getAllQuotes().first()
        assertFalse(quotes.contains(quote))
    }
}

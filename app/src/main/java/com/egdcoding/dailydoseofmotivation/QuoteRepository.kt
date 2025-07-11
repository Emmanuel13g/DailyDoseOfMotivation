package com.egdcoding.dailydoseofmotivation

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData

class QuoteRepository(private val quoteDao: QuoteDao) {

    suspend fun insert(quote: Quote) {
        quoteDao.insert(quote)
    }

    fun getQuotesBySource(source: String): LiveData<List<Quote>> {
        return quoteDao.getQuotesBySource(source).asLiveData()
    }

    suspend fun delete(quote: Quote) {
        quoteDao.delete(quote)
    }

    suspend fun updateQuote(id: Long, newText: String) {
        quoteDao.updateQuote(id, newText)
    }

}
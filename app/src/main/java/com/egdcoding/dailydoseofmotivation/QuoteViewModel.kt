package com.egdcoding.dailydoseofmotivation

import android.app.Application

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class QuoteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: QuoteRepository
    var favoriteQuotes: LiveData<List<Quote>> // Expose favorites to the UI
    val userQuotes: LiveData<List<Quote>>
    private val _quotes = mutableStateOf<List<String>>(emptyList())
    val quotes: State<List<String>> = _quotes

    init {
        val quoteDao = QuoteDatabase.getDatabase(application).quoteDao()
        repository = QuoteRepository(quoteDao)
        userQuotes = repository.getQuotesBySource("written")
        favoriteQuotes = repository.getQuotesBySource("like")

    }

    fun insertQuote(quote: Quote) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(quote)
        }
    }

    fun deleteQuote(quote: Quote) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(quote)
        }
    }

    fun updateQuote(id: Long, newText: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateQuote(id, newText)
        }
    }


    fun handleLikeButtonClick(
        quoteText: String,
        isLiked: MutableState<Boolean>,
        favoriteQuotes: List<Quote>
    ) {
        if (!isLiked.value) {
            // Add to favorites in the local database
            insertQuote(Quote(text = quoteText, isFavorite = true, likeOrWritten = "like"))
            isLiked.value = true // Update the like state
        } else {
            // Remove from favorites
            val quoteToRemove = favoriteQuotes.find { it.text == quoteText }
            if (quoteToRemove != null) {
                deleteQuote(quoteToRemove) // Delete quote from database
                isLiked.value = false // Update the like state
            }
        }
    }
}
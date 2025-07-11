package com.egdcoding.dailydoseofmotivation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState


@Composable
fun CommunityScreen(viewModel: QuoteViewModel) {
    val db = Firebase.firestore
    var quotes by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) }
    var randomQuote by remember { mutableStateOf("Loading...") }
    var currentQuoteId by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    val isLiked = remember { mutableStateOf(false) } // Track liked state for the current quote
    val context = LocalContext.current
    val favoriteQuotes = viewModel.favoriteQuotes.observeAsState(emptyList())

    val snackbarHostState = remember { SnackbarHostState() }

    // Function to refresh quotes
    val refreshQuotes = {
        fetchQuotesFromFirestore(
            db = db,
            context = context,
            onSuccess = { fetchedQuotes ->
                quotes = fetchedQuotes
                if (fetchedQuotes.isNotEmpty()) {
                    val randomPair = fetchedQuotes.random()
                    randomQuote = randomPair.first
                    currentQuoteId = randomPair.second
                    isLiked.value = favoriteQuotes.value.any { it.text == randomQuote }
                } else {
                    randomQuote = "No quotes available. Start writing and sharing yours!!!"
                    currentQuoteId = ""
                }
                isLoading = false
            },
            onError = { e ->
                Log.e("CommunityScreen", "Error fetching quotes", e)
                randomQuote = "Error fetching quotes."
                currentQuoteId = ""
                isLoading = false
            }
        )
    }

    // Function to handle the Like button click
    fun handleLikeButtonClick(
        quoteText: String,
        isLiked: MutableState<Boolean>,
        viewModel: QuoteViewModel,
        favoriteQuotes: List<Quote>
    ) {
        if (!isLiked.value) {
            // Add to favorites in the local database
            viewModel.insertQuote(Quote(text = quoteText, isFavorite = true, likeOrWritten = "like"))

            // Update the like state
            isLiked.value = true
        } else {
            // Remove from favorites
            val quoteToRemove = favoriteQuotes.find { it.text == quoteText }
            if (quoteToRemove != null) {
                viewModel.deleteQuote(quoteToRemove) // Delete quote from database
                isLiked.value = false
            }
        }
    }

    // Initial quote loading
    LaunchedEffect(Unit) {
        refreshQuotes()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Background Image
        BackgroundImage(R.drawable.ic_darkbackground)

        if (isLoading) {
            // Show a loading indicator
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        } else {
            QuoteDisplay(
                randomQuote = randomQuote,
                onNextClick = {
                    if (quotes.isNotEmpty()) {
                        val randomPair = quotes.random()
                        randomQuote = randomPair.first
                        currentQuoteId = randomPair.second
                        isLiked.value = favoriteQuotes.value.any { it.text == randomQuote }
                    } else {
                        randomQuote = "No quotes available."
                        currentQuoteId = ""
                    }
                },
                isLiked = isLiked,
                onReportClick = {
                    if (currentQuoteId.isNotEmpty()) {
                        reportQuote(
                            quote = randomQuote,
                            quoteId = currentQuoteId,
                            db = db,
                            onSuccess = {
                                Toast.makeText(
                                    context,
                                    "Quote reported successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                refreshQuotes()
                                CoroutineScope(Dispatchers.Main).launch {
                                    snackbarHostState.showSnackbar("Quote reported successfully.")
                                }
                            },
                            onError = { e ->
                                Log.e("CommunityScreen", "Error reporting quote", e)
                                Toast.makeText(
                                    context,
                                    "Unable to report this quote. Please try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                CoroutineScope(Dispatchers.Main).launch {
                                    snackbarHostState.showSnackbar("Failed to report the quote.")
                                }
                            }
                        )
                    }
                },
                onLikeClick = {
                    handleLikeButtonClick(
                        quoteText = randomQuote,
                        isLiked = isLiked,
                        viewModel = viewModel,
                        favoriteQuotes = favoriteQuotes.value
                    )
                },
                isLoading = isLoading
            )
        }
    }
}


fun fetchQuotesFromFirestore(
    db: FirebaseFirestore,
    context: Context,  // Pass context as a parameter
    onSuccess: (List<Pair<String, String>>) -> Unit,
    onError: (Exception) -> Unit
) {
    if (NetworkUtils.isInternetAvailable(context)) {
        db.collection("CommunityQuotes")
            .get()
            .addOnSuccessListener { result ->
                val fetchedQuotes = result.mapNotNull {
                    val quoteText = it.getString("quote")
                    val documentId = it.id
                    if (quoteText != null) Pair(quoteText, documentId) else null
                }
                onSuccess(fetchedQuotes)
            }
            .addOnFailureListener { onError(it) }
    } else {
        onError(Exception("No internet connection"))
    }
}


// Function to handle reporting a quote
fun reportQuote(
    quote: String,
    quoteId: String,
    db: FirebaseFirestore,
    onSuccess: () -> Unit,
    onError: (Exception) -> Unit
) {
    val reportedQuote = hashMapOf(
        "quote" to quote,
        "reportedAt" to System.currentTimeMillis()
    )

    db.collection("ReportedQuotes")
        .add(reportedQuote)
        .addOnSuccessListener {
            db.collection("CommunityQuotes")
                .document(quoteId)
                .delete()
                .addOnSuccessListener { onSuccess() }
                .addOnFailureListener { onError(it) }
        }
        .addOnFailureListener { onError(it) }

}

@Composable
fun QuoteDisplay(
    randomQuote: String,
    onReportClick: () -> Unit,
    onNextClick: () -> Unit,
    isLiked: State<Boolean>,
    onLikeClick: () -> Unit,
    isLoading: Boolean
) {
    var showDialog by remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Center the quote vertically and horizontally
        Text(
            text = randomQuote,
            textAlign = TextAlign.Center,
            fontSize = 21.sp,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium.copy(
                lineHeight = 30.sp),
            modifier = Modifier.align(Alignment.Center)
        )


        // Icons row at the bottom
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            // Like Button
            IconButton(
                onClick = { if (!isLoading) onLikeClick() },
                enabled = !isLoading
            ) {
                Icon(
                    imageVector = if (isLiked.value) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Like this quote",
                    tint = if (isLiked.value) Color.Red else Color.White,
                    modifier = Modifier.size(35.dp)
                )
            }

            // Refresh Button
            IconButton(onClick = onNextClick) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh to a new quote",
                    tint = Color.White,
                    modifier = Modifier.size(35.dp)
                )
            }

            // Report Button
            IconButton(onClick = { showDialog = true }) { // Open dialog
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = "Report this quote",
                    tint = Color.White,
                    modifier = Modifier.size(35.dp)
                )
            }
        }
    }

    // Confirmation Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Report Quote") },
            text = { Text("Are you sure you want to report this quote?") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onReportClick() // Proceed with report
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("No")
                }
            }
        )
    }
}


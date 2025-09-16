package com.egdcoding.dailydoseofmotivation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun MyQuotesScreen(viewModel: QuoteViewModel, navController: NavController) {
    val userQuotes by viewModel.userQuotes.observeAsState(emptyList())
    val db = Firebase.firestore
    val context = LocalContext.current
    var expandedQuote by remember { mutableStateOf<String?>(null) } // Track which menu is expanded
    var editingQuoteId by remember { mutableStateOf<Long?>(null) } // Track which quote is being edited
    var updatedText by remember { mutableStateOf("") } // Store edited text

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        BackgroundImage(R.drawable.moon_mountain)

        if (userQuotes.isEmpty()) {
            Text("No quotes yet. Start writing your own!", color = Color.White)
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(8.dp)
            ) {
                items(userQuotes) { quote ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(0.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0x680B0B44))
                        // 0x680B0B44
                        // 0x41000002
                        // 0x2A0B0B44
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (editingQuoteId == quote.id) {
                                    // Show TextField when editing
                                    OutlinedTextField(
                                        value = updatedText,
                                        onValueChange = { updatedText = it },
                                        modifier = Modifier.weight(1f),
                                        label = { Text("Edit your quote") }
                                    )
                                } else {
                                    // Display the quote text
                                    Text(
                                        text = quote.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f),
                                        color = Color.White
                                    )
                                }

                                // Three Dots Menu (Options)
                                Box {
                                    IconButton(onClick = {
                                        expandedQuote = if (expandedQuote == quote.text) null else quote.text
                                    }) {
                                        Icon(Icons.Default.MoreVert, contentDescription = "More Options", tint = Color.White)
                                    }

                                    DropdownMenu(
                                        expanded = expandedQuote == quote.text,
                                        onDismissRequest = { expandedQuote = null }
                                    ) {
                                        // Share Option
                                        DropdownMenuItem(
                                            text = { Text("Share to Community") },
                                            onClick = {
                                                val communityQuote = hashMapOf("quote" to quote.text)
                                                db.collection("CommunityQuotes")
                                                    .add(communityQuote)
                                                    .addOnSuccessListener {
                                                        Toast.makeText(context, "Quote shared!", Toast.LENGTH_SHORT).show()
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                                    }
                                                expandedQuote = null
                                            },
                                            leadingIcon = {
                                                Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.Gray)
                                            }
                                        )

                                        // Edit Option
                                        DropdownMenuItem(
                                            text = { Text("Edit") },
                                            onClick = {
                                                editingQuoteId = quote.id
                                                updatedText = quote.text
                                                expandedQuote = null
                                            },
                                            leadingIcon = {
                                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray)
                                            }
                                        )

                                        // Delete Option
                                        DropdownMenuItem(
                                            text = { Text("Delete", color = Color.Red) },
                                            onClick = {
                                                viewModel.deleteQuote(quote) // Implement this in your ViewModel
                                                expandedQuote = null
                                            },
                                            leadingIcon = {
                                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                            }
                                        )
                                    }
                                }
                            }

                            // Show Save & Cancel buttons only when editing
                            if (editingQuoteId == quote.id) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.updateQuote(quote.id, updatedText)
                                            editingQuoteId = null // Exit edit mode
                                            Toast.makeText(context, "Quote updated!", Toast.LENGTH_SHORT).show()
                                        }
                                    ) {
                                        Text("Save")
                                    }

                                    Button(
                                        onClick = { editingQuoteId = null }
                                    ) {
                                        Text("Cancel")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // FloatingActionButton at the bottom right
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            FloatingActionButton(
                onClick = { navController.navigate("addQuote") },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create a Quote")
            }
        }
    }
}

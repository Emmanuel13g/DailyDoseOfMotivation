package com.egdcoding.dailydoseofmotivation

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun AddQuoteScreen(navController: NavController  ) {
    var quoteText by remember { mutableStateOf("") }
    val viewModel: QuoteViewModel = viewModel()
    val context = LocalContext.current

    BackgroundImage(R.drawable.ic_galaxy)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        TextField(
            value = quoteText,
            onValueChange = { quoteText = it },
            label = { Text("Enter a quote") },
            modifier = Modifier.fillMaxWidth()
            .testTag("QuoteInput")

        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val trimmedQuote = quoteText.trim()
                if (trimmedQuote.isNotEmpty()) {
                    viewModel.insertQuote(Quote(text = trimmedQuote, likeOrWritten = "written"))
                    Toast.makeText(context, "Quote added!", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                } else {
                    Toast.makeText(context, "Please enter a quote.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().testTag("SaveQuoteButton")
        ) {
            Text("Save Quote")
        }
    }
}

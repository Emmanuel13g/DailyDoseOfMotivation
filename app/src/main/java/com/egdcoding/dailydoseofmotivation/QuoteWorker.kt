package com.egdcoding.dailydoseofmotivation

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class QuoteWorker(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {
        fetchAndSendNotification(applicationContext)
        return Result.success()
    }

    private fun fetchAndSendNotification(context: Context) {
        val db = Firebase.firestore
        db.collection("quotes")
            .get()
            .addOnSuccessListener { result ->
                val quotes = result.mapNotNull { it.getString("quote") }
                val randomQuote = if (quotes.isNotEmpty()) quotes.random() else "Stay motivated!"

                sendNotification(context, randomQuote)
            }
            .addOnFailureListener {
                Log.e("QuoteWorker", "Error fetching quotes", it)
            }
    }

    private fun sendNotification(context: Context, quote: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("QUOTE_TEXT", quote)  // ✅ Pass the quote in the intent
        }

        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "QUOTE_CHANNEL")
            .setSmallIcon(R.drawable.ic_app_logo)
            .setContentTitle("Daily Motivation")
            .setContentText(quote)
            .setStyle(NotificationCompat.BigTextStyle().bigText(quote)) // Expand text for long quotes
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)  // ✅ Ensure the intent is attached
            .setAutoCancel(true)
            .build()

        val notificationManager =
            ContextCompat.getSystemService(context, NotificationManager::class.java)
        notificationManager?.notify(1, notification)
    }

}

package com.egdcoding.dailydoseofmotivation

import android.content.Context
import androidx.work.*
import java.util.Calendar
import java.util.concurrent.TimeUnit


fun scheduleDailyQuoteNotification(context: Context) {
    val workManager = WorkManager.getInstance(context)

    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 8) // 24 hour format
        set(Calendar.MINUTE, 30)
        set(Calendar.SECOND, 0)
    }

    val currentTime = Calendar.getInstance().timeInMillis
    var initialDelay = calendar.timeInMillis - currentTime

    // If the time has already passed today, schedule it for tomorrow
    if (initialDelay < 0) {
        initialDelay += TimeUnit.DAYS.toMillis(1)
    }

    // Create OneTimeWorkRequest to run at a specific time
    val dailyWorkRequest = OneTimeWorkRequestBuilder<QuoteWorker>()
        .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
        .setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED) // Ensure internet
                .build()
        )
        .build()

    // Enqueue the work with a unique work name, so it doesn’t repeat unnecessarily
    workManager.enqueueUniqueWork(
        "DailyQuoteWorker",
        ExistingWorkPolicy.REPLACE, // Replace the existing work if there's any
        dailyWorkRequest
    )
}




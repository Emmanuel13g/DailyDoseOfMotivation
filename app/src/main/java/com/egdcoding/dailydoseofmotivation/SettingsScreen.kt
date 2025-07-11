package com.egdcoding.dailydoseofmotivation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.crashlytics.FirebaseCrashlytics

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val crashlyticsEnabled by viewModel.crashlyticsEnabled.observeAsState(false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Enable Crashlytics (This helps us to understand what happens in case of a crash.)",
                fontSize = 18.sp,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = crashlyticsEnabled,
                onCheckedChange = { isChecked ->
                    viewModel.setCrashlyticsEnabled(isChecked)
                    FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = isChecked
                }
            )
        }
    }
}


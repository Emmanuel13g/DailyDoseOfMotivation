package com.egdcoding.dailydoseofmotivation

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val _crashlyticsEnabled = MutableLiveData<Boolean>()

    val crashlyticsEnabled: LiveData<Boolean> = _crashlyticsEnabled

    init {
        _crashlyticsEnabled.value = prefs.getBoolean("crashlytics_enabled", true)
    }

    fun setCrashlyticsEnabled(enabled: Boolean) {
        _crashlyticsEnabled.value = enabled
        prefs.edit().putBoolean("crashlytics_enabled", enabled).apply()
    }
}

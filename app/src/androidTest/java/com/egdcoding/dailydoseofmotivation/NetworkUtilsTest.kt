package com.egdcoding.dailydoseofmotivation

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class NetworkUtilsTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @Test
    fun testInternetAvailability() {
        val isConnected = NetworkUtils.isInternetAvailable(context)
        assertNotNull(isConnected) // It shouldn't be null
    }
}

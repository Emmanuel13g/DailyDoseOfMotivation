package com.egdcoding.dailydoseofmotivation

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.egdcoding.dailydoseofmotivation.ui.theme.MotivationalAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/******************************************************
 * Do this with the screen on or else it won't pass!! *
 ******************************************************/

@RunWith(AndroidJUnit4::class)
class QuoteRepositoryTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testQuote = "This is a test quote"

    @Before
    fun setup() {
        composeTestRule.setContent {
            MotivationalAppTheme {
                AppScreen(quote = testQuote)
            }
        }
    }

    @Test
    fun testAddingAndDisplayingQuote() {
        // Ensure the main UI is displayed
        composeTestRule.onRoot().assertExists()

        composeTestRule.onNodeWithText("My Quotes").assertExists().performClick()
        composeTestRule.waitForIdle()

        // Wait for the FAB button to be ready
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            try {
                composeTestRule.onNodeWithContentDescription("Create a Quote").fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }

        // Click FAB to navigate to AddQuoteScreen
        composeTestRule.onNodeWithContentDescription("Create a Quote").assertExists().performClick()
        composeTestRule.waitForIdle()

        // Check if the TextField exists and enter a quote
        composeTestRule.onNodeWithTag("QuoteInput").assertExists()
            .performTextInput("This is a test quote")

        // Click Save button
        composeTestRule.onNodeWithTag("SaveQuoteButton").assertExists().performClick()
        composeTestRule.waitForIdle()

        // Debugging log
        println("Navigated to My Quotes screen")

        // Ensure the new quote appears
        composeTestRule.waitUntil(timeoutMillis = 8_000) {
            try {
                composeTestRule.onNodeWithText("This is a test quote", useUnmergedTree = true).fetchSemanticsNode()
                true
            } catch (e: AssertionError) {
                false
            }
        }
        composeTestRule.onNodeWithText("This is a test quote", useUnmergedTree = true).assertExists()
    }

}


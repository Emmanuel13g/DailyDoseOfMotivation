package com.egdcoding.dailydoseofmotivation

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.egdcoding.dailydoseofmotivation.ui.theme.MotivationalAppTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import android.Manifest  // ✅ Import this


class MainActivity : ComponentActivity() {

    companion object {
        private const val REQUEST_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        createNotificationChannel(this)  // Create notification channel
        scheduleDailyQuoteNotification(this)
        val quote = intent.getStringExtra("QUOTE_TEXT")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), REQUEST_CODE)
            }
        }

        setContent {
            MotivationalAppTheme {
                AppScreen(quote)
            }
        }
        //addQuoteList()
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppScreen(quote: String?) {
    val viewModel: QuoteViewModel = viewModel()
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Daily Dose of Motivation",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2F3144),
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "main",
            Modifier.padding(paddingValues)
        ) {
            addScreens(navController, viewModel, quote)
        }
    }
}


fun NavGraphBuilder.addScreens(navController: NavHostController, viewModel: QuoteViewModel, quote: String?
) {
    composable("main") {
        MainRoute(
            navController,
            viewModel,
            quote,
            onError = { exception ->
                Log.e("MainRoute", "Error: ${exception.message}")
            }
        )
    }
    composable("favorites") { FavoritesScreen(viewModel) }
    composable("myQuotes") { MyQuotesScreen(viewModel, navController) }
    composable("community") { CommunityScreen(viewModel) }
    composable("addQuote") { AddQuoteScreen(navController) }
    composable("settings") {
        val settingsViewModel: SettingsViewModel = viewModel()
        SettingsScreen(settingsViewModel)
    }
}


@Composable
fun MainRoute(
    navController: NavController,
    viewModel: QuoteViewModel,
    quote: String?,
    onError: (Exception) -> Unit
) {
    val db = Firebase.firestore
    val quotesState = remember { mutableStateOf<List<String>>(emptyList()) }
    val randomQuoteState = rememberSaveable { mutableStateOf(quote ?: "Loading...") } // ✅ Use received quote if available
    val context: Context = LocalContext.current

    val favoriteQuotes by viewModel.favoriteQuotes.observeAsState(emptyList())
    val isLiked = remember { mutableStateOf(false) }

    StatusColor()

    fun loadRandomQuote(quotes: List<String>, favoriteQuotes: List<Quote>) {
        val randomQuote = if (quotes.isNotEmpty()) quotes.random() else "No quotes available."
        randomQuoteState.value = randomQuote
        isLiked.value = favoriteQuotes.any { it.text == randomQuote && it.isFavorite }
    }

    if (NetworkUtils.isInternetAvailable(context)) {
        LaunchedEffect(Unit) {
            db.collection("quotes")
                .get()
                .addOnSuccessListener { result ->
                    val quotes = result.mapNotNull { it.getString("quote") }
                    quotesState.value = quotes
                    if (quote == null) { // ✅ Only load a new quote if one wasn't received
                        if (quotes.isNotEmpty()) {
                            loadRandomQuote(quotes, favoriteQuotes)
                        } else {
                            randomQuoteState.value = "No quotes available."
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error fetching quotes: ", exception)
                    onError(exception)
                }
        }
    } else {
        onError(Exception("No internet connection"))
    }

    // UI remains unchanged
    Box(
        modifier = Modifier.fillMaxSize().background(Color.LightGray)
    ) {
        BackgroundImage(R.drawable.ic_dark_home_background)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = randomQuoteState.value,
                textAlign = TextAlign.Center,
                fontSize = 21.sp,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 30.sp),
                color = Color.White,
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp, vertical = 0.dp)
            ) {
                IconButton(
                    onClick = {
                        viewModel.handleLikeButtonClick(
                            quoteText = randomQuoteState.value,
                            isLiked = isLiked,
                            favoriteQuotes = favoriteQuotes
                        )
                    },
                    modifier = Modifier.weight(1f, false)
                ) {
                    val currentQuote = randomQuoteState.value
                    val liked = favoriteQuotes.any { it.text == currentQuote && it.isFavorite }
                    Icon(
                        imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (liked) Color.Red else Color.White,
                        modifier = Modifier.size(35.dp)
                    )
                }

                IconButton(
                    onClick = { loadRandomQuote(quotesState.value, favoriteQuotes) }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White,
                            modifier = Modifier.size(35.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

            }
        }
    }
}

@Composable
fun StatusColor(){
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            color = Color(0xFF2F3144), // Your chosen color
            darkIcons = false // Change to true if you want light icons
        )
    }
}


@Composable
fun BottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(containerColor = Color(0xFF2D2A38)) {
        NavigationBarItem(
            selected = currentRoute == "main",
            onClick = { if (currentRoute != "main") navController.navigate("main") { launchSingleTop = true } },
            label = { Text("Home") },
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            colors = getNavigationBarColors()
        )
        NavigationBarItem(
            selected = currentRoute == "favorites",
            onClick = { if (currentRoute != "favorites") navController.navigate("favorites") { launchSingleTop = true } },
            label = { Text("Favorites") },
            icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
            colors = getNavigationBarColors()
        )
        NavigationBarItem(
            selected = currentRoute == "myQuotes",
            onClick = { if (currentRoute != "myQuotes") navController.navigate("myQuotes") { launchSingleTop = true } },
            label = { Text("My Quotes") },
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            colors = getNavigationBarColors()
        )
        NavigationBarItem(
            selected = currentRoute == "community",
            onClick = { if (currentRoute != "community") navController.navigate("community") { launchSingleTop = true } },
            label = { Text("Community") },
            icon = { Icon(Icons.Default.Groups, contentDescription = null) },
            colors = getNavigationBarColors()
        )
    }
}

@Composable
fun getNavigationBarColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = Color.LightGray,
    unselectedIconColor = Color.LightGray,
    selectedTextColor = Color.White,
    unselectedTextColor = Color.LightGray
)


@Composable
fun BackgroundImage(img:Int){
    Image(
        painter = painterResource(id = img),
        contentDescription = "Motivational Background",
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )

}

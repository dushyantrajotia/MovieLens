package com.devfusion.movielens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private const val TAG = "ML-DEBUG"

sealed class BottomTab(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    object Home : BottomTab("home", "Home", Icons.Default.Home)
    object Trending : BottomTab("trending", "Trending", Icons.Default.TrendingUp)
    object MyMovies : BottomTab("mymovies", "MyMovies", Icons.Default.List)
    object Chat : BottomTab("chat", "Chat", Icons.Default.Chat)
    object Profile : BottomTab("profile", "Profile", Icons.Default.AccountCircle)

    companion object {
        fun fromRoute(route: String?): BottomTab {
            return when (route) {
                Home.route -> Home
                Trending.route -> Trending
                MyMovies.route -> MyMovies
                Chat.route -> Chat
                Profile.route -> Profile
                else -> {
                    Log.w(TAG, "Unknown route '$route', falling back to Home")
                    Home
                }
            }
        }

        val allTabs: List<BottomTab> = listOf(Home, Trending, MyMovies, Chat, Profile)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieLensApp() {
    val context = LocalContext.current
    val authManager = remember {
        (context.applicationContext as MovieLensApplication).authManager
    }

    var selectedTabIndex by rememberSaveable { mutableStateOf(0) }

    // compute directly (no remember) or use derivedStateOf if heavy
    val currentTab = BottomTab.allTabs.getOrNull(selectedTabIndex) ?: BottomTab.Home

    Scaffold(
        bottomBar = {
            NavigationBar {
                BottomTab.allTabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = {
                            if (selectedTabIndex != index) {
                                selectedTabIndex = index
                                Log.d(TAG, "selectedTabIndex -> $selectedTabIndex (${BottomTab.allTabs[selectedTabIndex].route})")
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()

        // Option A: remove key entirely (Compose will handle recomposition fine here)
        when (currentTab) {
            is BottomTab.Home -> HomeScreen(modifier = modifier)
            is BottomTab.Trending -> TrendingScreen(modifier = modifier)
            is BottomTab.MyMovies -> MyMoviesScreen(modifier = modifier)
            is BottomTab.Chat -> ChatbaseScreen()
            is BottomTab.Profile -> ProfileScreen(modifier = modifier, authManager = authManager)
        }

        // Option B (if you prefer an explicit key): use the numeric index as the key
        // key(selectedTabIndex) {
        //     when (currentTab) { ... }
        // }
    }
}
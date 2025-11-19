package com.devfusion.movielens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private const val TAG = "ML-DEBUG"

sealed class BottomTab(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Home : BottomTab("home", "Home", Icons.Default.Home)
    object Trending : BottomTab("trending", "Trending", Icons.Default.TrendingUp)
    object MyMovies : BottomTab("mymovies", "MyMovies", Icons.Default.List)
    object Profile : BottomTab("profile", "Profile", Icons.Default.AccountCircle)

    companion object {
        fun fromRoute(route: String?): BottomTab {
            return when (route) {
                Home.route -> Home
                Trending.route -> Trending
                MyMovies.route -> MyMovies
                Profile.route -> Profile
                else -> {
                    Log.w(TAG, "Unknown route '$route', falling back to Home")
                    Home
                }
            }
        }

        // Immutable list (do not mutate this anywhere)
        val allTabs: List<BottomTab> = listOf(Home, Trending, MyMovies, Profile)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieLensApp() {
    // Persist a simple String route (Bundle-safe)
    var currentRoute by rememberSaveable { mutableStateOf(BottomTab.Home.route) }

    // Derive the current tab using fromRoute (always returns a non-null tab)
    val currentTab = remember(currentRoute) { BottomTab.fromRoute(currentRoute) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                // Defensive iteration: use index-based loop and getOrNull
                val tabs = BottomTab.allTabs
                for (i in tabs.indices) {
                    val tab = tabs.getOrNull(i) ?: run {
                        Log.w(TAG, "BottomTab.allTabs[$i] was null — falling back to Home")
                        BottomTab.Home
                    }

                    val selected = tab.route == currentRoute

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            if (currentRoute != tab.route) {
                                currentRoute = tab.route
                            }
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding).fillMaxSize()

        // Use when over a non-null currentTab
        when (currentTab) {
            BottomTab.Home -> HomeScreen(modifier = modifier)
            BottomTab.Trending -> TrendingScreen(modifier = modifier)
            BottomTab.MyMovies -> MyMoviesScreen(modifier = modifier)
            BottomTab.Profile -> ProfileScreen(modifier = modifier)
        }
    }
}

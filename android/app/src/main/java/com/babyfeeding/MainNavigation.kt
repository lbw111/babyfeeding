package com.babyfeeding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.babyfeeding.ui.home.HomeScreen

sealed class Screen(
    val route: String,
    val label: String,
    val iconText: String
) {
    data object Home : Screen("home", "Home", "Home")
    data object History : Screen("history", "History", "History")
    data object Stats : Screen("stats", "Stats", "Stats")
    data object Settings : Screen("settings", "Settings", "Settings")
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val screens = listOf(Screen.Home, Screen.History, Screen.Stats, Screen.Settings)
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                screens.forEach { screen ->
                    val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text(screen.iconText) },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.History.route) {
                PlaceholderScreen(
                    title = "History",
                    innerPadding = PaddingValues()
                )
            }
            composable(Screen.Stats.route) {
                PlaceholderScreen(
                    title = "Statistics",
                    innerPadding = PaddingValues()
                )
            }
            composable(Screen.Settings.route) {
                PlaceholderScreen(
                    title = "Settings",
                    innerPadding = PaddingValues()
                )
            }
        }
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    innerPadding: PaddingValues
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "$title screen")
    }
}

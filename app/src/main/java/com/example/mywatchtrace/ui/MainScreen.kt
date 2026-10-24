package com.example.mywatchtrace.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mywatchtrace.ui.screens.CalendarScreen
import com.example.mywatchtrace.ui.screens.DailyLogScreen
import com.example.mywatchtrace.ui.screens.MyWatchesScreen
import com.example.mywatchtrace.ui.screens.TopWatchesScreen
import com.example.mywatchtrace.ui.screens.WatchCollectionScreen

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object MyWatches : BottomNavItem("my_watches", "Saatlerim", Icons.Default.Watch)
    data object WatchCollection : BottomNavItem("watch_collection", "Detaylar", Icons.Default.CollectionsBookmark)
    data object TopWatches : BottomNavItem("top_watches", "İstatistik", Icons.Default.Leaderboard)
    data object DailyLog : BottomNavItem("daily_log", "Günlük Kayıt", Icons.Default.Today)
    data object Calendar : BottomNavItem("calendar", "Takvim", Icons.Default.CalendarMonth)
}

@Composable
fun MainScreen(
    viewModel: WatchViewModel
) {
    val navController = rememberNavController()
    val items = listOf(
        BottomNavItem.MyWatches,
        BottomNavItem.WatchCollection,
        BottomNavItem.TopWatches,
        BottomNavItem.DailyLog,
        BottomNavItem.Calendar
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.title) },
                        label = { Text(item.title) },
                        selected = currentRoute == item.route,
                        onClick = {
                            if (currentRoute != item.route) {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.MyWatches.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Calendar.route) {
                CalendarScreen(viewModel = viewModel)
            }
            composable(BottomNavItem.WatchCollection.route) {
                WatchCollectionScreen(viewModel = viewModel)
            }
            composable(BottomNavItem.TopWatches.route) {
                TopWatchesScreen(viewModel = viewModel)
            }
            composable(BottomNavItem.DailyLog.route) {
                DailyLogScreen(
                    viewModel = viewModel,
                    onNavigateToMyWatches = {
                        navController.navigate(BottomNavItem.MyWatches.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(BottomNavItem.MyWatches.route) {
                MyWatchesScreen(viewModel = viewModel)
            }
        }
    }
}

package com.lupseiv.supplementtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lupseiv.supplementtracker.ui.AddSupplementScreen
import com.lupseiv.supplementtracker.ui.AddSupplementViewModel
import com.lupseiv.supplementtracker.ui.DetailScreen
import com.lupseiv.supplementtracker.ui.DetailViewModel
import com.lupseiv.supplementtracker.ui.HistoryScreen
import com.lupseiv.supplementtracker.ui.HistoryViewModel
import com.lupseiv.supplementtracker.ui.SupplementsScreen
import com.lupseiv.supplementtracker.ui.SupplementsViewModel
import com.lupseiv.supplementtracker.ui.TodayScreen
import com.lupseiv.supplementtracker.ui.TodayViewModel
import com.lupseiv.supplementtracker.ui.theme.SupplementTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SupplementTrackerTheme {
                SupplementTrackerApp()
            }
        }
    }
}

private enum class TopLevelDestination(val route: String, val label: String, val icon: ImageVector) {
    Today("today", "Today", Icons.Default.Checklist),
    Supplements("supplements", "Supplements", Icons.Default.Medication),
    History("history", "History", Icons.Default.History),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SupplementTrackerApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val isTopLevel = TopLevelDestination.entries.any { it.route == currentRoute }

    Scaffold(
        topBar = {
            if (isTopLevel) {
                TopAppBar(title = {
                    Text(TopLevelDestination.entries.first { it.route == currentRoute }.label)
                })
            }
        },
        bottomBar = {
            if (isTopLevel) {
                NavigationBar {
                    TopLevelDestination.entries.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = null) },
                            label = { Text(destination.label) },
                        )
                    }
                }
            }
        },
        floatingActionButton = {
            if (currentRoute == TopLevelDestination.Supplements.route) {
                FloatingActionButton(onClick = { navController.navigate("add") }) {
                    Icon(Icons.Default.Add, contentDescription = "Add supplement")
                }
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = TopLevelDestination.Today.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(TopLevelDestination.Today.route) {
                val vm: TodayViewModel = viewModel(factory = TodayViewModel.Factory)
                TodayScreen(vm, onSupplementClick = { navController.navigate("detail/$it") })
            }
            composable(TopLevelDestination.Supplements.route) {
                val vm: SupplementsViewModel = viewModel(factory = SupplementsViewModel.Factory)
                SupplementsScreen(vm, onSupplementClick = { navController.navigate("detail/$it") })
            }
            composable(TopLevelDestination.History.route) {
                val vm: HistoryViewModel = viewModel(factory = HistoryViewModel.Factory)
                HistoryScreen(vm)
            }
            composable(
                route = "detail/{id}",
                arguments = listOf(navArgument("id") { type = NavType.LongType }),
            ) {
                val vm: DetailViewModel = viewModel(factory = DetailViewModel.Factory)
                DetailScreen(vm, onBack = { navController.popBackStack() })
            }
            composable("add") {
                val vm: AddSupplementViewModel = viewModel(factory = AddSupplementViewModel.Factory)
                AddSupplementScreen(vm, onBack = { navController.popBackStack() })
            }
        }
    }
}

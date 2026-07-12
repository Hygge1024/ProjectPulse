package com.hygge.projectpulse.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.hygge.projectpulse.R
import com.hygge.projectpulse.ui.checkin.CheckInScreen
import com.hygge.projectpulse.ui.exercises.ExercisesScreen
import com.hygge.projectpulse.ui.settings.SettingsScreen
import com.hygge.projectpulse.ui.stats.StatsScreen
import dev.chrisbanes.haze.HazeDefaults
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild

sealed class Screen(val route: String, val titleRes: Int, val icon: ImageVector) {
    data object CheckIn : Screen("check_in", R.string.nav_check_in, Icons.Filled.CheckCircle)
    data object Stats : Screen("stats", R.string.nav_stats, Icons.Filled.Analytics)
    data object Exercises : Screen("exercises", R.string.nav_exercises, Icons.Filled.FitnessCenter)
    data object Settings : Screen("settings", R.string.nav_settings, Icons.Filled.Settings)
}

private val bottomItems = listOf(Screen.CheckIn, Screen.Stats, Screen.Exercises, Screen.Settings)

@Composable
fun PulseFitApp() {
    val navController = rememberNavController()
    val hazeState = remember { HazeState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.CheckIn.route,
            modifier = Modifier
                .fillMaxSize()
                .haze(
                    state = hazeState,
                    backgroundColor = MaterialTheme.colorScheme.background
                )
        ) {
            composable(Screen.CheckIn.route) { CheckInScreen() }
            composable(Screen.Stats.route) { StatsScreen() }
            composable(Screen.Exercises.route) { ExercisesScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }

        val bottomBarTint = HazeDefaults.tint(Color.White.copy(alpha = 0.3f))
        NavigationBar(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .hazeChild(
                    state = hazeState,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    style = HazeDefaults.style(
                        backgroundColor = Color.White.copy(alpha = 0.15f),
                        tint = bottomBarTint,
                        blurRadius = 20.dp,
                        noiseFactor = 0.1f
                    )
                ),
            containerColor = Color.Transparent
        ) {
            bottomItems.forEach { screen ->
                val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                NavigationBarItem(
                    icon = { Icon(screen.icon, contentDescription = stringResource(screen.titleRes)) },
                    label = { Text(stringResource(screen.titleRes)) },
                    selected = selected,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

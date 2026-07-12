package com.hygge.projectpulse.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
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

private val bottomItems = listOf(
    Screen.CheckIn,
    Screen.Stats,
    Screen.Exercises,
    Screen.Settings
)

@Composable
fun PulseFitApp() {
    val navController = rememberNavController()
    val hazeState = remember { HazeState() }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Screen.CheckIn.route,
            modifier = Modifier
                .fillMaxSize()
                .haze(
                    state = hazeState,
                    style = HazeDefaults.style(
                        tint = MaterialTheme.colorScheme.surface.copy(alpha = 0.18f),
                        blurRadius = 30.dp,
                        noiseFactor = 0.1f
                    )
                )
        ) {
            composable(Screen.CheckIn.route) { CheckInScreen() }
            composable(Screen.Stats.route) { StatsScreen() }
            composable(Screen.Exercises.route) { ExercisesScreen() }
            composable(Screen.Settings.route) { SettingsScreen() }
        }

        GlassBottomBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            hazeState = hazeState,
            items = bottomItems,
            currentRoute = currentRoute,
            onItemClick = { screen ->
                navController.navigate(screen.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        )
    }
}

@Composable
private fun GlassBottomBar(
    modifier: Modifier = Modifier,
    hazeState: HazeState,
    items: List<Screen>,
    currentRoute: String?,
    onItemClick: (Screen) -> Unit
) {
    val shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val surface = MaterialTheme.colorScheme.surface
    val borderColor = if (surface.luminance() < 0.5f) {
        Color.White.copy(alpha = 0.5f)
    } else {
        Color.Black.copy(alpha = 0.1f)
    }
    val barStyle = HazeDefaults.style(
        tint = surface.copy(alpha = 0.55f),
        blurRadius = 32.dp,
        noiseFactor = 0.1f
    )
    val pillStyle = HazeDefaults.style(
        tint = surface.copy(alpha = 0.65f),
        blurRadius = 24.dp,
        noiseFactor = 0.1f
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
            .clip(shape)
            .hazeChild(state = hazeState, shape = shape, style = barStyle)
            .border(1.dp, borderColor, shape)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { screen ->
                val selected = currentRoute == screen.route
                val itemColor = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }

                if (selected) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .hazeChild(
                                state = hazeState,
                                shape = RoundedCornerShape(50),
                                style = pillStyle
                            )
                            .clickable { onItemClick(screen) },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = stringResource(screen.titleRes),
                                tint = itemColor,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(screen.titleRes),
                                color = itemColor,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .height(56.dp)
                            .clip(RoundedCornerShape(50))
                            .clickable { onItemClick(screen) }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = screen.icon,
                            contentDescription = stringResource(screen.titleRes),
                            tint = itemColor,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(screen.titleRes),
                            color = itemColor,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }
    }
}

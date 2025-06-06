package com.dieti.dietiestates25.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dieti.dietiestates25.ui.navigation.Screen

// Sealed class for navigation items remains the same
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem(Screen.HomeScreen.route, Icons.Default.Home, "Esplora")
    object Notifications : BottomNavItem(Screen.NotificationScreen.route, Icons.Default.Notifications, "Notifiche")
    object Profile : BottomNavItem(Screen.ProfileScreen.route, Icons.Default.Person, "Profilo")
}

@Composable
fun AppBottomNavigation(
    navController: NavController,
    idUtente: String = "sconosciuto",
    // New lambda to intercept navigation attempts. Defaults to 'true' to avoid breaking other screens.
    onNavigateAttempt: () -> Boolean = { true }
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color(0xFF009688), // Teal color
        contentColor = Color.White
    ) {
        val items = listOf(
            BottomNavItem.Home,
            BottomNavItem.Notifications,
            BottomNavItem.Profile,
        )

        items.forEach { item ->
            val selected = isRouteSelected(currentRoute, item.route)
            AddItem(
                item = item,
                navController = navController,
                selected = selected,
                onNavigateAttempt = onNavigateAttempt // Pass the lambda down
            )
        }
    }
}

fun isRouteSelected(currentRoute: String?, itemRoute: String): Boolean {
    return currentRoute?.startsWith(itemRoute) == true
}

@Composable
fun RowScope.AddItem(
    item: BottomNavItem,
    navController: NavController,
    selected: Boolean,
    onNavigateAttempt: () -> Boolean // Receive the lambda
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .weight(1f)
            .then(if (selected) {
                Modifier
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.2f))
            } else {
                Modifier
            })
    ) {
        this@AddItem.NavigationBarItem(
            icon = {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = if (selected) Color.White else Color.White.copy(alpha = 0.6f)
                )
            },
            label = {
                Text(
                    text = item.label,
                    color = if (selected) Color.White else Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelSmall
                )
            },
            selected = selected,
            onClick = {
                // Check if the destination is not the current one
                if (!isRouteSelected(currentRoute, item.route)) {
                    // Call the interceptor lambda first
                    if (onNavigateAttempt()) {
                        // If it returns true, proceed with navigation
                        val route = if (item.route == Screen.HomeScreen.route) {
                            "${item.route}/utente" // Handle user argument for home
                        } else {
                            item.route
                        }
                        navController.navigate(route) {
                            // Standard navigation logic to avoid building up a large back stack
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                    // If onNavigateAttempt() returns false, do nothing.
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent // Remove the indicator
            )
        )
    }
}

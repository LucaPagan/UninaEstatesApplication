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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.Dimensions

// Sealed class for navigation items remains the same
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem(Screen.HomeScreen.route, Icons.Default.Home, "Esplora")
    object Notifications : BottomNavItem(Screen.NotificationScreen.route, Icons.Default.Notifications, "Notifiche")
    object Profile : BottomNavItem(Screen.ProfileScreen.route, Icons.Default.Person, "Profilo")
}

@Composable
fun AppBottomNavigation(
    navController: NavController,
    idUtente: String,
    // New lambda to intercept navigation attempts. Defaults to 'true' to avoid breaking other screens.
    onNavigateAttempt: () -> Boolean = { true }
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val colorScheme = MaterialTheme.colorScheme
    val dimension = Dimensions

    NavigationBar(
        containerColor = colorScheme.primary,
        contentColor = colorScheme.onPrimary
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
                onNavigateAttempt = onNavigateAttempt, // Pass the lambda down
                colorScheme = colorScheme,
                dimension = dimension,
                idUtente = idUtente
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
    idUtente: String,
    onNavigateAttempt: () -> Boolean, // Receive the lambda
    colorScheme: ColorScheme,
    dimension: Dimensions
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .weight(1f)
            .then(
                if (selected) {
                    Modifier
                        .padding(
                            horizontal = dimension.paddingSmall,
                            vertical = dimension.paddingExtraSmall
                        )
                        .clip(RoundedCornerShape(dimension.cornerRadiusLarge))
                        .background(colorScheme.onPrimary.copy(alpha = 0.2f))
                } else {
                    Modifier
                }
            )
    ) {
        this@AddItem.NavigationBarItem(
            icon = {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.label,
                    tint = if (selected) colorScheme.onPrimary else colorScheme.onPrimary.copy(alpha = 0.6f)
                )
            },
            label = {
                Text(
                    text = item.label,
                    color = if (selected) colorScheme.onPrimary else colorScheme.onPrimary.copy(alpha = 0.6f),
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
                        val finalRoute = when (item) {
                            // Home e Profilo richiedono l'ID Utente
                            BottomNavItem.Home -> "${item.route}/$idUtente"
                            BottomNavItem.Profile -> "${item.route}/$idUtente"
                            BottomNavItem.Notifications -> "${item.route}/$idUtente"
                            // Notifiche (e altre future senza argomenti) usano la rotta base
                            else -> item.route
                        }

                        navController.navigate(finalRoute) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = colorScheme.surfaceDim // Remove the indicator
            )
        )
    }
}

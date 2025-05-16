// File: ui/components/AppBottomNavigation.kt
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

// Definisci i tuoi elementi di navigazione
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem(Screen.HomeScreen.route, Icons.Default.Home, "Esplora")
    object Notifications : BottomNavItem(Screen.NotificationScreen.route, Icons.Default.Notifications, "Notifiche")
    object Profile : BottomNavItem(Screen.ProfileScreen.route, Icons.Default.Person, "Profilo")
}

@Composable
fun AppBottomNavigation(navController: NavController, idUtente: String = "sconosciuto") {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = Color(0xFF009688), // Teal color to match the image
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
                currentRoute = currentRoute,
                navController = navController,
                selected = selected
            )
        }
    }
}

// Funzione per verificare la corrispondenza della route
fun isRouteSelected(currentRoute: String?, itemRoute: String): Boolean {
    // Se la route contiene argomenti (come home_screen/{idUtente}),
    // verifica che inizi con la route base
    return currentRoute?.startsWith(itemRoute) == true
}

@Composable
fun RowScope.AddItem(
    item: BottomNavItem,
    currentRoute: String?,
    navController: NavController,
    selected: Boolean
) {
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
                if (!isRouteSelected(currentRoute, item.route)) {
                    // Se siamo nella home, potremmo dover gestire l'argomento idUtente
                    if (item.route == Screen.HomeScreen.route) {
                        // Passa un valore di default per idUtente
                        navController.navigate("${item.route}/utente")
                    } else {
                        navController.navigate(item.route)
                    }
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent // Remove the indicator
            )
        )
    }
}
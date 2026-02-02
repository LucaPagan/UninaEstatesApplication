package com.dieti.dietiestates25.ui.components

import android.util.Log
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

sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem(Screen.HomeScreen.route, Icons.Default.Home, "Esplora")
    object Notifications : BottomNavItem(Screen.NotificationScreen.route, Icons.Default.Notifications, "Notifiche")
    object Profile : BottomNavItem(Screen.ProfileScreen.route, Icons.Default.Person, "Profilo")
}

@Composable
fun AppBottomNavigation(
    navController: NavController,
    idUtente: String = "sconosciuto",
    onNavigateAttempt: () -> Boolean = { true }
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val colorScheme = MaterialTheme.colorScheme
    val dimension = Dimensions

    // Debug: Stampiamo dove siamo
    // Log.d("NAV_BAR_DEBUG", "Current Route: $currentRoute | ID per navigazione: $idUtente")

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
            // Logica di selezione più robusta per rotte con argomenti
            // Se currentRoute è "home_screen/123" e item.route è "home_screen", startsWith è true.
            val selected = currentRoute?.startsWith(item.route) == true

            AddItem(
                item = item,
                navController = navController,
                selected = selected,
                idUtente = idUtente,
                onNavigateAttempt = onNavigateAttempt,
                colorScheme = colorScheme,
                dimension = dimension,
                currentRoute = currentRoute // Passiamo per controllo
            )
        }
    }
}

@Composable
fun RowScope.AddItem(
    item: BottomNavItem,
    navController: NavController,
    selected: Boolean,
    idUtente: String,
    onNavigateAttempt: () -> Boolean,
    colorScheme: ColorScheme,
    dimension: Dimensions,
    currentRoute: String?
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .then(if (selected) {
                Modifier
                    .padding(horizontal = dimension.paddingSmall, vertical = dimension.paddingExtraSmall)
                    .clip(RoundedCornerShape(dimension.cornerRadiusLarge))
                    .background(colorScheme.onPrimary.copy(alpha = 0.2f))
            } else {
                Modifier
            })
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
                Log.d("NAV_BAR_DEBUG", "-----------------------------")
                Log.d("NAV_BAR_DEBUG", "CLICK su: ${item.label}")

                // Controllo Anti-Loop: Se siamo già qui, non fare nulla
                if (currentRoute?.startsWith(item.route) == true) {
                    Log.d("NAV_BAR_DEBUG", "BLOCCATO: Già in questa schermata ($currentRoute)")
                    return@NavigationBarItem
                }

                if (onNavigateAttempt()) {
                    // Costruzione Rotta: Aggiungiamo ID solo se necessario
                    val finalRoute = when (item) {
                        BottomNavItem.Home -> "${item.route}/$idUtente"
                        BottomNavItem.Profile -> "${item.route}/$idUtente"
                        else -> item.route
                    }

                    Log.d("NAV_BAR_DEBUG", "Tento navigazione verso: '$finalRoute'")

                    try {
                        navController.navigate(finalRoute) {
                            // Configurazioni standard per la BottomBar
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        Log.d("NAV_BAR_DEBUG", "Comando navigate inviato.")
                    } catch (e: Exception) {
                        Log.e("NAV_BAR_DEBUG", "CRASH Navigazione!", e)
                    }
                } else {
                    Log.d("NAV_BAR_DEBUG", "Navigazione bloccata dall'interceptor esterno.")
                }
            },
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = colorScheme.surfaceDim
            )
        )
    }
}
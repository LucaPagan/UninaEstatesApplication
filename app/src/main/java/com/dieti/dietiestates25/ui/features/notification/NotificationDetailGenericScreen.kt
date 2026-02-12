package com.dieti.dietiestates25.ui.features.notification

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar

@Composable
fun NotificationDetailGenericScreen(
    navController: NavController,
    title: String,
    body: String
) {
    Scaffold(
        topBar = {
            // FIX: Uso del parametro nominato 'onBackClick' per evitare ambiguità con 'actions'
            GeneralHeaderBar(
                title = "Dettaglio Notifica",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
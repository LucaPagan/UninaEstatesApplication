package com.dieti.dietiestates25.ui.screen

import com.dieti.dietiestates25.ui.components.AppRequestDisplay
import com.dieti.dietiestates25.ui.model.RequestViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.AppEmptyDisplayView
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

@OptIn(ExperimentalMaterial3Api::class) // Necessario per TopAppBar
@Composable
fun RequestsScreen(
    navController: NavController,
    viewModel: RequestViewModel = viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = com.dieti.dietiestates25.ui.theme.Dimensions

    val requests by viewModel.richieste.collectAsState()

    val gradientColors = listOf(
        colorScheme.primary.copy(alpha = 0.7f),
        colorScheme.background,
        colorScheme.background,
        colorScheme.primary.copy(alpha = 0.6f)
    )

    Scaffold(
        topBar = {
            GeneralHeaderBar(
                title = "Le tue richieste",
                onBackClick = { navController.popBackStack() }
            )
        },
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = gradientColors))
                .padding(paddingValues)
        ) {
            if (requests.isEmpty()) {
                // La view per la lista vuota rimane invariata
                AppEmptyDisplayView(
                    modifier = Modifier.fillMaxSize(),
                    message = "Nessuna richiesta da mostrare.",
                    dimensions = dimensions,
                    colorScheme = colorScheme,
                    typography = typography
                )
            } else {
                // La LazyColumn per le richieste rimane invariata
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = dimensions.paddingMedium,
                        vertical = dimensions.paddingMedium
                    ),
                    verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
                ) {
                    items(requests, key = { it.id }) { richiesta ->
                        AppRequestDisplay(
                            richiesta = richiesta,
                            onClick = {
                                // Logica di navigazione al dettaglio
                            }
                        )
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true, name = "Richieste Screen Light")
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Richieste Screen Dark")
@Composable
fun RequestsScreenPreview() {
    DietiEstatesTheme {
        RequestsScreen(navController = rememberNavController())
    }
}
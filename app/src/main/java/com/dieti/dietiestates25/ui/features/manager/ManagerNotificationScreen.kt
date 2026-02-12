package com.dieti.dietiestates25.ui.features.manager

import android.app.Application
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Composable
fun ManagerNotificationsScreen(
    navController: NavController,
    viewModel: ManagerNotificationsViewModel = viewModel()
) {
    val richieste by viewModel.richieste.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val dimensions = Dimensions

    LaunchedEffect(Unit) {
        viewModel.loadRichieste()
    }

    Scaffold(
        topBar = {
            GeneralHeaderBar(
                title = "Immobili da Assegnare",
                onBackClick = { navController.popBackStack() }
            )
        },
        containerColor = colorScheme.background
    ) { paddingValues ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (richieste.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Nessun immobile da prendere in carico.",
                    color = colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(dimensions.paddingMedium),
                verticalArrangement = Arrangement.spacedBy(dimensions.paddingMedium)
            ) {
                items(richieste) { richiesta ->
                    RequestItemCard(
                        richiesta = richiesta,
                        onClick = {
                            navController.navigate(Screen.ManagerPendingPropertyScreen.withId(richiesta.id))
                        },
                        colorScheme = colorScheme
                    )
                }
            }
        }
    }
}

@Composable
fun RequestItemCard(
    richiesta: RichiestaDTO,
    onClick: () -> Unit,
    colorScheme: ColorScheme
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }, // Apre la preview intera al tocco
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Immagine Preview dell'Immobile
            AsyncImage(
                model = RetrofitClient.getFullUrl(richiesta.immagineUrl ?: ""),
                contentDescription = "Anteprima Immobile",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .background(colorScheme.surfaceVariant)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = richiesta.titolo,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = richiesta.descrizione ?: "Richiesta di assegnazione incarico",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = richiesta.data,
                    style = MaterialTheme.typography.labelSmall,
                    color = colorScheme.primary
                )
            }
        }
    }
}
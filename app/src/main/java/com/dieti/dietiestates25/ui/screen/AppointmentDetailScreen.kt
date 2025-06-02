package com.dieti.dietiestates25.ui.screen

import android.content.res.Configuration
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.model.AppointmentDetailViewModel
import com.dieti.dietiestates25.ui.model.AppointmentDetail // Importa la data class
import com.dieti.dietiestates25.ui.model.AppointmentIconType // Importa l'enum per l'icona

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BusinessCenter // Per MEETING appuntamento
import androidx.compose.material.icons.filled.Build // Per GENERIC appuntamento
import androidx.compose.material.icons.filled.Event // Per VISIT appuntamento
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppRedButton
import com.dieti.dietiestates25.ui.components.CircularIconActionButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailScreen(
    navController: NavController,
    appointmentId: String?, // L'ID dell'appuntamento da visualizzare
    viewModel: AppointmentDetailViewModel = viewModel()
) {
    val appointmentDetail by viewModel.currentAppointment.collectAsState()
    val dimensions = Dimensions
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    LaunchedEffect(appointmentId) {
        viewModel.loadAppointmentDetail(appointmentId)
    }

    Scaffold(
        topBar = {
            AppointmentDetailTopAppBar(
                navController = navController,
                appointmentTitle = appointmentDetail?.title ?: "Dettaglio Appuntamento",
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions,
                onReschedule = viewModel::rescheduleAppointment, // Aggiungi azioni se necessario
                onCancel = viewModel::cancelAppointment
            )
        }
    ) { paddingValues ->
        if (appointmentDetail == null && appointmentId != null) {
            // Mostra un caricamento o uno scheletro
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (appointmentDetail != null) {
            AppointmentDetailContent(
                modifier = Modifier.padding(paddingValues),
                appointmentDetail = appointmentDetail!!, // Sappiamo che non è nullo qui
                formattedDate = viewModel.getFormattedDate(appointmentDetail),
                formattedTime = viewModel.getFormattedTime(appointmentDetail),
                onReschedule = viewModel::rescheduleAppointment,
                onCancel = viewModel::cancelAppointment,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        } else {
            // Messaggio di errore o stato vuoto se l'ID era nullo o il caricamento fallisce
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Impossibile caricare i dettagli dell'appuntamento.")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppointmentDetailTopAppBar(
    navController: NavController,
    appointmentTitle: String,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions,
    onReschedule: () -> Unit,
    onCancel: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.primary)
            .padding(horizontal = dimensions.paddingMedium)
            .padding(top = dimensions.paddingExtraSmall, bottom = dimensions.paddingExtraSmall)
            .statusBarsPadding(),
        title = {
            Text(
                text = appointmentTitle,
                style = typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            CircularIconActionButton(
                onClick = { navController.popBackStack() },
                iconVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Indietro",
                backgroundColor = colorScheme.primaryContainer,
                iconTint = colorScheme.onPrimaryContainer,
                iconModifier = Modifier.size(dimensions.iconSizeMedium)
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.primary,
            titleContentColor = colorScheme.onPrimary,
            navigationIconContentColor = colorScheme.onPrimary,
            actionIconContentColor = colorScheme.onPrimary
        )
    )
}

@Composable
private fun AppointmentDetailContent(
    modifier: Modifier = Modifier,
    appointmentDetail: AppointmentDetail,
    formattedDate: String,
    formattedTime: String,
    onReschedule: () -> Unit,
    onCancel: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(
                    RoundedCornerShape(
                        topStart = dimensions.cornerRadiusLarge,
                        topEnd = dimensions.cornerRadiusLarge
                    )
                )
                .background(colorScheme.background)
                .padding(dimensions.paddingMedium)
                .verticalScroll(rememberScrollState()), // Permette lo scroll se il contenuto è lungo
            verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
        ) {
            // Header con icona e titolo appuntamento
            AppointmentHeaderCard(
                title = appointmentDetail.title,
                iconType = appointmentDetail.iconType,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )

            // Card Dettagli Data e Ora
            InfoCard {
                InfoRow(icon = Icons.Filled.Event, label = "Data", value = formattedDate, dimensions = dimensions, colorScheme = colorScheme, typography = typography)
                InfoRow(icon = Icons.Filled.Schedule, label = "Orario", value = formattedTime, dimensions = dimensions, colorScheme = colorScheme, typography = typography)
            }

            // Card Indirizzo
            InfoCard {
                InfoRow(icon = Icons.Filled.LocationOn, label = "Indirizzo", value = appointmentDetail.address, dimensions = dimensions, colorScheme = colorScheme, typography = typography)
            }

            // Immagine Proprietà (se presente)
            appointmentDetail.propertyImageUrl?.let { imageUrl ->
                Card(
                    shape = RoundedCornerShape(dimensions.cornerRadiusMedium),
                    elevation = CardDefaults.cardElevation(dimensions.elevationSmall)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .error(R.drawable.property1) // Placeholder se l'immagine non carica
                            .build(),
                        contentDescription = "Immagine proprietà",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp) // Altezza fissa per l'immagine
                            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium)),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            // Card Descrizione (se presente)
            appointmentDetail.description?.takeIf { it.isNotBlank() }?.let {
                InfoCard(title = "Descrizione") {
                    Text(text = it, style = typography.bodyMedium, color = colorScheme.onSurfaceVariant)
                }
            }

            // Card Partecipanti (se presente)
            appointmentDetail.participants?.takeIf { it.isNotEmpty() }?.let { participants ->
                InfoCard(title = "Partecipanti") {
                    participants.forEach { participant ->
                        InfoRow(icon = Icons.Filled.Person, value = participant, showLabel = false, dimensions = dimensions, colorScheme = colorScheme, typography = typography)
                    }
                }
            }

            // Card Note (se presente)
            appointmentDetail.notes?.takeIf { it.isNotBlank() }?.let {
                InfoCard(title = "Note Aggiuntive") {
                    Text(text = it, style = typography.bodyMedium, color = colorScheme.onSurfaceVariant)
                }
            }


            // Spacer per spingere i bottoni in fondo se il contenuto è corto
            Spacer(modifier = Modifier.weight(1f))

            // Sezione Bottoni Azione
            AppointmentActionButtons(
                onReschedule = onReschedule,
                onCancel = onCancel,
                dimensions = dimensions
            )
        }
    }
}

@Composable
private fun AppointmentHeaderCard(
    title: String,
    iconType: AppointmentIconType,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    val icon = when (iconType) {
        AppointmentIconType.VISIT -> Icons.Filled.Event
        AppointmentIconType.MEETING -> Icons.Filled.BusinessCenter
        AppointmentIconType.GENERIC -> Icons.Filled.Build
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
            .background(colorScheme.surfaceVariant)
            .padding(dimensions.paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(dimensions.iconSizeExtraLarge) // Es. 48.dp
                .clip(RoundedCornerShape(dimensions.cornerRadiusSmall)) // Es. 8.dp
                .background(colorScheme.secondaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Icona Appuntamento",
                modifier = Modifier.size(dimensions.iconSizeLarge), // Es. 32dp o 36dp
                tint = colorScheme.onSecondaryContainer
            )
        }
        Spacer(modifier = Modifier.width(dimensions.spacingMedium))
        Text(
            text = title,
            style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun InfoCard(
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimensions.cornerRadiusMedium),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.elevationNone)
    ) {
        Column(modifier = Modifier.padding(Dimensions.paddingMedium)) {
            title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = Dimensions.spacingSmall)
                )
            }
            content()
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String? = null, // Etichetta opzionale
    value: String,
    showLabel: Boolean = true, // Controlla se mostrare l'etichetta
    dimensions: Dimensions,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = dimensions.spacingExtraSmall)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label ?: value,
            tint = colorScheme.primary,
            modifier = Modifier.size(dimensions.iconSizeSmall) // 20.dp
        )
        Spacer(modifier = Modifier.width(dimensions.spacingMedium))
        Column {
            if (showLabel && label != null) {
                Text(text = label, style = typography.labelMedium, color = colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }
            Text(text = value, style = typography.bodyMedium, color = colorScheme.onSurface)
        }
    }
}


@Composable
private fun AppointmentActionButtons(
    onReschedule: () -> Unit,
    onCancel: () -> Unit,
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall) // Spazio tra i bottoni
    ) {
        AppPrimaryButton(
            onClick = onReschedule,
            modifier = Modifier.fillMaxWidth(),
            text = "Riprogramma Appuntamento",
        )
        AppRedButton( // Assumendo che AppRedButton sia definito nei tuoi componenti
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
            text = "Annulla Appuntamento"
        )
    }
}

@Preview(showBackground = true, name = "Appointment Detail Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Appointment Detail Dark")
@Composable
fun AppointmentDetailScreenPreview() {
    val navController = rememberNavController()
    val previewViewModel = viewModel<AppointmentDetailViewModel>()
    // Carica un appuntamento di esempio per la preview
    LaunchedEffect(Unit) {
        previewViewModel.loadAppointmentDetail("apt1")
    }
    AppointmentDetailScreen(
        navController = navController,
        appointmentId = "apt1", // ID di esempio per la preview
        viewModel = previewViewModel
    )

}
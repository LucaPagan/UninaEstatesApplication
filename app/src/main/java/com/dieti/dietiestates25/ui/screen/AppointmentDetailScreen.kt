package com.dieti.dietiestates25.ui.screen

import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.model.AppointmentDetailViewModel
import com.dieti.dietiestates25.ui.model.AppointmentDetail
import com.dieti.dietiestates25.ui.model.AppointmentIconType

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppRedButton
import com.dieti.dietiestates25.ui.components.CircularIconActionButton
import com.dieti.dietiestates25.ui.components.CalendarView
import com.dieti.dietiestates25.ui.components.TimeSlotSelector
import java.time.LocalDate
import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailScreen(
    navController: NavController,
    appointmentId: String?,
    viewModel: AppointmentDetailViewModel = viewModel()
) {
    val appointmentDetail by viewModel.currentAppointment.collectAsState()
    val appointmentCancelledEvent by viewModel.appointmentCancelledEvent.collectAsState() // Osserva l'evento

    val dimensions = Dimensions
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    var showRescheduleDialog by remember { mutableStateOf(false) }

    LaunchedEffect(appointmentId) {
        viewModel.loadAppointmentDetail(appointmentId)
    }

    // Effetto per gestire la navigazione dopo la cancellazione
    LaunchedEffect(appointmentCancelledEvent) {
        if (appointmentCancelledEvent) {
            Log.d("AppointmentDetailScreen", "Appointment cancelled event received, navigating back.")
            navController.popBackStack()
            viewModel.onCancellationEventConsumed() // Resetta l'evento nel ViewModel
        }
    }

    Scaffold(
        topBar = {
            AppointmentDetailTopAppBar(
                navController = navController,
                appointmentTitle = appointmentDetail?.title ?: "Dettaglio Appuntamento",
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions,
                onReschedule = {
                    Log.d("AppointmentDetailScreen", "Reschedule button clicked. Opening dialog.")
                    showRescheduleDialog = true
                },
                onCancel = viewModel::cancelAppointment // Questa chiamata ora triggererà l'evento
            )
        }
    ) { paddingValues ->
        // La logica per mostrare caricamento/contenuto/errore rimane la stessa
        if (appointmentDetail == null && appointmentId != null && !appointmentCancelledEvent) { // Non mostrare loader se stiamo per navigare indietro
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (appointmentDetail != null) {
            AppointmentDetailContent(
                modifier = Modifier.padding(paddingValues),
                appointmentDetail = appointmentDetail!!,
                formattedDate = viewModel.getFormattedDate(appointmentDetail),
                formattedTime = viewModel.getFormattedTime(appointmentDetail),
                onReschedule = {
                    Log.d("AppointmentDetailScreen", "Reschedule from content clicked. Opening dialog.")
                    showRescheduleDialog = true
                },
                onCancel = viewModel::cancelAppointment, // Questa chiamata ora triggererà l'evento
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        } else {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Impossibile caricare i dettagli dell'appuntamento.")
            }
        }
    }

    if (showRescheduleDialog && appointmentDetail != null) {
        val currentApt = appointmentDetail!!
        RescheduleAppointmentDialog(
            currentAppointmentDate = currentApt.date,
            currentAppointmentTimeSlotIndex = viewModel.getTimeSlotIndex(currentApt.timeSlot),
            onDismiss = { showRescheduleDialog = false },
            onConfirm = { newDate, newTimeSlotIndex ->
                viewModel.confirmReschedule(newDate, newTimeSlotIndex)
                showRescheduleDialog = false
            },
            colorScheme = colorScheme,
            typography = typography,
            dimensions = dimensions
        )
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
    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(colorScheme.primaryContainer)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.primary)
                .padding(
                    horizontal = dimensions.paddingMedium,
                    vertical = dimensions.paddingMedium
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                CircularIconActionButton(
                    onClick = { navController.popBackStack() },
                    iconVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro",
                    backgroundColor = colorScheme.primaryContainer,
                    iconTint = colorScheme.onPrimaryContainer,
                    buttonSize = dimensions.iconSizeLarge,
                    iconSize = dimensions.iconSizeMedium,
                )
                Spacer(modifier = Modifier.width(dimensions.spacingSmall))
                Text(
                    text = appointmentTitle,
                    style = typography.titleLarge,
                    color = colorScheme.onPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
            }
            Box {
                CircularIconActionButton(
                    onClick = { showMenu = !showMenu },
                    iconVector = Icons.Filled.MoreVert,
                    contentDescription = "Altre opzioni",
                    backgroundColor = colorScheme.primaryContainer,
                    iconTint = colorScheme.onPrimaryContainer,
                    buttonSize = dimensions.iconSizeLarge,
                    iconSize = dimensions.iconSizeMedium,
                )
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Riprogramma") },
                        onClick = {
                            onReschedule()
                            showMenu = false
                        },
                        leadingIcon = { Icon(Icons.Filled.EditCalendar, contentDescription = "Riprogramma") }
                    )
                    DropdownMenuItem(
                        text = { Text("Annulla Appuntamento", color = colorScheme.error) },
                        onClick = {
                            onCancel() // Chiama la lambda per cancellare
                            showMenu = false
                        },
                        leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = "Annulla", tint = colorScheme.error) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AppointmentDetailContent(
    modifier: Modifier = Modifier,
    appointmentDetail: AppointmentDetail,
    formattedDate: String,
    formattedTime: String,
    onReschedule: () -> Unit,
    onCancel: () -> Unit, // Riceve la lambda
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
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
        ) {
            AppointmentHeaderCard(
                title = appointmentDetail.title,
                iconType = appointmentDetail.iconType,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
            InfoCard {
                InfoRow(icon = Icons.Filled.Event, label = "Data", value = formattedDate, dimensions = dimensions, colorScheme = colorScheme, typography = typography)
                InfoRow(icon = Icons.Filled.Schedule, label = "Orario", value = formattedTime, dimensions = dimensions, colorScheme = colorScheme, typography = typography)
            }
            InfoCard {
                InfoRow(icon = Icons.Filled.LocationOn, label = "Indirizzo", value = appointmentDetail.address, dimensions = dimensions, colorScheme = colorScheme, typography = typography)
            }
            appointmentDetail.propertyImageUrl?.let { imageUrl ->
                Card(
                    shape = RoundedCornerShape(dimensions.cornerRadiusMedium),
                    elevation = CardDefaults.cardElevation(dimensions.elevationSmall)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(imageUrl)
                            .crossfade(true)
                            .error(R.drawable.property1)
                            .build(),
                        contentDescription = "Immagine proprietà",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensions.circularIconSize)
                            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
            appointmentDetail.description?.takeIf { it.isNotBlank() }?.let {
                InfoCard(title = "Descrizione") {
                    Text(text = it, style = typography.bodyMedium, color = colorScheme.onSurfaceVariant)
                }
            }
            appointmentDetail.participants?.takeIf { it.isNotEmpty() }?.let { participants ->
                InfoCard(title = "Partecipanti") {
                    participants.forEach { participant ->
                        InfoRow(icon = Icons.Filled.Person, value = participant, showLabel = false, dimensions = dimensions, colorScheme = colorScheme, typography = typography)
                    }
                }
            }
            appointmentDetail.notes?.takeIf { it.isNotBlank() }?.let {
                InfoCard(title = "Note Aggiuntive") {
                    Text(text = it, style = typography.bodyMedium, color = colorScheme.onSurfaceVariant)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            AppointmentActionButtons(
                onReschedule = onReschedule,
                onCancel = onCancel, // Passa la lambda
                dimensions = dimensions
            )
        }
    }
}

@Composable
private fun RescheduleAppointmentDialog(
    currentAppointmentDate: LocalDate,
    currentAppointmentTimeSlotIndex: Int?,
    onDismiss: () -> Unit,
    onConfirm: (newDate: LocalDate, newTimeSlotIndex: Int) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    var selectedDateInDialog by remember { mutableStateOf(currentAppointmentDate) }
    var selectedTimeSlotInDialog by remember { mutableStateOf<Int?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(dimensions.cornerRadiusLarge),
            colors = CardDefaults.cardColors(containerColor = colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(dimensions.paddingLarge)
                    .width(IntrinsicSize.Max),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(dimensions.spacingLarge)
            ) {
                Text(
                    text = "Riprogramma Appuntamento",
                    style = typography.titleLarge,
                    color = colorScheme.onSurface
                )
                CalendarView(
                    initialSelectedDate = selectedDateInDialog,
                    onDateSelected = { newDate -> selectedDateInDialog = newDate },
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions,
                    highlightedDate = LocalDate.now(),
                    disabledDates = setOf(currentAppointmentDate)
                )
                TimeSlotSelector(
                    selectedTimeSlotIndex = selectedTimeSlotInDialog,
                    onTimeSlotSelected = { index -> selectedTimeSlotInDialog = index },
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions,
                    disabledTimeSlotIndices = if (selectedDateInDialog == currentAppointmentDate) {
                        currentAppointmentTimeSlotIndex?.let { setOf(it) } ?: emptySet()
                    } else {
                        emptySet()
                    }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Annulla", color = colorScheme.primary)
                    }
                    Spacer(modifier = Modifier.width(dimensions.spacingSmall))
                    Button(
                        onClick = {
                            selectedTimeSlotInDialog?.let { timeSlotIndex ->
                                onConfirm(selectedDateInDialog, timeSlotIndex)
                            }
                        },
                        enabled = selectedTimeSlotInDialog != null &&
                                (selectedDateInDialog != currentAppointmentDate || selectedTimeSlotInDialog != currentAppointmentTimeSlotIndex)
                    ) {
                        Text("Conferma")
                    }
                }
            }
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
                .size(dimensions.iconSizeExtraLarge)
                .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                .background(colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Icona Appuntamento",
                modifier = Modifier.size(dimensions.iconSizeMedium),
                tint = colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.width(dimensions.spacingMedium))
        Text(
            text = title,
            style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = colorScheme.onPrimary,
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
    label: String? = null,
    value: String,
    showLabel: Boolean = true,
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
            modifier = Modifier.size(dimensions.iconSizeSmall)
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
    onCancel: () -> Unit, // Riceve la lambda per cancellare
    dimensions: Dimensions
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
    ) {
        AppPrimaryButton(
            onClick = onReschedule,
            modifier = Modifier.fillMaxWidth(),
            text = "Riprogramma Appuntamento",
        )
        AppRedButton(
            onClick = onCancel, // Chiama la lambda per cancellare
            modifier = Modifier.fillMaxWidth(),
            text = "Annulla Appuntamento"
        )
    }
}

@Preview(showBackground = true, name = "Appointment Detail Light")
@Preview(showBackground = true, name = "Appointment Detail Dark")
@Composable
fun AppointmentDetailScreenPreview() {
    val navController = rememberNavController()
    val previewViewModel = viewModel<AppointmentDetailViewModel>()
    LaunchedEffect(Unit) {
        previewViewModel.loadAppointmentDetail("apt1")
    }
    AppointmentDetailScreen(
        navController = navController,
        appointmentId = "apt1",
        viewModel = previewViewModel
    )
}

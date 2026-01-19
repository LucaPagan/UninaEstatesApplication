package com.dieti.dietiestates25.ui.features.appointments

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.CalendarView
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.TimeSlotSelector
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
import com.dieti.dietiestates25.ui.theme.Dimensions
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentBookingScreen(
    navController: NavController,
    idUtente: String,
    idImmobile: String
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    // --- GESTIONE STATO LOCALE (Sostituisce il ViewModel) ---
    val scope = rememberCoroutineScope() // Per lanciare operazioni asincrone (delay)

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTimeSlotIndex by remember { mutableStateOf<Int?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var bookingSuccess by remember { mutableStateOf(false) } // Flag per navigare via al successo

    val scrollState = rememberScrollState()

    // --- LOGICA DI PRENOTAZIONE ---
    fun performBooking() {
        scope.launch {
            isLoading = true

            // Simula una chiamata di rete (es. 2 secondi)
            delay(2000)

            // Qui inseriresti la logica reale (es. chiamata a Retrofit/Firebase)
            // Log per debug: "Prenotazione per utente $idUtente immobile $idImmobile il $selectedDate"

            Toast.makeText(context, "Prenotazione inviata con successo!", Toast.LENGTH_SHORT).show()

            isLoading = false
            bookingSuccess = true
        }
    }

    // --- GESTIONE EFFETTI ---
    // Se la prenotazione ha successo, torna indietro
    LaunchedEffect(bookingSuccess) {
        if (bookingSuccess) {
            navController.popBackStack()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            GeneralHeaderBar(
                title = "Prenota una visita",
                onBackClick = { navController.popBackStack() }
            )
        },
        bottomBar = {
            AppointmentBookingBottomBar(
                haptic = haptic,
                colorScheme = colorScheme,
                onProceedClick = {
                    performBooking() // Chiama la funzione locale
                },
                dimensions = dimensions,
                isProceedEnabled = selectedTimeSlotIndex != null && !isLoading
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            AppointmentBookingContent(
                paddingValues = paddingValues,
                scrollState = scrollState,
                selectedDate = selectedDate,
                onDateSelected = { newDate -> selectedDate = newDate },
                selectedTimeSlotIndex = selectedTimeSlotIndex,
                onTimeSlotSelected = { newTimeSlotIndex -> selectedTimeSlotIndex = newTimeSlotIndex },
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )

            // Overlay di caricamento
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(colorScheme.surface.copy(alpha = 0.5f))
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun AppointmentBookingBottomBar(
    haptic: HapticFeedback,
    colorScheme: ColorScheme,
    onProceedClick: () -> Unit,
    dimensions: Dimensions,
    isProceedEnabled: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.background)
                .padding(dimensions.paddingMedium),
        ) {
            AppPrimaryButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onProceedClick()
                },
                text = "Conferma Prenotazione",
                modifier = Modifier.fillMaxWidth(),
                enabled = isProceedEnabled
            )
        }
    }
}

@Composable
private fun AppointmentBookingContent(
    paddingValues: PaddingValues,
    scrollState: ScrollState,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    selectedTimeSlotIndex: Int?,
    onTimeSlotSelected: (Int) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.surface)
            .padding(paddingValues)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = dimensions.paddingMedium)
                .imePadding()
        ) {
            Spacer(modifier = Modifier.height(dimensions.spacingMedium))
            ScreenSectionTitle(
                title = "Seleziona il tuo giorno disponibile",
                colorScheme = colorScheme,
                typography = typography
            )
            Spacer(modifier = Modifier.height(dimensions.spacingSmall))
            CalendarView(
                initialSelectedDate = selectedDate,
                onDateSelected = onDateSelected,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
            Spacer(modifier = Modifier.height(dimensions.spacingLarge))
            ScreenSectionTitle(
                title = "Scegli la fascia oraria",
                colorScheme = colorScheme,
                typography = typography
            )
            Spacer(modifier = Modifier.height(dimensions.spacingMedium))
            TimeSlotSelector(
                selectedTimeSlotIndex = selectedTimeSlotIndex,
                onTimeSlotSelected = onTimeSlotSelected,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
            Spacer(modifier = Modifier.height(dimensions.spacingLarge))
            NotificationBox(
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
            Spacer(modifier = Modifier.height(dimensions.spacingLarge))
        }
    }
}

@Composable
private fun ScreenSectionTitle(
    title: String,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Text(
        text = title,
        style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = colorScheme.onSurface
    )
}

@Composable
fun NotificationBox(
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.cornerRadiusMedium),
        color = colorScheme.secondaryContainer.copy(alpha = 0.4f),
        border = BorderStroke(dimensions.borderStrokeSmall, colorScheme.outline.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier.padding(dimensions.paddingMedium)
        ) {
            Text(
                text = "Nota sulla prenotazione:",
                style = typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(dimensions.spacingSmall))
            Text(
                text = "La tua richiesta sarà inviata all'agente o al proprietario. Riceverai una notifica di conferma.",
                style = typography.bodySmall,
                color = colorScheme.onSecondaryContainer.copy(alpha = 0.9f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppointmentBookingScreenPreview() {
    val navController = rememberNavController()
    AppointmentBookingScreen(navController = navController, idUtente = "1", idImmobile = "1")
}
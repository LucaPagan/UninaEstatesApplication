package com.dieti.dietiestates25.ui.screen

import com.dieti.dietiestates25.ui.components.CalendarView
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.CircularIconActionButton
import com.dieti.dietiestates25.ui.components.TimeSlotSelector
import com.dieti.dietiestates25.ui.theme.Dimensions

import java.time.LocalDate

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar


@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentBookingScreen(
    navController: NavController
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var selectedTimeSlotIndex by remember { mutableStateOf<Int?>(0) } // Modificato per usare l'indice
    val scrollState = rememberScrollState()
    val haptic = LocalHapticFeedback.current

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
                    // Ora selectedTimeSlotIndex è l'indice, puoi mappare a una stringa se necessario
                    val timeSlots = listOf("9-12", "12-14", "14-17", "17-20") // Coerente con TimeSlotSelector
                    val timeSlotString = selectedTimeSlotIndex?.let { timeSlots.getOrNull(it) } ?: "N/A"
                    println("Data selezionata: $selectedDate, Fascia oraria (indice $selectedTimeSlotIndex): $timeSlotString")
                },
                dimensions = dimensions,
                isProceedEnabled = selectedTimeSlotIndex != null // Abilita Prosegui solo se uno slot è selezionato
            )
        }
    ) { paddingValues ->
        AppointmentBookingContent(
            paddingValues = paddingValues,
            scrollState = scrollState,
            selectedDate = selectedDate,
            onDateSelected = { newDate -> selectedDate = newDate },
            selectedTimeSlotIndex = selectedTimeSlotIndex,
            onTimeSlotSelected = { newTimeSlotIndex -> selectedTimeSlotIndex = newTimeSlotIndex }, // Riceve l'indice
            colorScheme = colorScheme,
            typography = typography,
            dimensions = dimensions
        )
    }
}

@Composable
private fun AppointmentBookingBottomBar(
    haptic: HapticFeedback,
    colorScheme: ColorScheme,
    onProceedClick: () -> Unit,
    dimensions: Dimensions,
    isProceedEnabled: Boolean // Nuovo parametro per abilitare/disabilitare il bottone
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // Padding per la navigation bar di sistema
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.background) // Sfondo standard per bottom bar
                .padding(dimensions.paddingMedium),
        ) {
            AppPrimaryButton(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onProceedClick()
                },
                text = "Prosegui",
                modifier = Modifier.fillMaxWidth(),
                enabled = isProceedEnabled // Abilita/disabilita il bottone
            )
        }
    }
}

@Composable
private fun AppointmentBookingContent(
    paddingValues: PaddingValues,
    scrollState: androidx.compose.foundation.ScrollState,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    selectedTimeSlotIndex: Int?, // Modificato per usare l'indice
    onTimeSlotSelected: (Int) -> Unit, // Modificato per ricevere l'indice
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
                .imePadding() // Padding per la tastiera
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
                dimensions = dimensions // Passa dimensions a CalendarView
            )
            Spacer(modifier = Modifier.height(dimensions.spacingLarge))
            ScreenSectionTitle(
                title = "Scegli la fascia oraria",
                colorScheme = colorScheme,
                typography = typography
            )
            Spacer(modifier = Modifier.height(dimensions.spacingMedium)) // Aumentato leggermente lo spazio
            TimeSlotSelector( // Usa il nuovo TimeSlotSelector
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
            Spacer(modifier = Modifier.height(dimensions.spacingLarge)) // Spazio extra in fondo
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
        style = typography.titleMedium.copy(fontWeight = FontWeight.SemiBold), // Leggero bold
        color = colorScheme.onSurface
    )
}

// TimeSlotSelector ora è definito in components/CalendarComponents.kt
// Rimuovi la vecchia definizione da questo file se era qui.

@Composable
fun NotificationBox(
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.cornerRadiusMedium),
        color = colorScheme.secondaryContainer.copy(alpha = 0.4f), // Leggermente più trasparente
        border = BorderStroke(dimensions.borderStrokeSmall, colorScheme.outline.copy(alpha = 0.6f)) // Bordo più leggero
    ) {
        Column(
            modifier = Modifier.padding(dimensions.paddingMedium)
        ) {
            Text(
                text = "Questa non è una prenotazione effettiva:",
                style = typography.bodyMedium.copy(fontWeight = FontWeight.Bold), // Bold per più enfasi
                color = colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(dimensions.spacingSmall)) // Aumentato leggermente
            Text(
                text = "La tua richiesta sarà inviata all'inserzionista che si occuperà di ricontattarti.",
                style = typography.bodySmall,
                color = colorScheme.onSecondaryContainer.copy(alpha = 0.9f) // Leggermente meno trasparente
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppointmentBookingScreenPreview() {
    val navController = rememberNavController()
    AppointmentBookingScreen(navController = navController)
}

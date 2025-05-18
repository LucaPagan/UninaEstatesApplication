package com.dieti.dietiestates25.ui.screen

import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import java.time.LocalDate

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke // Mantenuto per NotificationBox
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable // Mantenuto per TimeSlotSelector
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape // Mantenuto per TimeSlotSelector e NotificationBox
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip // Mantenuto per TimeSlotSelector
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight // Mantenuto per NotificationBox
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.CalendarView
import com.dieti.dietiestates25.ui.components.AppPrimaryButton


@SuppressLint("UnusedBoxWithConstraintsScope") // Riconsidera se `BoxWithConstraints` era davvero necessario
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentBookingScreen(
    navController: NavController
) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography

        // Lo stato iniziale di selectedDate ora può essere gestito meglio da CalendarView,
        // ma manteniamolo qui se serve per altre logiche o per il BottomBar.
        // CalendarView gestirà la sua selezione interna e la notificherà.
        var selectedDate by remember { mutableStateOf(LocalDate.now()) } // Inizializza con oggi
        var selectedTimeSlot by remember { mutableStateOf<Int?>(0) }
        val scrollState = rememberScrollState()
        val haptic = LocalHapticFeedback.current

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                AppointmentBookingTopAppBar(
                    navController = navController,
                    haptic = haptic,
                    colorScheme = colorScheme,
                    typography = typography
                )
            },
            bottomBar = {
                AppointmentBookingBottomBar(
                    haptic = haptic,
                    colorScheme = colorScheme,
                    onProceedClick = {
                        // Logica da eseguire quando si clicca "Prosegui"
                        // Usa selectedDate e selectedTimeSlot
                        println("Data selezionata: $selectedDate, Fascia oraria: ${selectedTimeSlot?.let { it + 1 }}")
                        // navController.navigate(...)
                    }
                )
            }
        ) { paddingValues ->
            AppointmentBookingContent(
                paddingValues = paddingValues,
                scrollState = scrollState,
                selectedDate = selectedDate,
                onDateSelected = { newDate -> selectedDate = newDate },
                selectedTimeSlot = selectedTimeSlot,
                onTimeSlotSelected = { newTimeSlot -> selectedTimeSlot = newTimeSlot },
                colorScheme = colorScheme,
                typography = typography
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppointmentBookingTopAppBar(
    navController: NavController,
    haptic: HapticFeedback,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        TopAppBar(
            title = {
                Text(
                    text = "Prenota una visita",
                    style = typography.titleMedium
                )
            },
            navigationIcon = {
                IconButton(onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    navController.popBackStack()
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Chiudi",
                        tint = colorScheme.onPrimary // Assicura visibilità
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorScheme.primary,
                titleContentColor = colorScheme.onPrimary
            ),
            modifier = Modifier.statusBarsPadding() // Usa l'padding della status bar
        )
        HorizontalDivider(color = colorScheme.onSurfaceVariant, thickness = 1.dp)
    }
}


@Composable
private fun AppointmentBookingBottomBar(
    haptic: HapticFeedback,
    colorScheme: ColorScheme,
    onProceedClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding() // Usa l'padding della navigation bar
    ) {
        HorizontalDivider(color = colorScheme.onSurfaceVariant, thickness = 1.dp) // Colore più standard per i divisori
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.surface) // Usa surface per coerenza con il contenuto
                .padding(16.dp),
        ) {
            AppPrimaryButton( // Utilizzo del componente bottone primario
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onProceedClick()
                },
                text = "Prosegui",
                modifier = Modifier.fillMaxWidth()
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
    selectedTimeSlot: Int?,
    onTimeSlotSelected: (Int) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Box( // Box esterno per il background e il padding generale dal Scaffold
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.surface) // Background principale
            .padding(paddingValues) // Applica i padding dello Scaffold
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp) // Padding orizzontale per il contenuto
                .imePadding()
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            ScreenSectionTitle(
                title = "Seleziona il tuo giorno disponibile",
                colorScheme = colorScheme,
                typography = typography
            )

            Spacer(modifier = Modifier.height(8.dp)) // Spazio ridotto prima del calendario

            // Utilizzo del componente CalendarView estratto
            CalendarView(
                initialSelectedDate = selectedDate, // Passa la data selezionata
                onDateSelected = onDateSelected,    // Callback per aggiornare la data
                colorScheme = colorScheme,
                typography = typography
                // Non serve un modifier specifico qui se deve prendere tutta la larghezza
            )

            Spacer(modifier = Modifier.height(24.dp))

            ScreenSectionTitle(
                title = "Scegli la fascia oraria",
                colorScheme = colorScheme,
                typography = typography
            )

            Spacer(modifier = Modifier.height(12.dp))

            TimeSlotSelector(
                selectedTimeSlot = selectedTimeSlot,
                onTimeSlotSelected = onTimeSlotSelected,
                colorScheme = colorScheme,
                typography = typography
            )

            Spacer(modifier = Modifier.height(24.dp))

            NotificationBox(
                colorScheme = colorScheme,
                typography = typography
            )

            Spacer(modifier = Modifier.height(24.dp)) // Spazio alla fine per scroll
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
        style = typography.titleMedium,
        color = colorScheme.onSurface // Testo sul colore di superficie
    )
}


@Composable
fun TimeSlotSelector( // Lasciato pubblico se usato altrove, altrimenti private
    selectedTimeSlot: Int?,
    onTimeSlotSelected: (Int) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography
) {
    val timeSlots = remember { listOf("9-12", "12-14", "14-17", "17-20") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(0.dp) // Nessuno spazio, gestito dai bordi/divisori
    ) {
        timeSlots.forEachIndexed { index, slot ->
            val isSelected = index == selectedTimeSlot
            // Definisci le forme per gli angoli arrotondati
            val shape = when (index) {
                0 -> RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp, topEnd = 0.dp, bottomEnd = 0.dp)
                timeSlots.size - 1 -> RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 24.dp, bottomEnd = 24.dp)
                else -> RoundedCornerShape(0.dp)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(shape) // Applica la forma prima del background e del click
                    .background(
                        if (isSelected) colorScheme.primary else colorScheme.secondary
                    )
                    .clickable { onTimeSlotSelected(index) },
                contentAlignment = Alignment.Center // Allinea il testo al centro
            ) {
                Text(
                    text = slot,
                    color = if (isSelected) colorScheme.onPrimary else colorScheme.onSecondary,
                    style = typography.labelLarge
                )
            }

            // Aggiungi il divisore solo tra i pulsanti
            if (index < timeSlots.size - 1) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp) // Altezza del divisore
                        .background(color = colorScheme.outline.copy(alpha = 0.5f)) // Colore del divisore più sottile
                        .align(Alignment.CenterVertically) // Allinea il divisore verticalmente
                )
            }
        }
    }
}


@Composable
fun NotificationBox( // Lasciato pubblico se usato altrove, altrimenti private
    colorScheme: ColorScheme,
    typography: Typography
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colorScheme.secondaryContainer, // Usa secondaryContainer per coerenza
        border = BorderStroke(1.dp, colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Questa non è una prenotazione effettiva:",
                style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = colorScheme.onSecondaryContainer // Testo su secondaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "La tua richiesta sarà inviata all'inserzionista che si occuperà di ricontattarti.",
                style = typography.bodySmall,
                color = colorScheme.onSecondaryContainer // Testo su secondaryContainer
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppointmentBookingScreenPreview() { // Rinominato per coerenza
    val navController = rememberNavController()
    AppointmentBookingScreen(navController = navController)
}
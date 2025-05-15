package com.dieti.dietiestates25.ui.screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.*

import android.annotation.SuppressLint

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@SuppressLint("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentBookingScreen(
    navController: NavController
) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography

        var selectedDate by remember { mutableStateOf(LocalDate.of(2025, 8, 17)) }
        var selectedTimeSlot by remember { mutableStateOf<Int?>(0) } // Default to first time slot
        val scrollState = rememberScrollState()
        val haptic = LocalHapticFeedback.current

        Scaffold(
            // Gestisce automaticamente insets per status bar, navigation bar e keyboard
            modifier = Modifier.fillMaxSize(),

            topBar = {
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
                                contentDescription = "Chiudi"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.primary,
                        titleContentColor = colorScheme.onPrimary
                    ),
                    modifier = Modifier.statusBarsPadding()
                )
            },
            bottomBar = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    HorizontalDivider(color = colorScheme.onBackground, thickness = 1.dp)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(colorScheme.background)
                            .padding(16.dp),
                    ) {
                        Button(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                // Qui puoi usare la data e l'orario selezionati per le operazioni successive
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary
                            )
                        ) {
                            Text(
                                text = "Prosegui",
                                color = colorScheme.onPrimary,
                                style = typography.labelLarge
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
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
                        .imePadding() // Gestisce la tastiera
                ) {
                    HorizontalDivider(color = colorScheme.onBackground, thickness = 1.dp)

                    // Heading
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Seleziona il tuo giorno disponibile",
                            style = typography.titleMedium,
                            color = colorScheme.onBackground
                        )
                    }

                    // Main content area
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))

                        CalendarView(
                            selectedDate = selectedDate,
                            onDateSelected = { selectedDate = it },
                            colorScheme = colorScheme,
                            typography = typography
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "Scegli la fascia oraria",
                            style = typography.titleMedium,
                            color = colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        TimeSlotSelector(
                            selectedTimeSlot = selectedTimeSlot,
                            onTimeSlotSelected = { selectedTimeSlot = it },
                            colorScheme = colorScheme,
                            typography = typography
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        NotificationBox(colorScheme, typography)

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CalendarView(
    selectedDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography
) {
    // Stato per tenere traccia della data selezionata internamente nel componente
    var internalSelectedDate by remember { mutableStateOf(selectedDate) }

    // Utilizziamo lo stato per consentire la navigazione tra i mesi
    var viewingDate by remember { mutableStateOf(internalSelectedDate) }

    // Aggiorniamo la data iniziale per mostrare il mese corrente
    val currentDate = remember { LocalDate.now() }

    // Usiamo LaunchedEffect per impostare la data iniziale solo all'avvio
    LaunchedEffect(Unit) {
        // Impostare la data corrente come selezionata all'avvio
        internalSelectedDate = currentDate
        viewingDate = currentDate
        onDateSelected(currentDate)
    }

    val currentMonth = remember(viewingDate) { viewingDate.month }
    val currentYear = remember(viewingDate) { viewingDate.year }
    val firstDayOfMonth = remember(currentMonth, currentYear) {
        LocalDate.of(currentYear, currentMonth, 1)
    }

    // Funzione ausiliaria per verificare se un anno è bisestile
    fun isLeapYear(year: Int): Boolean {
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
    }

    val daysInMonth = remember(currentMonth, currentYear) {
        when (currentMonth) {
            Month.FEBRUARY -> if (isLeapYear(currentYear)) 29 else 28
            Month.APRIL, Month.JUNE, Month.SEPTEMBER, Month.NOVEMBER -> 30
            else -> 31
        }
    }

    // In Italia il primo giorno della settimana è lunedì (1), quindi non serve l'aggiustamento
    val firstDayOfWeek = remember(firstDayOfMonth) {
        firstDayOfMonth.dayOfWeek.value - 1 // 0 per lunedì, 6 per domenica
    }

    // Giorni della settimana in italiano: Lunedì, Martedì, Mercoledì, Giovedì, Venerdì, Sabato, Domenica
    val weekdays = listOf("L", "M", "M", "G", "V", "S", "D")

    // Definizione dei colori
    val selectedTextColor = colorScheme.onPrimaryContainer
    val weekendColor = colorScheme.onPrimary
    val holidayColor = colorScheme.error

    // Funzione per verificare se una data è un giorno festivo in Italia
    fun isHoliday(date: LocalDate): Boolean {
        val month = date.monthValue
        val day = date.dayOfMonth

        // Festività fisse in Italia
        return when {
            // Capodanno
            month == 1 && day == 1 -> true
            // Epifania
            month == 1 && day == 6 -> true
            // Festa della Liberazione
            month == 4 && day == 25 -> true
            // Festa dei lavoratori
            month == 5 && day == 1 -> true
            // Festa della Repubblica
            month == 6 && day == 2 -> true
            // Ferragosto
            month == 8 && day == 15 -> true
            // Tutti i Santi
            month == 11 && day == 1 -> true
            // Immacolata Concezione
            month == 12 && day == 8 -> true
            // Natale
            month == 12 && day == 25 -> true
            // Santo Stefano
            month == 12 && day == 26 -> true
            // Weekend (Sabato e Domenica)
            date.dayOfWeek == DayOfWeek.SATURDAY -> true
            date.dayOfWeek == DayOfWeek.SUNDAY -> true
            // Pasqua e Lunedì dell'Angelo richiederebbero un calcolo più complesso
            else -> false
        }
    }

    // Funzioni per navigare tra i mesi
    val goToPreviousMonth = {
        viewingDate = viewingDate.minusMonths(1)
    }

    val goToNextMonth = {
        viewingDate = viewingDate.plusMonths(1)
    }

    // Funzione per gestire la selezione di una data e aggiornare sia lo stato interno che quello esterno
    val handleDateSelection = { date: LocalDate ->
        internalSelectedDate = date
        onDateSelected(date)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colorScheme.primary
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Seleziona una data",
                color = colorScheme.onPrimary,
                style = typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Formato data italiana: gio, 29 apr
                val dateFormatter = DateTimeFormatter.ofPattern("EEE, d MMM", Locale.ITALIAN)
                val formattedDate = internalSelectedDate.format(dateFormatter)

                Text(
                    text = formattedDate,
                    style = typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = colorScheme.onPrimary,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Formato mese e anno in italiano: Aprile 2025
                val monthYearFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ITALIAN)
                val formattedMonthYear = firstDayOfMonth.format(monthYearFormatter)

                // Capitalizzazione prima lettera del mese
                val capitalizedMonthYear = formattedMonthYear.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }

                Text(
                    text = capitalizedMonthYear,
                    style = typography.titleMedium,
                    color = colorScheme.onPrimary,
                    modifier = Modifier.weight(1f)
                )

                // Pulsante mese precedente con funzionalità
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Mese precedente",
                    tint = colorScheme.onPrimary,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { goToPreviousMonth() }
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Pulsante mese successivo con funzionalità
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Mese successivo",
                    tint = colorScheme.onPrimary,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { goToNextMonth() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Intestazioni dei giorni della settimana
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                weekdays.forEachIndexed { index, day ->
                    // Colora di rosso Sabato (index 5) e Domenica (index 6)
                    val isWeekend = index == 5 || index == 6
                    val dayColor = if (isWeekend) holidayColor else colorScheme.onPrimary

                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            style = typography.bodyMedium,
                            color = dayColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Griglia del calendario
            val totalCells = if (firstDayOfWeek + daysInMonth <= 35) 35 else 42

            for (i in 0 until totalCells / 7) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    for (j in 0 until 7) {
                        val dayIndex = i * 7 + j
                        val dayOfMonth = dayIndex - firstDayOfWeek + 1

                        if (dayOfMonth in 1..daysInMonth) {
                            val date = LocalDate.of(currentYear, currentMonth, dayOfMonth)
                            val isSelected = date.equals(internalSelectedDate)
                            val isToday = date.equals(currentDate)
                            val isHoliday = isHoliday(date)

                            // Determina il colore del testo in base alla data
                            val textColor = when {
                                isSelected -> selectedTextColor // Colore personalizzato quando selezionato
                                isHoliday -> holidayColor // Colore rosso per i giorni festivi
                                else -> weekendColor // Colore bianco per i giorni normali
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            isSelected -> Color(0x99FFFFFF)
                                            isToday -> Color(0x33FFFFFF)
                                            else -> colorScheme.surfaceDim
                                        }
                                    )
                                    .clickable { handleDateSelection(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayOfMonth.toString(),
                                    style = typography.bodyMedium.copy(
                                        fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
                                    ),
                                    color = textColor
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun TimeSlotSelector(
    selectedTimeSlot: Int?,
    onTimeSlotSelected: (Int) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography
) {
    val timeSlots = listOf("9-12", "12-14", "14-17", "17-20")

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        timeSlots.forEachIndexed { index, slot ->
            val isSelected = index == selectedTimeSlot
            val shape = when (index) {
                0 -> RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)
                timeSlots.size - 1 -> RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
                else -> RoundedCornerShape(0.dp)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(shape)
                    .background(
                        if (isSelected) colorScheme.primary else colorScheme.secondary
                    )
                    .clickable { onTimeSlotSelected(index) }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = slot,
                            color = if (isSelected) colorScheme.onPrimary else colorScheme.onSecondary,
                            style = typography.labelLarge
                        )
                    }

                    // Aggiungi il divisore solo tra i pulsanti (non dopo l'ultimo)
                    if (index < timeSlots.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(24.dp) // Altezza del divisore ridotta
                                .background(color = colorScheme.background)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationBox(
    colorScheme: ColorScheme,
    typography: Typography
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = colorScheme.secondary,
        border = BorderStroke(1.dp, colorScheme.outline)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Questa non è una prenotazione effettiva:",
                style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = colorScheme.onSecondary
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "La tua richiesta sarà inviata all'inserzionista che si occuperà di ricontattarti.",
                style = typography.bodySmall,
                color = colorScheme.onSecondary
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppointmentBookingActivityPreview() {
    val navController = rememberNavController()
    Surface(
        modifier = Modifier.fillMaxSize(),
    ) {
        AppointmentBookingScreen(navController = navController)
    }
}
package com.dieti.dietiestates25.ui.screen

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieti.dietiestates25.ui.theme.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.*
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun AppointmentBookingScreen(
    navController : NavController
) {
    var selectedDate by remember { mutableStateOf(LocalDate.of(2025, 8, 17)) }
    var selectedTimeSlot by remember { mutableStateOf<Int?>(0) } // Default to first time slot
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeutralLight)
    ) {
        AppointmentHeader(navController)

        BoxWithConstraints {
            val screenHeight = maxHeight

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(screenHeight - 80.dp) // Riserva spazio per header e button
                    .verticalScroll(scrollState)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Seleziona il tuo giorno disponibile",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = GrayBlue
                )

                Spacer(modifier = Modifier.height(16.dp))

                CalendarView(
                    selectedDate = selectedDate,
                    onDateSelected = { selectedDate = it }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Scegli la fascia oraria",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = GrayBlue
                )

                Spacer(modifier = Modifier.height(12.dp))

                TimeSlotSelector(
                    selectedTimeSlot = selectedTimeSlot,
                    onTimeSlotSelected = { selectedTimeSlot = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                NotificationBox()
            }
        }
        Column(modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(
                color = Color.LightGray,
                thickness = 1.dp
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NeutralLight)
                    .padding(16.dp),
            ) {
                ContinueButton()
            }
        }
    }
}

@Composable
fun AppointmentHeader(
    navController : NavController
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    navController.popBackStack()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    modifier = Modifier.size(24.dp),
                    tint = GrayBlue
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = "Prenota una visita",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = GrayBlue
            )
        }
    }

    HorizontalDivider(thickness = 1.dp, color = Color.LightGray)
}

@Composable
fun CalendarView(
    selectedDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit
) {
    // Utilizziamo lo stato per consentire la navigazione tra i mesi
    var viewingDate by remember { mutableStateOf(selectedDate) }

    // Aggiorniamo la data iniziale per mostrare il mese corrente
    // Questa è necessaria solo all'avvio dell'app
    val currentDate = remember { LocalDate.now() }

    // Usiamo LaunchedEffect per impostare la data iniziale solo all'avvio
    LaunchedEffect(Unit) {
        viewingDate = currentDate
        // Se selectedDate è la data di default, aggiorniamola anche
        if (selectedDate == LocalDate.now()) {
            onDateSelected(currentDate)
        }
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

    // Funzioni per navigare tra i mesi
    val goToPreviousMonth = {
        viewingDate = viewingDate.minusMonths(1)
    }

    val goToNextMonth = {
        viewingDate = viewingDate.plusMonths(1)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = TealVibrant
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Seleziona una data",
                color = Color.White,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Formato data italiana: gio, 29 apr
                val dateFormatter = DateTimeFormatter.ofPattern("EEE, d MMM", Locale.ITALIAN)
                val formattedDate = selectedDate.format(dateFormatter)

                Text(
                    text = formattedDate,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
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

                Text(
                    text = formattedMonthYear,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )

                // Pulsante mese precedente con funzionalità
                Icon(
                    imageVector = Icons.Default.KeyboardArrowLeft,
                    contentDescription = "Mese precedente",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { goToPreviousMonth() }
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Pulsante mese successivo con funzionalità
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = "Mese successivo",
                    tint = Color.White,
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
                weekdays.forEach { day ->
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = day,
                            fontSize = 16.sp,
                            color = Color.White
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
                            val isSelected = date.equals(selectedDate)
                            val isWeekend = date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
                            val textColor = if (isWeekend) Color(0xFFE57373) else Color.White

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) Color(0x99FFFFFF) else Color.Transparent)
                                    .clickable { onDateSelected(date) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = dayOfMonth.toString(),
                                    fontSize = 16.sp,
                                    color = textColor,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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
    onTimeSlotSelected: (Int) -> Unit
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
                        if (isSelected) TealVibrant else TealLight
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
                            color = if (isSelected) Color.White else GrayBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Aggiungi il divisore solo tra i pulsanti (non dopo l'ultimo)
                    if (index < timeSlots.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(24.dp) // Altezza del divisore ridotta
                                .background(Color.Black)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationBox() {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = TealLight,
        border = BorderStroke(1.dp, Color.Gray)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Questa non è una prenotazione effettiva:",
                fontWeight = FontWeight.Medium,
                color = GrayBlue
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "La tua richiesta sarà inviata all'inserzionista che si occuperà di ricontattarti.",
                color = GrayBlue,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun ContinueButton() {

    Button(
        onClick = { /* Continue action */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TealVibrant
        )
    ) {
        Text(
            text = "Prosegui",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
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
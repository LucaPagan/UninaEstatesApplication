package com.dieti.dietiestates25.ui.screen

import com.dieti.dietiestates25.ui.components.CalendarView
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.CircularIconActionButton
import com.dieti.dietiestates25.ui.theme.Dimensions

import java.time.LocalDate

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedback
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
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
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
                    println("Data selezionata: $selectedDate, Fascia oraria: ${selectedTimeSlot?.let { it + 1 }}")
                },
                dimensions = dimensions
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
            typography = typography,
            dimensions = dimensions
        )
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
                CircularIconActionButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        navController.popBackStack()
                    },
                    iconVector = Icons.Default.Close,
                    contentDescription = "Chiudi",
                    backgroundColor = colorScheme.primaryContainer,
                    iconTint = colorScheme.onPrimaryContainer
                    // buttonSize e iconSize usano i default di CircularIconActionButton (40.dp e 24.dp)
                    // Se questi valori (40.dp, 24.dp) non sono in Dimensions, rimangono così
                )
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = colorScheme.primary,
                titleContentColor = colorScheme.onPrimary,
                navigationIconContentColor = colorScheme.onPrimaryContainer,
                actionIconContentColor = colorScheme.onPrimary
            ),
            modifier = Modifier.statusBarsPadding()
        )
        HorizontalDivider(
            color = colorScheme.onPrimary.copy(alpha = 0.3f),
            thickness = 1.dp // 1.dp non in Dimensions, lasciato invariato
        )
    }
}


@Composable
private fun AppointmentBookingBottomBar(
    haptic: HapticFeedback,
    colorScheme: ColorScheme,
    onProceedClick: () -> Unit,
    dimensions: Dimensions // Aggiunto
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        HorizontalDivider(
            color = colorScheme.onSurfaceVariant,
            thickness = 1.dp // 1.dp non in Dimensions, lasciato invariato
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.surface)
                .padding(dimensions.paddingMedium), // SOSTITUITO 16.dp
        ) {
            AppPrimaryButton(
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
    typography: Typography,
    dimensions: Dimensions // Aggiunto
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
                .padding(horizontal = dimensions.paddingMedium) // SOSTITUITO 16.dp
                .imePadding()
        ) {
            Spacer(modifier = Modifier.height(dimensions.spacingMedium)) // SOSTITUITO 16.dp
            ScreenSectionTitle(
                title = "Seleziona il tuo giorno disponibile",
                colorScheme = colorScheme,
                typography = typography
            )
            Spacer(modifier = Modifier.height(dimensions.spacingSmall)) // SOSTITUITO 8.dp
            CalendarView(
                initialSelectedDate = selectedDate,
                onDateSelected = onDateSelected,
                colorScheme = colorScheme,
                typography = typography
            )
            Spacer(modifier = Modifier.height(dimensions.spacingLarge)) // SOSTITUITO 24.dp
            ScreenSectionTitle(
                title = "Scegli la fascia oraria",
                colorScheme = colorScheme,
                typography = typography
            )
            Spacer(modifier = Modifier.height(12.dp)) // 12.dp non in Dimensions, lasciato invariato
            TimeSlotSelector(
                selectedTimeSlot = selectedTimeSlot,
                onTimeSlotSelected = onTimeSlotSelected,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
            Spacer(modifier = Modifier.height(dimensions.spacingLarge)) // SOSTITUITO 24.dp
            NotificationBox(
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
            Spacer(modifier = Modifier.height(dimensions.spacingLarge)) // SOSTITUITO 24.dp
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
        color = colorScheme.onSurface
    )
}


@Composable
fun TimeSlotSelector(
    selectedTimeSlot: Int?,
    onTimeSlotSelected: (Int) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions // Aggiunto
) {
    val timeSlots = remember { listOf("9-12", "12-14", "14-17", "17-20") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(0.dp) // 0.dp ok
    ) {
        timeSlots.forEachIndexed { index, slot ->
            val isSelected = index == selectedTimeSlot
            val shape = when (index) {
                0 -> RoundedCornerShape(topStart = dimensions.cornerRadiusLarge, bottomStart = dimensions.cornerRadiusLarge, topEnd = 0.dp, bottomEnd = 0.dp) // SOSTITUITO 24.dp
                timeSlots.size - 1 -> RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = dimensions.cornerRadiusLarge, bottomEnd = dimensions.cornerRadiusLarge) // SOSTITUITO 24.dp
                else -> RoundedCornerShape(0.dp) // 0.dp ok
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(dimensions.iconSizeExtraLarge) // SOSTITUITO 48.dp
                    .clip(shape)
                    .background(
                        if (isSelected) colorScheme.primary else colorScheme.secondary
                    )
                    .clickable { onTimeSlotSelected(index) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = slot,
                    color = if (isSelected) colorScheme.onPrimary else colorScheme.onSecondary,
                    style = typography.labelLarge
                )
            }
            if (index < timeSlots.size - 1) {
                Box(
                    modifier = Modifier
                        .width(1.dp) // 1.dp non in Dimensions
                        .height(dimensions.spacingLarge) // SOSTITUITO 24.dp
                        .background(color = colorScheme.outline.copy(alpha = 0.5f))
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Composable
fun NotificationBox(
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions // Aggiunto
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.cornerRadiusMedium), // SOSTITUITO 12.dp
        color = colorScheme.secondaryContainer,
        border = BorderStroke(1.dp, colorScheme.outline) // 1.dp non in Dimensions
    ) {
        Column(
            modifier = Modifier.padding(dimensions.paddingMedium) // SOSTITUITO 16.dp
        ) {
            Text(
                text = "Questa non è una prenotazione effettiva:",
                style = typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(dimensions.spacingExtraSmall)) // SOSTITUITO 4.dp
            Text(
                text = "La tua richiesta sarà inviata all'inserzionista che si occuperà di ricontattarti.",
                style = typography.bodySmall,
                color = colorScheme.onSecondaryContainer
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
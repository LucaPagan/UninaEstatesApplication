package com.dieti.dietiestates25.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.dieti.dietiestates25.ui.theme.Dimensions
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun CalendarView(
    initialSelectedDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    modifier: Modifier = Modifier,
    dimensions: Dimensions = Dimensions,
    highlightedDate: LocalDate? = LocalDate.now(), // Data da evidenziare (es. oggi)
    disabledDates: Set<LocalDate> = emptySet() // Date da disabilitare
) {
    var internalSelectedDate by remember { mutableStateOf(initialSelectedDate) }
    var viewingDate by remember { mutableStateOf(initialSelectedDate) }

    LaunchedEffect(initialSelectedDate) {
        internalSelectedDate = initialSelectedDate
        viewingDate = initialSelectedDate
        // Non chiamare onDateSelected qui per evitare doppia chiamata all'init
    }

    val currentMonth = remember(viewingDate) { viewingDate.month }
    val currentYear = remember(viewingDate) { viewingDate.year }
    val firstDayOfMonth = remember(currentMonth, currentYear) {
        LocalDate.of(currentYear, currentMonth, 1)
    }
    val daysInMonth = remember(currentMonth, currentYear) {
        currentMonth.length(isLeapYear(currentYear))
    }
    val firstDayOfWeekOffset = remember(firstDayOfMonth) {
        firstDayOfMonth.dayOfWeek.value - 1
    }
    val weekdays = remember { listOf("L", "M", "M", "G", "V", "S", "D") }

    val selectedTextColor = colorScheme.onPrimaryContainer
    val weekendDayColor = colorScheme.onSurfaceVariant // Colore per i giorni del weekend non festivi
    val holidayTextColor = colorScheme.error // Colore per le festività
    val disabledTextColor = colorScheme.onSurface.copy(alpha = 0.38f)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(dimensions.cornerRadiusLarge),
        color = colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier.padding(dimensions.paddingMedium)
        ) {
            CalendarHeader(
                viewingDate = viewingDate,
                colorScheme = colorScheme,
                typography = typography,
                onPreviousMonthClick = { viewingDate = viewingDate.minusMonths(1) },
                onNextMonthClick = { viewingDate = viewingDate.plusMonths(1) },
                dimensions = dimensions
            )
            Spacer(modifier = Modifier.height(dimensions.spacingMedium))
            WeekdayHeaders(
                weekdays = weekdays,
                weekendHeaderColor = colorScheme.error.copy(alpha = 0.7f), // Header weekend leggermente diverso
                defaultHeaderColor = colorScheme.onSurfaceVariant,
                typography = typography
            )
            Spacer(modifier = Modifier.height(dimensions.spacingSmall))
            CalendarGrid(
                currentYear = currentYear,
                currentMonth = currentMonth,
                daysInMonth = daysInMonth,
                firstDayOfWeekOffset = firstDayOfWeekOffset,
                internalSelectedDate = internalSelectedDate,
                highlightedDate = highlightedDate,
                disabledDates = disabledDates,
                colorScheme = colorScheme,
                typography = typography,
                selectedTextColor = selectedTextColor,
                holidayTextColor = holidayTextColor,
                weekendDayColor = weekendDayColor,
                disabledTextColor = disabledTextColor,
                onDateClick = { date ->
                    if (!disabledDates.contains(date)) {
                        internalSelectedDate = date
                        onDateSelected(date)
                    }
                },
                dimensions = dimensions
            )
            Spacer(modifier = Modifier.height(dimensions.spacingSmall))
        }
    }
}

@Composable
private fun CalendarHeader(
    viewingDate: LocalDate,
    colorScheme: ColorScheme,
    typography: Typography,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    dimensions: Dimensions
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CalendarNavigationIcon(
            icon = Icons.AutoMirrored.Filled.ArrowLeft,
            contentDescription = "Mese precedente",
            onClick = onPreviousMonthClick,
            tint = colorScheme.onSurfaceVariant,
            dimensions = dimensions
        )
        val monthYearFormatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ITALIAN) }
        val formattedMonthYear = viewingDate.format(monthYearFormatter).replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ITALIAN) else it.toString()
        }
        Text(
            text = formattedMonthYear,
            style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = dimensions.spacingSmall)
        )
        CalendarNavigationIcon(
            icon = Icons.AutoMirrored.Filled.ArrowRight,
            contentDescription = "Mese successivo",
            onClick = onNextMonthClick,
            tint = colorScheme.onSurfaceVariant,
            dimensions = dimensions
        )
    }
}

@Composable
private fun CalendarNavigationIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color,
    dimensions: Dimensions
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = tint,
        modifier = Modifier
            .size(dimensions.iconSizeMedium)
            .clickable(onClick = onClick)
    )
}

@Composable
private fun WeekdayHeaders(
    weekdays: List<String>,
    weekendHeaderColor: Color,
    defaultHeaderColor: Color,
    typography: Typography
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        weekdays.forEachIndexed { index, day ->
            val dayColor = if (index >= 5) weekendHeaderColor else defaultHeaderColor
            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                Text(text = day, style = typography.bodySmall.copy(fontWeight = FontWeight.Medium), color = dayColor)
            }
        }
    }
}

@Composable
private fun CalendarGrid(
    currentYear: Int, currentMonth: Month, daysInMonth: Int, firstDayOfWeekOffset: Int,
    internalSelectedDate: LocalDate, highlightedDate: LocalDate?, disabledDates: Set<LocalDate>,
    colorScheme: ColorScheme, typography: Typography, selectedTextColor: Color,
    holidayTextColor: Color, weekendDayColor: Color, disabledTextColor: Color,
    onDateClick: (LocalDate) -> Unit, dimensions: Dimensions
) {
    val totalCells = if (firstDayOfWeekOffset + daysInMonth <= 35) 35 else 42
    Column {
        for (i in 0 until totalCells / 7) {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = dimensions.spacingExtraSmall / 2)) {
                for (j in 0 until 7) {
                    val dayIndex = i * 7 + j
                    val dayOfMonth = dayIndex - firstDayOfWeekOffset + 1
                    if (dayOfMonth in 1..daysInMonth) {
                        val date = LocalDate.of(currentYear, currentMonth, dayOfMonth)
                        val isSelected = date == internalSelectedDate
                        val isHighlighted = date == highlightedDate && !isSelected // Evidenzia solo se non già selezionato
                        val isDisabled = disabledDates.contains(date)
                        val isPublicHoliday = isItalianHoliday(date)
                        val isWeekendDay = date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY

                        val textColor = when {
                            isDisabled -> disabledTextColor
                            isSelected -> selectedTextColor
                            isPublicHoliday -> holidayTextColor
                            isWeekendDay && !isHighlighted -> weekendDayColor // Colore weekend se non è oggi
                            else -> colorScheme.onSurface
                        }
                        val backgroundColor = when {
                            isSelected -> colorScheme.primaryContainer
                            isHighlighted -> colorScheme.tertiaryContainer.copy(alpha = 0.5f) // Colore per oggi
                            else -> Color.Transparent
                        }

                        DayCell(
                            day = dayOfMonth.toString(),
                            isSelected = isSelected,
                            isHighlighted = isHighlighted,
                            isDisabled = isDisabled,
                            textColor = textColor,
                            backgroundColor = backgroundColor,
                            typography = typography,
                            onClick = { onDateClick(date) },
                            dimensions = dimensions
                        )
                    } else {
                        EmptyDayCell(dimensions = dimensions)
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.DayCell(
    day: String, isSelected: Boolean, isHighlighted: Boolean, isDisabled: Boolean,
    textColor: Color, backgroundColor: Color, typography: Typography,
    onClick: () -> Unit, dimensions: Dimensions
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .padding(dimensions.spacingExtraSmall / 2)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(enabled = !isDisabled, onClick = onClick), // Disabilita click se isDisabled
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            style = typography.bodyMedium.copy(
                fontWeight = if (isSelected || isHighlighted) FontWeight.Bold else FontWeight.Normal,
                textDecoration = if (isDisabled) TextDecoration.LineThrough else null // Sbarra il testo se disabilitato
            ),
            color = textColor
        )
    }
}

@Composable
private fun RowScope.EmptyDayCell(dimensions: Dimensions) {
    Box(modifier = Modifier.weight(1f).aspectRatio(1f).padding(dimensions.spacingExtraSmall / 2))
}

private fun isLeapYear(year: Int): Boolean = year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)

private fun isItalianHoliday(date: LocalDate): Boolean {
    val month = date.monthValue
    val day = date.dayOfMonth
    val fixedHolidays = setOf(
        Pair(1, 1), Pair(1, 6), Pair(4, 25), Pair(5, 1),
        Pair(6, 2), Pair(8, 15), Pair(11, 1), Pair(12, 8),
        Pair(12, 25), Pair(12, 26)
    )
    if (fixedHolidays.contains(Pair(month, day))) return true
    val year = date.year
    val a = year % 19; val b = year / 100; val c = year % 100
    val d = b / 4; val e = b % 4; val f = (b + 8) / 25
    val g = (b - f + 1) / 3; val h = (19 * a + b - d - g + 15) % 30
    val i = c / 4; val k = c % 4; val l = (32 + 2 * e + 2 * i - h - k) % 7
    val m = (a + 11 * h + 22 * l) / 451
    val easterMonth = (h + l - 7 * m + 114) / 31
    val easterDay = ((h + l - 7 * m + 114) % 31) + 1
    val easterSunday = LocalDate.of(year, easterMonth, easterDay)
    val easterMonday = easterSunday.plusDays(1)
    return date.isEqual(easterSunday) || date.isEqual(easterMonday)
}

@Composable
fun TimeSlotSelector(
    selectedTimeSlotIndex: Int?,
    onTimeSlotSelected: (Int) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions,
    disabledTimeSlotIndices: Set<Int> = emptySet() // Nuovo parametro
) {
    val timeSlots = remember { listOf("9-12", "12-14", "14-17", "17-20") }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        timeSlots.forEachIndexed { index, slot ->
            val isSelected = index == selectedTimeSlotIndex
            val isDisabled = disabledTimeSlotIndices.contains(index) // Controlla se lo slot è disabilitato

            val shape = when (index) {
                0 -> RoundedCornerShape(topStart = dimensions.cornerRadiusMedium, bottomStart = dimensions.cornerRadiusMedium, topEnd = 0.dp, bottomEnd = 0.dp)
                timeSlots.size - 1 -> RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = dimensions.cornerRadiusMedium, bottomEnd = dimensions.cornerRadiusMedium)
                else -> RoundedCornerShape(0.dp)
            }
            val backgroundColor = when {
                isDisabled -> colorScheme.surfaceContainerLowest // Colore per disabilitato
                isSelected -> colorScheme.primary
                else -> colorScheme.surfaceContainer
            }
            val textColor = when {
                isDisabled -> colorScheme.onSurface.copy(alpha = 0.38f) // Testo disabilitato
                isSelected -> colorScheme.onPrimary
                else -> colorScheme.onSurfaceVariant
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(dimensions.iconSizeExtraLarge)
                    .clip(shape)
                    .background(backgroundColor)
                    .clickable(enabled = !isDisabled) { // Disabilita click se isDisabled
                        if (!isDisabled) onTimeSlotSelected(index)
                    }
                    .padding(horizontal = dimensions.paddingSmall),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = slot,
                    color = textColor,
                    style = typography.labelLarge.copy(
                        fontWeight = if (isSelected && !isDisabled) FontWeight.Bold else FontWeight.Normal,
                        textDecoration = if (isDisabled) TextDecoration.LineThrough else null // Sbarra testo se disabilitato
                    )
                )
            }
            if (index < timeSlots.size - 1) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(dimensions.spacingLarge * 0.6f)
                        .background(color = colorScheme.outline.copy(alpha = 0.5f))
                        .align(Alignment.CenterVertically)
                )
            }
        }
    }
}

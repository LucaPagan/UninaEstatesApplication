package com.dieti.dietiestates25.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowLeft
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * A composable function that displays a calendar view for selecting a date.
 *
 * @param initialSelectedDate The initially selected date. Defaults to the current date.
 * @param onDateSelected Callback function that is invoked when a date is selected.
 * @param colorScheme The MaterialTheme color scheme to use for styling.
 * @param typography The MaterialTheme typography to use for styling.
 * @param modifier Modifier for this composable.
 */
@Composable
fun CalendarView(
    initialSelectedDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    modifier: Modifier = Modifier
) {
    var internalSelectedDate by remember { mutableStateOf(initialSelectedDate) }
    var viewingDate by remember { mutableStateOf(initialSelectedDate) }
    val currentDate = remember { LocalDate.now() }

    // Initialize with the current date or provided initial date
    LaunchedEffect(initialSelectedDate) {
        internalSelectedDate = initialSelectedDate
        viewingDate = initialSelectedDate
        onDateSelected(initialSelectedDate) // Notify initial selection
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
        firstDayOfMonth.dayOfWeek.value - 1 // 0 for Monday, 6 for Sunday
    }

    val weekdays = remember { listOf("L", "M", "M", "G", "V", "S", "D") }

    val selectedTextColor = colorScheme.onPrimaryContainer
    val weekendColor = colorScheme.onPrimary
    val holidayColor = colorScheme.error

    val goToPreviousMonth = {
        viewingDate = viewingDate.minusMonths(1)
    }

    val goToNextMonth = {
        viewingDate = viewingDate.plusMonths(1)
    }

    val handleDateSelection = { date: LocalDate ->
        internalSelectedDate = date
        onDateSelected(date)
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = colorScheme.primary
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            CalendarHeader(
                internalSelectedDate = internalSelectedDate,
                firstDayOfMonth = firstDayOfMonth,
                colorScheme = colorScheme,
                typography = typography,
                onPreviousMonthClick = goToPreviousMonth,
                onNextMonthClick = goToNextMonth
            )

            Spacer(modifier = Modifier.height(16.dp))

            WeekdayHeaders(
                weekdays = weekdays,
                holidayColor = holidayColor,
                defaultColor = colorScheme.onPrimary,
                typography = typography
            )

            Spacer(modifier = Modifier.height(8.dp))

            CalendarGrid(
                currentYear = currentYear,
                currentMonth = currentMonth,
                daysInMonth = daysInMonth,
                firstDayOfWeekOffset = firstDayOfWeekOffset,
                internalSelectedDate = internalSelectedDate,
                currentDate = currentDate,
                colorScheme = colorScheme,
                typography = typography,
                selectedTextColor = selectedTextColor,
                holidayColor = holidayColor,
                weekendColor = weekendColor,
                onDateClick = handleDateSelection
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun CalendarHeader(
    internalSelectedDate: LocalDate,
    firstDayOfMonth: LocalDate,
    colorScheme: ColorScheme,
    typography: Typography,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit
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
        val dateFormatter = remember { DateTimeFormatter.ofPattern("EEE, d MMM", Locale.ITALIAN) }
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
        val monthYearFormatter = remember { DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ITALIAN) }
        val formattedMonthYear = firstDayOfMonth.format(monthYearFormatter)
        val capitalizedMonthYear = formattedMonthYear.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.ITALIAN) else it.toString()
        }
        Text(
            text = capitalizedMonthYear,
            style = typography.titleMedium,
            color = colorScheme.onPrimary,
            modifier = Modifier.weight(1f)
        )
        CalendarNavigationIcon(
            icon = Icons.AutoMirrored.Filled.ArrowLeft,
            contentDescription = "Mese precedente",
            onClick = onPreviousMonthClick,
            tint = colorScheme.onPrimary
        )
        Spacer(modifier = Modifier.width(12.dp))
        CalendarNavigationIcon(
            icon = Icons.AutoMirrored.Filled.ArrowRight,
            contentDescription = "Mese successivo",
            onClick = onNextMonthClick,
            tint = colorScheme.onPrimary
        )
    }
}

@Composable
private fun CalendarNavigationIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    tint: Color
) {
    Icon(
        imageVector = icon,
        contentDescription = contentDescription,
        tint = tint,
        modifier = Modifier
            .size(24.dp)
            .clickable(onClick = onClick)
    )
}

@Composable
private fun WeekdayHeaders(
    weekdays: List<String>,
    holidayColor: Color,
    defaultColor: Color,
    typography: Typography
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        weekdays.forEachIndexed { index, day ->
            val isWeekend = index == 5 || index == 6
            val dayColor = if (isWeekend) holidayColor else defaultColor
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
}

@Composable
private fun CalendarGrid(
    currentYear: Int,
    currentMonth: Month,
    daysInMonth: Int,
    firstDayOfWeekOffset: Int,
    internalSelectedDate: LocalDate,
    currentDate: LocalDate,
    colorScheme: ColorScheme,
    typography: Typography,
    selectedTextColor: Color,
    holidayColor: Color,
    weekendColor: Color,
    onDateClick: (LocalDate) -> Unit
) {
    val totalCells = if (firstDayOfWeekOffset + daysInMonth <= 35) 35 else 42
    Column {
        for (i in 0 until totalCells / 7) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                for (j in 0 until 7) {
                    val dayIndex = i * 7 + j
                    val dayOfMonth = dayIndex - firstDayOfWeekOffset + 1

                    if (dayOfMonth in 1..daysInMonth) {
                        val date = LocalDate.of(currentYear, currentMonth, dayOfMonth)
                        val isSelected = date.equals(internalSelectedDate)
                        val isToday = date.equals(currentDate)
                        val isPublicHoliday = isItalianHoliday(date)

                        val textColor = when {
                            isSelected -> selectedTextColor
                            isPublicHoliday -> holidayColor
                            else -> weekendColor
                        }
                        val backgroundColor = when {
                            isSelected -> Color(0x99FFFFFF) // Semi-transparent white for selection
                            isToday -> Color(0x33FFFFFF)    // More transparent white for today
                            else -> colorScheme.surfaceDim   // Default background from theme
                        }

                        DayCell(
                            day = dayOfMonth.toString(),
                            isSelected = isSelected,
                            isToday = isToday,
                            textColor = textColor,
                            backgroundColor = backgroundColor,
                            typography = typography,
                            onClick = { onDateClick(date) }
                        )
                    } else {
                        EmptyDayCell()
                    }
                }
            }
        }
    }
}

@Composable
private fun RowScope.DayCell(
    day: String,
    isSelected: Boolean,
    isToday: Boolean,
    textColor: Color,
    backgroundColor: Color,
    typography: Typography,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day,
            style = typography.bodyMedium.copy(
                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal
            ),
            color = textColor
        )
    }
}

@Composable
private fun RowScope.EmptyDayCell() {
    Box(
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
    )
}

// Helper function for leap year
private fun isLeapYear(year: Int): Boolean {
    return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)
}

// Helper function to check for Italian holidays
private fun isItalianHoliday(date: LocalDate): Boolean {
    val month = date.monthValue
    val day = date.dayOfMonth

    // Fixed holidays
    val fixedHolidays = setOf(
        Pair(1, 1),   // Capodanno
        Pair(1, 6),   // Epifania
        Pair(4, 25),  // Festa della Liberazione
        Pair(5, 1),   // Festa dei lavoratori
        Pair(6, 2),   // Festa della Repubblica
        Pair(8, 15),  // Ferragosto
        Pair(11, 1),  // Tutti i Santi
        Pair(12, 8),  // Immacolata Concezione
        Pair(12, 25), // Natale
        Pair(12, 26)  // Santo Stefano
    )
    if (fixedHolidays.contains(Pair(month, day))) {
        return true
    }

    // Easter Sunday and Easter Monday (Pasquetta) - requires complex calculation
    // For simplicity, this example does not calculate Easter.
    // A full implementation would require an algorithm like the Gregorian algorithm.

    // Also mark Saturdays and Sundays as "holidays" for coloring purposes in the calendar
    return date.dayOfWeek == DayOfWeek.SATURDAY || date.dayOfWeek == DayOfWeek.SUNDAY
}
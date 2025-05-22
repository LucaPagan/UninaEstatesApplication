package com.dieti.dietiestates25.ui.model

import java.time.LocalDate

data class Notification(
    val id: Int,
    val senderType: String,
    val message: String,
    val iconType: NotificationIconType,
    val date: LocalDate,
    var isFavorite: Boolean = false,
)

enum class NotificationIconType {
    PHONE, PERSON, BADGE
}

data class Appointment(
    val id: Int,
    val title: String,
    val description: String?,
    val iconType: AppointmentIconType,
    val date: LocalDate,
    val timeSlot: String,
)

enum class AppointmentIconType {
    VISIT, MEETING, GENERIC
}

val TimeSlots = listOf("9-12", "12-14", "14-17", "17-20")
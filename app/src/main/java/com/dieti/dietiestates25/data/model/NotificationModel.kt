package com.dieti.dietiestates25.data.model

data class Notification(
    val id: String, // Aggiornato a String per supportare UUID
    val title: String, // Aggiunto Titolo
    val senderType: String, // Derivato o statico
    val message: String, // Mappato da 'corpo'
    val iconType: NotificationIconType,
    val date: String, // Usiamo String per la data formattata o raw ISO
    val isRead: Boolean, // Mappato da 'letto'
    var isFavorite: Boolean = false,
)

enum class NotificationIconType {
    PHONE, PERSON, BADGE
}

data class Appointment(
    val id: String, // Aggiornato a String per supportare UUID
    val title: String,
    val description: String?,
    val iconType: AppointmentIconType?,
    val date: String?, // String per semplicità di mapping
    val status: String?,
    val timeSlot: String,
    val clientName: String?,
    val propertyAddress: String?,
    var isFavorite: Boolean = false,
    val notes: String?,
    val propertyName: String // Aggiunto per comodità UI
)

enum class AppointmentIconType {
    VISIT, MEETING, GENERIC
}

val TimeSlots = listOf("9-12", "12-14", "14-17", "17-20")
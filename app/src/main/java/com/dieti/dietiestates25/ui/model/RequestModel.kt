package com.dieti.dietiestates25.ui.model

import java.time.LocalDate

// Enum per rappresentare i tre stati possibili di una richiesta
enum class StatoRichiesta {
    IN_ACCETTAZIONE,
    CONFERMATA,
    RIFIUTATA
}

// Data class che rappresenta una singola richiesta
data class Richiesta(
    val id: Int,
    val titolo: String,
    val descrizione: String,
    val data: LocalDate,
    val stato: StatoRichiesta,
    // Puoi aggiungere un tipo di icona se necessario, come per le notifiche
    // val iconType: RichiestaIconType = RichiestaIconType.GENERIC
)
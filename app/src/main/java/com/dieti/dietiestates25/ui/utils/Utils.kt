package com.dieti.dietiestates25.ui.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.math.roundToInt

/**
 * Funzione di estensione per trovare l'Activity a partire da un Context.
 * Utile perché LocalView.current.context in alcuni casi (es. Dialog, BottomSheet)
 * potrebbe non essere direttamente un'Activity.
 */
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/**
 * Formatta un valore Float come stringa di valuta.
 * Esempio: 123456.78f -> "€ 123.456,78" (dipende dalla Locale)
 *
 * @param locale La Locale da usare per la formattazione (default: Italia).
 * @param minimumFractionDigits Minimo numero di cifre decimali.
 * @param maximumFractionDigits Massimo numero di cifre decimali.
 * @return Stringa formattata o null se il valore è null.
 */
fun Float?.formatAsCurrency(
    locale: Locale = Locale.ITALY, // Default per l'Italia
    minimumFractionDigits: Int = 2,
    maximumFractionDigits: Int = 2
): String? {
    if (this == null) return null
    return try {
        val format = NumberFormat.getCurrencyInstance(locale)
        format.minimumFractionDigits = minimumFractionDigits
        format.maximumFractionDigits = maximumFractionDigits
        format.format(this)
    } catch (e: Exception) {
        // In caso di problemi con la formattazione, restituisci il numero come stringa semplice
        // o gestisci l'errore come preferisci.
        this.toString()
    }
}

/**
 * Formatta un valore Float come stringa con unità di misura (es. per area).
 * Esempio: 75.5f, "mq" -> "75 mq" (arrotonda all'intero) o "75.5 mq"
 *
 * @param unitSuffix Il suffisso dell'unità di misura (es. "mq", "km").
 * @param includeDecimals Se true, include una cifra decimale se presente.
 * @return Stringa formattata o null se il valore è null.
 */
fun Float?.formatWithUnit(unitSuffix: String, includeDecimals: Boolean = false): String? {
    if (this == null) return null
    return if (includeDecimals) {
        // Formatta con una cifra decimale se il numero non è intero
        if (this % 1 == 0f) {
            "${this.toInt()} $unitSuffix"
        } else {
            String.format(Locale.ITALIAN, "%.1f $unitSuffix", this)
        }
    } else {
        "${this.roundToInt()} $unitSuffix"
    }
}

/**
 * Esegue il parsing di una stringa di prezzo (potenzialmente con simboli di valuta e separatori)
 * in un Float. Questa è una versione base, potresti doverla rendere più robusta
 * per gestire diversi formati internazionali.
 *
 * @param priceString La stringa da analizzare.
 * @return Il valore Float o null se il parsing fallisce o la stringa è nulla/vuota.
 */
fun String?.parsePriceToFloat(): Float? {
    if (this.isNullOrBlank()) return null
    return try {
        // Rimuove simboli di valuta comuni, spazi, e separatori delle migliaia (punto).
        // Converte la virgola decimale in punto per il parsing a Float.
        val cleanedString = this
            .replace("€", "")
            .replace("$", "")
            .replace(".", "") // Rimuove i separatori delle migliaia (se sono punti)
            .replace(",", ".") // Converte la virgola decimale in punto
            .trim()
        cleanedString.toFloatOrNull()
    } catch (e: Exception) {
        null // Restituisce null se il parsing fallisce
    }
}

/**
 * Semplice funzione per capitalizzare la prima lettera di una stringa.
 */
fun String.capitalizeFirstLetter(): String {
    if (this.isEmpty()) return this
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

// Potresti aggiungere altre funzioni di utility qui, ad esempio:
// - Validatori semplici (es. per email, se non usi librerie apposite)
// - Funzioni per manipolare date (se non usi ThreeTenABP o java.time)
// - Ecc.
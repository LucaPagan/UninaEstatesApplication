package com.dieti.dietiestates25.ui.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToInt
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Balcony
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Countertops
import androidx.compose.material.icons.filled.Garage
import androidx.compose.material.icons.filled.KingBed
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Weekend
import androidx.compose.ui.graphics.vector.ImageVector

object Utils {

    // Funzione per convertire un URI (content://...) in un File reale
    fun getFileFromUri(context: Context, uri: Uri): File? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream: InputStream? = contentResolver.openInputStream(uri)

            // Crea un file temporaneo nella cache dell'app
            val tempFile = File.createTempFile("upload_image", ".jpg", context.cacheDir)
            tempFile.deleteOnExit() // Pulisce automaticamente all'uscita

            val outputStream = FileOutputStream(tempFile)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            tempFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Helper per formattare stringhe (es. prima lettera maiuscola)
    fun String.capitalizeFirstLetter(): String {
        return this.lowercase().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
    }
}
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

/**
 * Funzione condivisa per mappare le stringhe (tipologia stanza) alle Icone Material.
 * Accessibile da tutte le schermate nel package 'property'.
 */
fun getIconForRoomType(type: String): ImageVector {
    val t = type.lowercase(Locale.ROOT)
    return when {
        t.contains("letto") || t.contains("camera") -> Icons.Default.KingBed
        t.contains("bagno") -> Icons.Default.Bathtub
        // Countertops è spesso incluso nelle extended, se non lo trovi usa Icons.Default.Kitchen o altro
        t.contains("cucina") -> Icons.Default.Countertops
        t.contains("soggiorno") || t.contains("salone") || t.contains("sala") -> Icons.Default.Weekend
        t.contains("studio") -> Icons.Default.MenuBook
        t.contains("balcone") || t.contains("terrazz") -> Icons.Default.Balcony
        t.contains("garage") || t.contains("auto") -> Icons.Default.Garage
        else -> Icons.Default.MeetingRoom
    }
}
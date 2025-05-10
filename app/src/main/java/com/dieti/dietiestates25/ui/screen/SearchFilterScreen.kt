package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.RowScopeInstance.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
// Corretto l'import per KeyboardOptions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

// Aggiungiamo le opt-in necessarie per le API sperimentali di Material 3, se usate
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchFilterScreen(
    navController: NavController,
    idUtente: String = "utente",
    ricerca: String = "varcaturo", // Questo valore potrebbe essere usato per pre-impostare filtri o mostrare contesto
    onNavigateBack: () -> Unit = { navController.popBackStack() } // Default back navigation
) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography

        // State per i filtri selezionati
        var selectedPurchaseType by remember { mutableStateOf<String?>(null) }
        var selectedBathrooms by remember { mutableStateOf<Int?>(null) }
        var selectedCondition by remember { mutableStateOf<String?>(null) }

        // State per i valori dei campi di input (es. prezzo, superficie, locali)
        var minPrice by remember { mutableStateOf("") }
        var maxPrice by remember { mutableStateOf("") }
        var minSurface by remember { mutableStateOf("") }
        var maxSurface by remember { mutableStateOf("") }
        var minRooms by remember { mutableStateOf("") }
        var maxRooms by remember { mutableStateOf("") }


        Scaffold( // Usiamo Scaffold per una struttura standard con top bar e content
            topBar = {
                // Header migliorato come TopAppBar standard
                TopAppBar(
                    title = {
                        Text(
                            text = "SELEZIONA FILTRI",
                            color = colorScheme.onPrimary,
                            style = typography.titleMedium,
                            letterSpacing = 1.sp,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center // Centra il titolo nella TopAppBar
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Indietro",
                                tint = colorScheme.onPrimary
                            )
                        }
                    },
                    // Aggiungiamo un pulsante azioni opzionale (es. Reset)
                    actions = {
                        TextButton(
                            onClick = {
                                // Reset di tutti i filtri
                                selectedPurchaseType = null
                                selectedBathrooms = null
                                selectedCondition = null
                                minPrice = ""
                                maxPrice = ""
                                minSurface = ""
                                maxSurface = ""
                                minRooms = ""
                                maxRooms = ""
                            },
                            colors = ButtonDefaults.textButtonColors(contentColor = colorScheme.onPrimary)
                        ) {
                            Text(
                                text = "Reset",
                                style = typography.labelLarge // Usiamo labelLarge per coerenza con i pulsanti
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colorScheme.primary // Sfondo Primary
                    )
                )
            },
            bottomBar = {
                // Pulsante Cerca fisso in fondo
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colorScheme.background) // Sfondo per la bottom bar
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            // TODO: Applica i filtri selezionati e naviga ai risultati
                            // Esempio: Naviga alla schermata dei risultati con i filtri
                            // navController.navigate("apartmentListing/${idUtente}?comune=${ricerca}&type=${selectedPurchaseType ?: ""}&minPrice=${minPrice}&maxPrice=${maxPrice}...")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 8.dp,
                            pressedElevation = 4.dp
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Cerca",
                                tint = colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Mostra risultati", // Testo più descrittivo
                                color = colorScheme.onPrimary,
                                style = typography.labelLarge
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            // Contenuto scrollabile dei filtri
            Column(
                modifier = Modifier
                    .padding(paddingValues) // Applica il padding dello Scaffold
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(colorScheme.background) // Sfondo per il contenuto
                    .padding(horizontal = 16.dp) // Padding orizzontale per i filtri
            ) {
                Spacer(modifier = Modifier.height(16.dp)) // Spazio sotto la TopAppBar

                // Pulsanti Compra/Affitta migliorati
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp)) // Contenitore con angoli arrotondati
                        .background(colorScheme.surface) // Sfondo chiaro per il contenitore
                        .padding(4.dp), // Padding interno
                    horizontalArrangement = Arrangement.spacedBy(4.dp), // Spazio tra i pulsanti
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("Compra", "Affitta").forEach { option ->
                        val selected = option == selectedPurchaseType
                        Button(
                            onClick = {
                                selectedPurchaseType = if (selected) null else option
                            },
                            modifier = Modifier
                                .weight(1f) // Occupano lo spazio disponibile equamente
                                .height(40.dp), // Altezza fissa
                            shape = RoundedCornerShape(6.dp), // Angoli leggermente meno arrotondati per i pulsanti interni
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) colorScheme.primary else Color.Transparent, // Sfondo primary se selezionato, trasparente altrimenti
                                contentColor = if (selected) colorScheme.onPrimary else colorScheme.onSurface // Testo onPrimary se selezionato, onSurface altrimenti
                            ),
                            elevation = ButtonDefaults.buttonElevation(
                                defaultElevation = 0.dp, // Nessuna ombra predefinita
                                pressedElevation = 0.dp // Nessuna ombra alla pressione
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp) // Padding ridotto
                        ) {
                            Text(
                                text = option,
                                style = typography.labelLarge, // Usiamo labelLarge
                                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal // Grassetto se selezionato
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp)) // Spazio sotto i pulsanti Compra/Affitta

                // Sezione Filtri: Prezzo
                FilterSection(title = "Prezzo", typography = typography, colorScheme = colorScheme) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp) // Spazio tra i campi di input
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = minPrice,
                                onValueChange = { minPrice = it },
                                label = { Text("Minimo") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colorScheme.primary,
                                    unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f),
                                    focusedLabelColor = colorScheme.primary,
                                    unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.6f),
                                    cursorColor = colorScheme.primary
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Riferimento corretto
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = maxPrice,
                                onValueChange = { maxPrice = it },
                                label = { Text("Massimo") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colorScheme.primary,
                                    unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f),
                                    focusedLabelColor = colorScheme.primary,
                                    unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.6f),
                                    cursorColor = colorScheme.primary
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Riferimento corretto
                            )
                        }
                    }
                }

                // Sezione Filtri: Superficie
                FilterSection(title = "Superficie (mq)", typography = typography, colorScheme = colorScheme) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = minSurface,
                                onValueChange = { minSurface = it },
                                label = { Text("Minimo") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colorScheme.primary,
                                    unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f),
                                    focusedLabelColor = colorScheme.primary,
                                    unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.6f),
                                    cursorColor = colorScheme.primary
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Riferimento corretto
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = maxSurface,
                                onValueChange = { maxSurface = it },
                                label = { Text("Massimo") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colorScheme.primary,
                                    unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f),
                                    focusedLabelColor = colorScheme.primary,
                                    unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.6f),
                                    cursorColor = colorScheme.primary
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Riferimento corretto
                            )
                        }
                    }
                }

                // Sezione Filtri: Locali
                FilterSection(title = "Locali", typography = typography, colorScheme = colorScheme) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = minRooms,
                                onValueChange = { minRooms = it },
                                label = { Text("Minimo") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colorScheme.primary,
                                    unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f),
                                    focusedLabelColor = colorScheme.primary,
                                    unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.6f),
                                    cursorColor = colorScheme.primary
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Riferimento corretto
                            )
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = maxRooms,
                                onValueChange = { maxRooms = it },
                                label = { Text("Massimo") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = colorScheme.primary,
                                    unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.3f),
                                    focusedLabelColor = colorScheme.primary,
                                    unfocusedLabelColor = colorScheme.onSurface.copy(alpha = 0.6f),
                                    cursorColor = colorScheme.primary
                                ),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // Riferimento corretto
                            )
                        }
                    }
                }

                // Sezione Filtri: Bagni
                FilterSection(title = "Bagni", typography = typography, colorScheme = colorScheme) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp) // Spazio ridotto per più elementi
                    ) {
                        listOf(1, 2, 3).forEach { count ->
                            val isSelected = selectedBathrooms == count
                            FilterOptionButton(
                                text = count.toString(),
                                isSelected = isSelected,
                                onClick = {
                                    selectedBathrooms = if (isSelected) null else count
                                },
                                colorScheme = colorScheme,
                                typography = typography
                            )
                        }
                        // Pulsante ">3" separato
                        val isSelectedMoreThan3 = selectedBathrooms != null && selectedBathrooms!! > 3
                        FilterOptionButton(
                            text = ">3",
                            isSelected = isSelectedMoreThan3,
                            onClick = {
                                selectedBathrooms = if (isSelectedMoreThan3) null else 4 // Usiamo un valore arbitrario > 3 per rappresentare ">3"
                            },
                            colorScheme = colorScheme,
                            typography = typography
                        )
                    }
                }

                // Sezione Filtri: Stato immobile
                FilterSection(title = "Stato immobile", typography = typography, colorScheme = colorScheme) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterOptionButton(
                                text = "Nuovo",
                                isSelected = selectedCondition == "Nuovo",
                                onClick = { selectedCondition = if (selectedCondition == "Nuovo") null else "Nuovo" },
                                colorScheme = colorScheme,
                                typography = typography
                            )
                            FilterOptionButton(
                                text = "Ottimo",
                                isSelected = selectedCondition == "Ottimo",
                                onClick = { selectedCondition = if (selectedCondition == "Ottimo") null else "Ottimo" },
                                colorScheme = colorScheme,
                                typography = typography
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterOptionButton(
                                text = "Buono",
                                isSelected = selectedCondition == "Buono",
                                onClick = { selectedCondition = if (selectedCondition == "Buono") null else "Buono" },
                                colorScheme = colorScheme,
                                typography = typography
                            )
                            FilterOptionButton(
                                text = "Da ristrutturare",
                                isSelected = selectedCondition == "Da ristrutturare",
                                onClick = { selectedCondition = if (selectedCondition == "Da ristrutturare") null else "Da ristrutturare" },
                                colorScheme = colorScheme,
                                typography = typography
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // Spazio aggiuntivo
            }
        }
    }
}

@Composable
fun FilterSection(
    title: String,
    typography: Typography,
    colorScheme: ColorScheme,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = typography.titleSmall.copy(fontWeight = FontWeight.SemiBold), // Titolo sezione più definito
            color = colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

// Composable riutilizzabile per i pulsanti delle opzioni di filtro (Bagni, Stato immobile)
@Composable
fun FilterOptionButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .weight(1f), // Occupano lo spazio disponibile equamente
        shape = RoundedCornerShape(8.dp), // Angoli leggermente arrotondati
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) colorScheme.primary else Color.Transparent, // Sfondo primary se selezionato, trasparente altrimenti
            contentColor = if (isSelected) colorScheme.onPrimary else colorScheme.onSurface // Testo onPrimary se selezionato, onSurface altrimenti
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) colorScheme.primary else colorScheme.onSurface.copy(alpha = 0.5f) // Bordo primary se selezionato, onSurface altrimenti
        ),
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp) // Padding interno
    ) {
        Text(
            text = text,
            style = typography.labelLarge, // Usiamo labelLarge
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal, // Grassetto se selezionato
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth() // Assicura che il testo occupi lo spazio disponibile
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewFilterScreen() {
    val navController = rememberNavController()
    SearchFilterScreen(
        navController = navController,
        idUtente = "utente",
        ricerca = "varcaturo"
    )
}
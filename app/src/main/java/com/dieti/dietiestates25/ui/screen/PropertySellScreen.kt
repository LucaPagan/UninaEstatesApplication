@file:Suppress("DEPRECATION")

package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.layout.statusBarsPadding

@Composable
fun PropertySellScreen(navController: NavController, idUtente: String) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography
        val scrollState = rememberScrollState()

        val focusManager = LocalFocusManager.current

        // Stato per tutti i campi del form
        var propertyType by remember { mutableStateOf("Vendita") }
        var category by remember { mutableStateOf("") }
        var propertySubType by remember { mutableStateOf("") }
        var location by remember { mutableStateOf("") }
        var squareMeters by remember { mutableStateOf("") }
        var floor by remember { mutableStateOf("") }
        var price by remember { mutableStateOf("") }
        var condoFees by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var rooms by remember { mutableIntStateOf(0) }
        var bedrooms by remember { mutableIntStateOf(0) }
        var kitchens by remember { mutableIntStateOf(0) }
        var bathrooms by remember { mutableIntStateOf(0) }
        var hasBasement by remember { mutableStateOf(false) }
        var hasGarage by remember { mutableStateOf(false) }
        var hasTerrace by remember { mutableStateOf(false) }
        var furnishingState by remember { mutableStateOf("") }
        var airConditioningType by remember { mutableStateOf("") }
        var exposure by remember { mutableStateOf("") }
        var ownershipType by remember { mutableStateOf("") }
        var propertyCondition by remember { mutableStateOf("") }
        var constructionYear by remember { mutableStateOf("") }
        var availability by remember { mutableStateOf("") }

        // Lista delle immagini (in una app reale si gestirebbe in modo diverso)
        var imageCount by remember { mutableIntStateOf(0) }

        val requiredFieldMark = buildAnnotatedString {
            append("*")
            withStyle(style = SpanStyle(color = Color.Red)) {
                append(" ")
            }
        }

        // Gradient background
        val gradientColors = arrayOf(
            0.0f to colorScheme.primary,
            0.15f to colorScheme.background,
            1.0f to colorScheme.background
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colorStops = gradientColors))
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        focusManager.clearFocus()
                    })
                }
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Header
                HeaderBar(
                    navController = navController,
                    colorScheme = colorScheme,
                    typography = typography,
                    title = "Inserimento Proprietà"
                )

                // Form content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Sezione tipo di annuncio (Vendita/Affitto)
                    Text(
                        text = "Tipo di annuncio $requiredFieldMark",
                        style = typography.titleMedium,
                        color = colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        SegmentedButton(
                            text = "Vendita",
                            selected = propertyType == "Vendita",
                            onClick = { propertyType = "Vendita" },
                            modifier = Modifier.weight(1f),
                            colorScheme = colorScheme
                        )

                        SegmentedButton(
                            text = "Affitto",
                            selected = propertyType == "Affitto",
                            onClick = { propertyType = "Affitto" },
                            modifier = Modifier.weight(1f),
                            colorScheme = colorScheme
                        )
                    }

                    Divider()

                    // Categoria e tipologia
                    Text(
                        text = "Categoria e tipologia $requiredFieldMark",
                        style = typography.titleMedium,
                        color = colorScheme.primary
                    )

                    DropdownMenuField(
                        label = "Categoria",
                        value = category,
                        onValueChange = { category = it },
                        options = listOf("Residenziale", "Commerciale", "Industriale", "Terreno"),
                        colorScheme = colorScheme,
                        typography = typography,
                        required = true
                    )

                    DropdownMenuField(
                        label = "Tipologia",
                        value = propertySubType,
                        onValueChange = { propertySubType = it },
                        options = listOf("Appartamento", "Attico", "Villa", "Casa indipendente", "Villetta a schiera", "Loft"),
                        colorScheme = colorScheme,
                        typography = typography,
                        required = true
                    )

                    Divider()

                    // Localizzazione
                    Text(
                        text = "Localizzazione $requiredFieldMark",
                        style = typography.titleMedium,
                        color = colorScheme.primary
                    )

                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Indirizzo completo") },
                        placeholder = { Text("Via, numero civico, città, CAP") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colorScheme.onBackground.copy(alpha = 0.2f),
                        ),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Indirizzo",
                                tint = colorScheme.primary
                            )
                        }
                    )

                    Divider()

                    // Caratteristiche principali
                    // Modifica completa della sezione "Caratteristiche principali"
                    Text(
                        text = "Caratteristiche principali $requiredFieldMark",
                        style = typography.titleMedium,
                        color = colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = squareMeters,
                            onValueChange = { squareMeters = it },
                            label = { Text("Metri quadri") },
                            modifier = Modifier.weight(1.2f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.onBackground.copy(alpha = 0.2f),
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            trailingIcon = {
                                Text(
                                    text = "mq",
                                    style = typography.bodyMedium,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }
                        )

                        OutlinedTextField(
                            value = floor,
                            onValueChange = { floor = it },
                            label = { Text("Piano") },
                            modifier = Modifier.weight(0.8f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.onBackground.copy(alpha = 0.2f),
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }

                    Divider()

                    // Locali
                    Text(
                        text = "Locali $requiredFieldMark",
                        style = typography.titleMedium,
                        color = colorScheme.primary
                    )

                    RoomCounter(
                        icon = Icons.Default.Hotel,
                        title = "Camere da letto",
                        count = bedrooms,
                        onIncrement = { bedrooms++ },
                        onDecrement = { if (bedrooms > 0) bedrooms-- },
                        colorScheme = colorScheme,
                        typography = typography
                    )

                    RoomCounter(
                        icon = Icons.Default.Kitchen,
                        title = "Cucine",
                        count = kitchens,
                        onIncrement = { kitchens++ },
                        onDecrement = { if (kitchens > 0) kitchens-- },
                        colorScheme = colorScheme,
                        typography = typography
                    )

                    RoomCounter(
                        icon = Icons.Default.Bathroom,
                        title = "Bagni",
                        count = bathrooms,
                        onIncrement = { bathrooms++ },
                        onDecrement = { if (bathrooms > 0) bathrooms-- },
                        colorScheme = colorScheme,
                        typography = typography
                    )

                    Text(
                        text = "Totale locali: ${bedrooms + kitchens + bathrooms}",
                        style = typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )

                    Divider()

                    // Caratteristiche aggiuntive
                    Text(
                        text = "Caratteristiche aggiuntive",
                        style = typography.titleMedium,
                        color = colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            CheckboxField(
                                text = "Cantina",
                                checked = hasBasement,
                                onCheckedChange = { hasBasement = it },
                                colorScheme = colorScheme,
                                typography = typography
                            )

                            CheckboxField(
                                text = "Box/Garage",
                                checked = hasGarage,
                                onCheckedChange = { hasGarage = it },
                                colorScheme = colorScheme,
                                typography = typography
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            CheckboxField(
                                text = "Terrazzo",
                                checked = hasTerrace,
                                onCheckedChange = { hasTerrace = it },
                                colorScheme = colorScheme,
                                typography = typography
                            )
                        }
                    }

                    Divider()

                    // Dettagli proprietà
                    Text(
                        text = "Dettagli proprietà",
                        style = typography.titleMedium,
                        color = colorScheme.primary
                    )

                    DropdownMenuField(
                        label = "Arredamento",
                        value = furnishingState,
                        onValueChange = { furnishingState = it },
                        options = listOf("Non arredato", "Parzialmente arredato", "Arredato"),
                        colorScheme = colorScheme,
                        typography = typography
                    )

                    DropdownMenuField(
                        label = "Climatizzazione",
                        value = airConditioningType,
                        onValueChange = { airConditioningType = it },
                        options = listOf("Nessuna", "Aria condizionata", "Climatizzatore", "Pompa di calore"),
                        colorScheme = colorScheme,
                        typography = typography
                    )

                    DropdownMenuField(
                        label = "Esposizione",
                        value = exposure,
                        onValueChange = { exposure = it },
                        options = listOf("Nord", "Sud", "Est", "Ovest", "Nord-Est", "Nord-Ovest", "Sud-Est", "Sud-Ovest"),
                        colorScheme = colorScheme,
                        typography = typography
                    )

                    Divider()

                    // Informazioni legali
                    Text(
                        text = "Informazioni legali",
                        style = typography.titleMedium,
                        color = colorScheme.primary
                    )

                    DropdownMenuField(
                        label = "Tipo di proprietà",
                        value = ownershipType,
                        onValueChange = { ownershipType = it },
                        options = listOf("Piena proprietà", "Nuda proprietà", "Usufrutto", "Multiproprietà"),
                        colorScheme = colorScheme,
                        typography = typography
                    )

                    DropdownMenuField(
                        label = "Stato proprietà",
                        value = propertyCondition,
                        onValueChange = { propertyCondition = it },
                        options = listOf("Nuovo", "Ristrutturato", "Da ristrutturare", "Buono stato", "Ottimo stato"),
                        colorScheme = colorScheme,
                        typography = typography,
                        required = true
                    )

                    OutlinedTextField(
                        value = constructionYear,
                        onValueChange = { constructionYear = it },
                        label = { Text("Anno di costruzione") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colorScheme.onBackground.copy(alpha = 0.2f),
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    DropdownMenuField(
                        label = "Disponibilità",
                        value = availability,
                        onValueChange = { availability = it },
                        options = listOf("Libero", "Occupato", "Libero al rogito"),
                        colorScheme = colorScheme,
                        typography = typography
                    )

                    Divider()

                    // Informazioni economiche
                    Text(
                        text = "Informazioni economiche $requiredFieldMark",
                        style = typography.titleMedium,
                        color = colorScheme.primary
                    )

                    OutlinedTextField(
                        value = price,
                        onValueChange = { price = it },
                        label = { Text("Prezzo") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colorScheme.onBackground.copy(alpha = 0.2f),
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Euro,
                                contentDescription = "Prezzo",
                                tint = colorScheme.primary
                            )
                        }
                    )

                    OutlinedTextField(
                        value = condoFees,
                        onValueChange = { condoFees = it },
                        label = { Text("Spese condominiali €/mese") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colorScheme.onBackground.copy(alpha = 0.2f),
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Divider()

                    // Descrizione
                    Text(
                        text = "Descrizione $requiredFieldMark",
                        style = typography.titleMedium,
                        color = colorScheme.primary
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descrizione dettagliata") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colorScheme.onBackground.copy(alpha = 0.2f),
                        ),
                        maxLines = 6
                    )

                    Divider()

                    // Immagini
                    Text(
                        text = "Immagini $requiredFieldMark",
                        style = typography.titleMedium,
                        color = colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Mostra le immagini selezionate (per demo mostra solo il numero)
                        if (imageCount > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(colorScheme.primary.copy(alpha = 0.1f))
                                    .border(
                                        width = 1.dp,
                                        color = colorScheme.primary,
                                        shape = RoundedCornerShape(12.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$imageCount immagini selezionate",
                                    style = typography.bodyMedium,
                                    color = colorScheme.primary
                                )
                            }
                        }

                        // Bottone per aggiungere immagini
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(colorScheme.primary.copy(alpha = 0.1f))
                                .border(
                                    width = 1.dp,
                                    color = colorScheme.primary,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { imageCount++ },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Aggiungi immagini",
                                    tint = colorScheme.primary
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Aggiungi",
                                    style = typography.bodySmall,
                                    color = colorScheme.primary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bottone di invio form
                    Button(
                        onClick = {
                            // Logica di invio form - qui andrebbe implementata la validazione
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Text("PUBBLICA ANNUNCIO")
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun HeaderBar(
    navController: NavController,
    colorScheme: ColorScheme,
    typography: Typography,
    title: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        color = colorScheme.primary,
        shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro",
                    tint = colorScheme.onPrimary
                )
            }

            Text(
                text = title,
                color = colorScheme.onPrimary,
                style = typography.titleMedium,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun SegmentedButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme
) {
    Box(
        modifier = modifier
            .height(45.dp)
            .border(
                width = 1.dp,
                color = if (selected) colorScheme.primary else colorScheme.onBackground.copy(alpha = 0.2f),
                shape = RoundedCornerShape(25.dp)
            )
            .background(
                color = if (selected) colorScheme.primary else Color.Transparent,
                shape = RoundedCornerShape(25.dp)
            )
            .clip(RoundedCornerShape(25.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (selected) colorScheme.onPrimary else colorScheme.onBackground,
            textAlign = TextAlign.Center,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    colorScheme: ColorScheme,
    typography: Typography,
    required: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    val displayLabel = if (required) {
        "$label *"
    } else {
        label
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            label = { Text(displayLabel) },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorScheme.primary,
                unfocusedBorderColor = colorScheme.onBackground.copy(alpha = 0.2f),
            ),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(colorScheme.background)
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onValueChange(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}

@Composable
fun RoomCounter(
    icon: ImageVector,
    title: String,
    count: Int,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = title,
            style = typography.bodyMedium,
            modifier = Modifier.weight(1f),
            color = colorScheme.onBackground
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = onDecrement,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Diminuisci",
                    tint = colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }

            Text(
                text = "$count",
                style = typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(24.dp),
                textAlign = TextAlign.Center,
                color = colorScheme.onBackground

            )

            IconButton(
                onClick = onIncrement,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Aumenta",
                    tint = colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun CheckboxField(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onCheckedChange(it) },
            colors = CheckboxDefaults.colors(
                checkedColor = colorScheme.primary,
                uncheckedColor = colorScheme.onBackground.copy(alpha = 0.5f)
            )
        )

        Text(
            text = text,
            style = typography.bodyMedium,
            color = colorScheme.onBackground
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPropertySellScreen() {
    val navController = rememberNavController()
    PropertySellScreen(navController = navController, idUtente = "Danilo")
}
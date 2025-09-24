package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.platform.LocalFocusManager
import com.dieti.dietiestates25.ui.components.AppSecondaryButton
import com.dieti.dietiestates25.ui.components.CheckboxField
import com.dieti.dietiestates25.ui.components.DropdownMenuField
import com.dieti.dietiestates25.ui.components.FormSection
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
import com.dieti.dietiestates25.ui.components.RoomCounter
import com.dieti.dietiestates25.ui.components.SegmentedButton
import com.dieti.dietiestates25.ui.theme.Dimensions

@Composable
fun PropertySellScreen(navController: NavController, idUtente: String) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions
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
    var imageCount by remember { mutableIntStateOf(0) }

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
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
        ) {
            // Top Header
            GeneralHeaderBar(
                title = "Inserimento Proprietà",
                onBackClick = { navController.popBackStack() }
            )

            // Form content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(dimensions.paddingMedium),
                verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
            ) {
                FormSection(title = "Tipo di annuncio", isRequired = true) {
                    Row(horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)) {
                        SegmentedButton(
                            text = "Vendita",
                            selected = propertyType == "Vendita",
                            onClick = { propertyType = "Vendita" },
                            modifier = Modifier.weight(1f)
                        )
                        SegmentedButton(
                            text = "Affitto",
                            selected = propertyType == "Affitto",
                            onClick = { propertyType = "Affitto" },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                FormSection(title = "Categoria e tipologia", isRequired = true) {
                    DropdownMenuField(
                        label = "Categoria",
                        value = category,
                        onValueChange = { category = it },
                        options = listOf("Residenziale", "Commerciale", "Industriale", "Terreno"),
                        required = true
                    )
                    DropdownMenuField(
                        label = "Tipologia",
                        value = propertySubType,
                        onValueChange = { propertySubType = it },
                        options = listOf("Appartamento", "Villa", "Loft"),
                        required = true
                    )
                }

                FormSection(title = "Localizzazione", isRequired = true) {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Indirizzo completo") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.LocationOn, "Indirizzo") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colorScheme.onBackground.copy(alpha = 0.2f),
                        )
                    )
                }

                FormSection(title = "Caratteristiche principali", isRequired = true) {
                    Row(horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)) {
                        OutlinedTextField(
                            value = squareMeters,
                            onValueChange = { squareMeters = it },
                            label = { Text("Metri quadri") },
                            modifier = Modifier.weight(1.2f),
                            trailingIcon = { Text("mq") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.onBackground.copy(alpha = 0.2f),
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = floor,
                            onValueChange = { floor = it },
                            label = { Text("Piano") },
                            modifier = Modifier.weight(0.8f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.onBackground.copy(alpha = 0.2f),
                            )
                        )
                    }
                }

                FormSection(title = "Locali", isRequired = true) {
                    RoomCounter(
                        icon = Icons.Default.Hotel,
                        title = "Camere da letto",
                        count = bedrooms,
                        onIncrement = { bedrooms++ },
                        onDecrement = { if (bedrooms > 0) bedrooms-- }
                    )
                    RoomCounter(
                        icon = Icons.Default.Kitchen,
                        title = "Cucine",
                        count = kitchens,
                        onIncrement = { kitchens++ },
                        onDecrement = { if (kitchens > 0) kitchens-- }
                    )
                    RoomCounter(
                        icon = Icons.Default.Bathroom,
                        title = "Bagni",
                        count = bathrooms,
                        onIncrement = { bathrooms++ },
                        onDecrement = { if (bathrooms > 0) bathrooms-- }
                    )
                    Text(
                        text = "Totale locali: ${bedrooms + kitchens + bathrooms}",
                        style = typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = colorScheme.primary
                    )
                }

                FormSection(title = "Caratteristiche aggiuntive") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            CheckboxField(
                                text = "Cantina",
                                checked = hasBasement,
                                onCheckedChange = { hasBasement = it }
                            )
                            CheckboxField(
                                text = "Box/Garage",
                                checked = hasGarage,
                                onCheckedChange = { hasGarage = it }
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            CheckboxField(
                                text = "Terrazzo",
                                checked = hasTerrace,
                                onCheckedChange = { hasTerrace = it }
                            )
                        }
                    }
                }

                FormSection(title = "Dettagli proprietà") {
                    DropdownMenuField(
                        label = "Arredamento",
                        value = furnishingState,
                        onValueChange = { furnishingState = it },
                        options = listOf("Non arredato", "Parzialmente arredato", "Arredato")
                    )

                    DropdownMenuField(
                        label = "Climatizzazione",
                        value = airConditioningType,
                        onValueChange = { airConditioningType = it },
                        options = listOf(
                            "Nessuna",
                            "Aria condizionata",
                            "Climatizzatore",
                            "Pompa di calore"
                        )
                    )

                    DropdownMenuField(
                        label = "Esposizione",
                        value = exposure,
                        onValueChange = { exposure = it },
                        options = listOf(
                            "Nord", "Sud", "Est", "Ovest",
                            "Nord-Est", "Nord-Ovest", "Sud-Est", "Sud-Ovest"
                        )
                    )
                }

                FormSection(title = "Informazioni legali") {
                    DropdownMenuField(
                        label = "Tipo di proprietà",
                        value = ownershipType,
                        onValueChange = { ownershipType = it },
                        options = listOf(
                            "Piena proprietà",
                            "Nuda proprietà",
                            "Usufrutto",
                            "Multiproprietà"
                        )
                    )

                    DropdownMenuField(
                        label = "Stato proprietà",
                        value = propertyCondition,
                        onValueChange = { propertyCondition = it },
                        options = listOf(
                            "Nuovo",
                            "Ristrutturato",
                            "Da ristrutturare",
                            "Buono stato",
                            "Ottimo stato"
                        ),
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
                        options = listOf("Libero", "Occupato", "Libero al rogito")
                    )
                }

                FormSection(title = "Informazioni economiche", isRequired = true) {
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
                }

                FormSection(title = "Descrizione", isRequired = true) {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descrizione dettagliata") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(dimensions.infoCardHeight),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = colorScheme.primary,
                            unfocusedBorderColor = colorScheme.onBackground.copy(alpha = 0.2f),
                        ),
                        maxLines = 6
                    )
                }

                FormSection(title = "Immagini", isRequired = true) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
                    ) {
                        // Mostra le immagini selezionate
                        if (imageCount > 0) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(dimensions.imagePrewiev)
                                    .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                                    .background(colorScheme.primary.copy(alpha = 0.1f))
                                    .border(
                                        width = dimensions.borderStrokeSmall,
                                        color = colorScheme.primary,
                                        shape = RoundedCornerShape(dimensions.cornerRadiusMedium)
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
                                .size(dimensions.imagePrewiev)
                                .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
                                .background(colorScheme.primary.copy(alpha = 0.1f))
                                .border(
                                    width = dimensions.borderStrokeSmall,
                                    color = colorScheme.primary,
                                    shape = RoundedCornerShape(dimensions.cornerRadiusMedium)
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

                                Spacer(modifier = Modifier.height(dimensions.spacingExtraSmall))

                                Text(
                                    text = "Aggiungi",
                                    style = typography.bodySmall,
                                    color = colorScheme.primary
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(dimensions.spacingMedium))

                AppSecondaryButton(
                    text = "Pubblica Annuncio",
                    onClick = { /* TODO: Implementare logica di pubblicazione */ },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPropertySellScreen() {
    val navController = rememberNavController()
    PropertySellScreen(navController = navController, idUtente = "Danilo")
}
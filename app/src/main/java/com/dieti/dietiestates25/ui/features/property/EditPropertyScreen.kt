package com.dieti.dietiestates25.ui.features.property

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bathtub
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.KingBed
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.SquareFoot
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.ui.components.AppOutlinedTextField
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppRedButton
import com.dieti.dietiestates25.ui.components.CircularIconActionButton
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
import com.dieti.dietiestates25.ui.theme.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPropertyScreen(
    navController: NavController,
    immobileId: String, // ID passato dalla navigazione
    viewModel: EditPropertyViewModel = viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions
    val focusManager = LocalFocusManager.current

    val uiState by viewModel.uiState.collectAsState()

    // Carica i dati quando lo schermo si apre
    LaunchedEffect(immobileId) {
        viewModel.loadProperty(immobileId)
    }

    // Gestione navigazione post successo
    LaunchedEffect(uiState) {
        if (uiState is EditUiState.SuccessOperation) {
            navController.popBackStack() // Torna indietro dopo successo
        }
    }

    Scaffold(
        topBar = {
            GeneralHeaderBar(
                title = "Modifica Immobile",
                onBackClick = { navController.popBackStack() },
                actions = {}
            )
        }
    ) { innerPadding ->

        // Sfondo Gradiente
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(colorScheme.primary, colorScheme.background)
                    )
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { focusManager.clearFocus() }
                )
        ) {
            when(val state = uiState) {
                is EditUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = colorScheme.onPrimary
                    )
                }
                is EditUiState.Error -> {
                    Text(
                        text = state.msg,
                        color = colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is EditUiState.Content -> {
                    // Carichiamo il contenuto nel form
                    EditPropertyContent(
                        immobile = state.immobile,
                        navController = navController,
                        viewModel = viewModel,
                        dimensions = dimensions,
                        colorScheme = colorScheme,
                        typography = typography
                    )
                }
                else -> {} // Success gestito da LaunchedEffect
            }
        }
    }
}

@Composable
fun EditPropertyContent(
    immobile: ImmobileDTO,
    navController: NavController,
    viewModel: EditPropertyViewModel,
    dimensions: Dimensions,
    colorScheme: ColorScheme,
    typography: Typography
) {
    // --- STATI LOCALI MUTABILI PER IL FORM ---
    var isEditing by remember { mutableStateOf(false) }
    var showUnsavedChangesDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Inizializza i valori con i dati dal server
    // Estrazione conteggi ambienti
    val initialBeds = immobile.ambienti.firstOrNull { it.tipologia == "Letto" }?.numero ?: 0
    val initialBaths = immobile.ambienti.firstOrNull { it.tipologia == "Bagno" }?.numero ?: 0
    val initialRooms = immobile.ambienti.firstOrNull { it.tipologia == "Vani" }?.numero ?: 0

    var currentPrice by remember { mutableStateOf(immobile.prezzo?.toString() ?: "") }
    var currentMq by remember { mutableStateOf(immobile.mq?.toString() ?: "") }
    var currentDesc by remember { mutableStateOf(immobile.descrizione ?: "") }
    var currentBeds by remember { mutableStateOf(initialBeds) }
    var currentBaths by remember { mutableStateOf(initialBaths) }
    var currentRooms by remember { mutableStateOf(initialRooms) }

    // Per ora le immagini sono in sola lettura nella UI (complesso gestirle in edit via Multipart)
    val currentImages = remember { listOf(R.drawable.property1) } // Placeholder o logica Glide

    // Verifica Modifiche
    val isModified = currentPrice != (immobile.prezzo?.toString() ?: "") ||
            currentMq != (immobile.mq?.toString() ?: "") ||
            currentDesc != (immobile.descrizione ?: "") ||
            currentBeds != initialBeds ||
            currentBaths != initialBaths ||
            currentRooms != initialRooms

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = dimensions.paddingExtraLarge),
        verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
    ) {
        // --- HEADER BAR PERSONALIZZATA IN COLONNA ---
        item {
            EditPropertyTopBarActions(
                isEditing = isEditing,
                onEditToggle = {
                    if (isEditing && isModified) showUnsavedChangesDialog = true
                    else isEditing = !isEditing
                },
                colorScheme = colorScheme,
                dimensions = dimensions
            )
        }

        item {
            Column(
                modifier = Modifier.padding(horizontal = dimensions.paddingMedium),
                verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
            ) {

                // Immagini (Statico per ora)
                EditInfoCard(title = "Immagini") {
                    EditableImageSection(
                        images = currentImages,
                        onAddImage = {},
                        onRemoveImage = {},
                        isEditing = false, // Disabilitato upload immagini in questa versione
                        dimensions = dimensions
                    )
                    if(isEditing) {
                        Text("Modifica foto non disponibile in questa versione", style = typography.bodySmall, color = colorScheme.error)
                    }
                }

                // Prezzo
                EditInfoCard(title = "Prezzo") {
                    AppOutlinedTextField(
                        value = currentPrice,
                        onValueChange = { if (it.all { char -> char.isDigit() }) currentPrice = it },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = isEditing,
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = null,
                        leadingIcon = { Icon(Icons.Filled.Euro, null, tint = colorScheme.primary) }
                    )
                }

                // Dettagli Numerici
                EditInfoCard(title = "Dettagli Principali") {
                    Column(verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            AttributeCounter("Letti", Icons.Filled.KingBed, currentBeds, { currentBeds = it }, isEditing, dimensions)
                            AttributeCounter("Bagni", Icons.Filled.Bathtub, currentBaths, { currentBaths = it }, isEditing, dimensions)
                            AttributeCounter("Vani", Icons.Filled.MeetingRoom, currentRooms, { currentRooms = it }, isEditing, dimensions)
                        }

                        Text("Superficie", style = typography.titleSmall, modifier = Modifier.padding(top = dimensions.spacingSmall))
                        AppOutlinedTextField(
                            value = currentMq,
                            onValueChange = { if (it.all { char -> char.isDigit() }) currentMq = it },
                            label = null,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isEditing,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            leadingIcon = { Icon(Icons.Default.SquareFoot, null, tint = colorScheme.primary) },
                            suffix = { Text("m²") }
                        )
                    }
                }

                // Descrizione
                EditInfoCard(title = "Descrizione") {
                    AppOutlinedTextField(
                        value = currentDesc,
                        onValueChange = { currentDesc = it },
                        modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = dimensions.infoCardHeight),
                        enabled = isEditing,
                        singleLine = false,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default)
                    )
                }

                // Pulsanti Azione
                if (isEditing) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium),
                        modifier = Modifier.padding(top = dimensions.spacingMedium)
                    ) {
                        AppPrimaryButton(
                            text = "Salva Modifiche",
                            onClick = { showSuccessDialog = true },
                            enabled = isModified && currentPrice.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth()
                        )
                        AppRedButton(
                            text = "Elimina Definitivamente",
                            onClick = { showDeleteConfirmDialog = true },
                            enabled = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    // --- DIALOGS ---
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Conferma Modifica") },
            text = { Text("Vuoi salvare le modifiche apportate all'immobile?") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    viewModel.updateProperty(
                        id = immobile.id,
                        originalDto = immobile,
                        newPrice = currentPrice.toIntOrNull() ?: 0,
                        newMq = currentMq.toIntOrNull() ?: 0,
                        newDesc = currentDesc,
                        newBeds = currentBeds,
                        newBaths = currentBaths,
                        newRooms = currentRooms
                    )
                }) { Text("Salva") }
            },
            dismissButton = {
                TextButton(onClick = { showSuccessDialog = false }) { Text("Annulla") }
            }
        )
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Elimina Immobile") },
            text = { Text("Sei sicuro? Questa azione è irreversibile.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirmDialog = false
                        viewModel.deleteProperty(immobile.id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error)
                ) { Text("Elimina") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) { Text("Annulla") }
            }
        )
    }

    if (showUnsavedChangesDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedChangesDialog = false },
            title = { Text("Modifiche non salvate") },
            text = { Text("Se esci ora perderai le modifiche.") },
            confirmButton = {
                TextButton(onClick = {
                    showUnsavedChangesDialog = false
                    isEditing = false // Reset edit mode
                    // Qui si potrebbero resettare anche le variabili ai valori originali
                }) { Text("Esci senza salvare") }
            },
            dismissButton = {
                TextButton(onClick = { showUnsavedChangesDialog = false }) { Text("Resta") }
            }
        )
    }
}

// Componente helper per il toggle edit nella lista
@Composable
fun EditPropertyTopBarActions(
    isEditing: Boolean,
    onEditToggle: () -> Unit,
    colorScheme: ColorScheme,
    dimensions: Dimensions
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = dimensions.paddingMedium),
        horizontalArrangement = Arrangement.End
    ) {
        CircularIconActionButton(
            onClick = onEditToggle,
            iconVector = if (isEditing) Icons.Default.Close else Icons.Default.Edit,
            contentDescription = if (isEditing) "Annulla" else "Modifica",
            backgroundColor = colorScheme.primaryContainer,
            iconTint = if (isEditing) colorScheme.error else colorScheme.primary,
            iconSize = dimensions.iconSizeMedium
        )
    }
}

// Riusiamo gli helper EditableImageSection, AddImageButton, AttributeCounter, EditInfoCard
// già definiti nel tuo codice precedente (copiali qui o assicurati che siano nello stesso file/package)
@Composable
private fun EditableImageSection(
    images: List<Int>,
    onAddImage: () -> Unit,
    onRemoveImage: (Int) -> Unit,
    isEditing: Boolean,
    dimensions: Dimensions
) {
    val imageSize = dimensions.logoLarge
    LazyRow(
        contentPadding = PaddingValues(vertical = dimensions.paddingSmall),
        horizontalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
    ) {
        items(images.size) { index ->
            Box(modifier = Modifier.size(imageSize)) {
                Image(
                    painter = painterResource(id = images[index]),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(dimensions.cornerRadiusMedium)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
private fun AttributeCounter(
    label: String,
    icon: ImageVector,
    value: Int,
    onValueChange: (Int) -> Unit,
    isEditing: Boolean,
    dimensions: Dimensions
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(dimensions.spacingSmall))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)
        ) {
            IconButton(
                onClick = { onValueChange( (value - 1).coerceAtLeast(0) ) },
                enabled = isEditing && value > 0,
                modifier = Modifier.size(dimensions.iconSizeLarge)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Meno")
            }
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = { onValueChange(value + 1) },
                enabled = isEditing,
                modifier = Modifier.size(dimensions.iconSizeLarge)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Più")
            }
        }
    }
}

@Composable
fun EditInfoCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = Dimensions.spacingSmall),
            color = MaterialTheme.colorScheme.onBackground // Fix contrasto
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimensions.cornerRadiusMedium),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.elevationNone)
        ) {
            Column(modifier = Modifier.padding(Dimensions.paddingMedium)) {
                content()
            }
        }
    }
}
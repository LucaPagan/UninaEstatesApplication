package com.dieti.dietiestates25.ui.screen

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.components.AppOutlinedTextField
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppRedButton
import com.dieti.dietiestates25.ui.components.CircularIconActionButton
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions

// Data class to hold the initial state for comparison
private data class PropertyInitialState(
    val price: String,
    val beds: Int,
    val baths: Int,
    val rooms: Int,
    val area: Int,
    val description: String,
    val images: List<Int>
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPropertyScreen(
    navController: NavController
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions
    val focusManager = LocalFocusManager.current

    // States for holding current values
    var isEditing by remember { mutableStateOf(false) }

    // Dialog visibility states
    var showUnsavedChangesDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val initialData = remember {
        PropertyInitialState(
            price = "129.500",
            beds = 2,
            baths = 1,
            rooms = 3,
            area = 115,
            description = "Scopri questo accogliente appartamento situato nel cuore di Napoli...",
            images = listOf(R.drawable.property1, R.drawable.property2)
        )
    }

    var currentPrice by remember { mutableStateOf(initialData.price) }
    var currentBeds by remember { mutableStateOf(initialData.beds) }
    var currentBaths by remember { mutableStateOf(initialData.baths) }
    var currentRooms by remember { mutableStateOf(initialData.rooms) }
    var currentArea by remember { mutableStateOf(initialData.area) }
    var currentDescription by remember { mutableStateOf(initialData.description) }
    var currentImages by remember { mutableStateOf(initialData.images) }

    // Derived state to check if any value has changed from its initial state
    val isModified by remember(currentPrice, currentBeds, currentBaths, currentRooms, currentArea, currentDescription, currentImages) {
        derivedStateOf {
            currentPrice != initialData.price ||
                    currentBeds != initialData.beds ||
                    currentBaths != initialData.baths ||
                    currentRooms != initialData.rooms ||
                    currentArea != initialData.area ||
                    currentDescription != initialData.description ||
                    currentImages != initialData.images
        }
    }

    val exitEditMode = {
        isEditing = false
        focusManager.clearFocus()
    }

    val revertChangesAndExit = {
        currentPrice = initialData.price
        currentBeds = initialData.beds
        currentBaths = initialData.baths
        currentRooms = initialData.rooms
        currentArea = initialData.area
        currentDescription = initialData.description
        currentImages = initialData.images
        exitEditMode()
    }

    val gradientColors = arrayOf(
        0.0f to colorScheme.primary, 0.10f to colorScheme.background,
        0.70f to colorScheme.background, 1.0f to colorScheme.primary
    )

    Scaffold(
        topBar = {
            GeneralHeaderBar(
                title = "Modifica Immobile",
                onBackClick = { navController.popBackStack() },
                actions = {}
            )
            EditPropertyTopAppBar(
                onBackClick = {
                    if (isEditing && isModified) {
                        showUnsavedChangesDialog = true
                    } else {
                        navController.popBackStack()
                    }
                },
                isEditing = isEditing,
                onEditToggle = {
                    if (isEditing) {
                        if (isModified) {
                            showUnsavedChangesDialog = true
                        } else {
                            exitEditMode()
                        }
                    } else {
                        isEditing = true
                    }
                },
                colorScheme = colorScheme,
                dimensions = dimensions,
                typography = typography
            )
        }
    ) { innerPadding ->
        // The root layout that clears focus when tapped
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Brush.verticalGradient(colorStops = gradientColors))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null, // No ripple effect
                    onClick = { focusManager.clearFocus() } // Clear focus on click
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = dimensions.paddingExtraLarge),
                verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
            ) {
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = dimensions.paddingMedium),
                        verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
                    ) {
                        Spacer(modifier = Modifier.height(dimensions.spacingSmall))

                        EditInfoCard(title = "Immagini") {
                            EditableImageSection(
                                images = currentImages,
                                onAddImage = { /* TODO: Logic to add image */ },
                                onRemoveImage = { index ->
                                    currentImages = currentImages.toMutableList().also { it.removeAt(index) }
                                },
                                isEditing = isEditing,
                                dimensions = dimensions
                            )
                        }

                        // Price Card
                        EditInfoCard(title = "Prezzo") {
                            AppOutlinedTextField(
                                value = currentPrice,
                                onValueChange = { currentPrice = it },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = isEditing,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                label = null,
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Euro,
                                        contentDescription = "Prezzo in Euro",
                                        tint = colorScheme.primary
                                    )
                                }
                            )
                        }

                        // Main Details Card (Counters and Area)
                        EditInfoCard(title = "Dettagli Principali") {
                            Column(verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    AttributeCounter("Letti", Icons.Filled.KingBed, currentBeds, { currentBeds = it }, isEditing, dimensions)
                                    AttributeCounter("Bagni", Icons.Filled.Bathtub, currentBaths, { currentBaths = it }, isEditing, dimensions)
                                    AttributeCounter("Locali", Icons.Filled.MeetingRoom, currentRooms, { currentRooms = it }, isEditing, dimensions)
                                }

                                Text(
                                    text = "Area",
                                    style = typography.titleSmall,
                                    modifier = Modifier.padding(top = dimensions.spacingSmall)
                                )
                                AppOutlinedTextField(
                                    value = if (currentArea > 0) currentArea.toString() else "",
                                    onValueChange = {
                                        val newText = it.filter { char -> char.isDigit() }
                                        currentArea = newText.toIntOrNull() ?: 0
                                    },
                                    label = null,
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = isEditing,
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.SquareFoot,
                                            contentDescription = "Area in metri quadri",
                                            tint = colorScheme.primary
                                        )
                                    },
                                    suffix = { Text("m²") }
                                )
                            }
                        }

                        // Description Card
                        EditInfoCard(title = "Descrizione") {
                            if (isEditing) {
                                AppOutlinedTextField(
                                    value = currentDescription,
                                    onValueChange = { currentDescription = it },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .defaultMinSize(minHeight = dimensions.infoCardHeight),
                                    enabled = true,
                                    singleLine = false,
                                    // --- MODIFICA TASTIERA ---
                                    // Imposta ImeAction a None per favorire il tasto "Invio" per una nuova riga
                                    keyboardOptions = KeyboardOptions.Default.copy(
                                        imeAction = ImeAction.None
                                    ),
                                    // Rimuovi keyboardActions custom, non più necessarie
                                    keyboardActions = KeyboardActions.Default
                                    // --- FINE MODIFICA ---
                                )
                            } else {
                                // When not editing, show plain text
                                Text(
                                    text = currentDescription,
                                    style = typography.bodyLarge,
                                    color = colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = dimensions.paddingSmall)
                                )
                            }
                        }

                        // Action Buttons only shown in edit mode
                        if (isEditing) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium),
                                modifier = Modifier.padding(top = dimensions.spacingMedium)
                            ) {
                                AppPrimaryButton(
                                    text = "Richiesta Modifica",
                                    onClick = { showSuccessDialog = true },
                                    enabled = isModified,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                AppRedButton(
                                    text = "Cancella Immobile",
                                    onClick = { showDeleteConfirmDialog = true },
                                    enabled = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- DIALOGS ---
        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = { showSuccessDialog = false },
                title = { Text("Richiesta Inviata") },
                text = { Text("La tua richiesta di modifica è stata inviata con successo.") },
                confirmButton = {
                    TextButton(onClick = {
                        showSuccessDialog = false
                        exitEditMode()
                    }) {
                        Text("OK")
                    }
                }
            )
        }

        if (showDeleteConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmDialog = false },
                title = { Text("Conferma Cancellazione") },
                text = { Text("Sei sicuro di voler cancellare questo immobile? L'azione è irreversibile.") },
                confirmButton = {
                    Button(
                        onClick = {
                            // TODO: Add logic to delete the property
                            showDeleteConfirmDialog = false
                            navController.popBackStack() // Go back after deletion
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.error)
                    ) {
                        Text("Elimina")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmDialog = false }) {
                        Text("Annulla")
                    }
                }
            )
        }

        if (showUnsavedChangesDialog) {
            AlertDialog(
                onDismissRequest = { showUnsavedChangesDialog = false },
                title = { Text("Modifiche Non Salvate") },
                text = { Text("Vuoi uscire senza salvare le modifiche?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showUnsavedChangesDialog = false
                            revertChangesAndExit()
                        }
                    ) {
                        Text("Esci senza salvare")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUnsavedChangesDialog = false }) {
                        Text("Continua a modificare")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditPropertyTopAppBar(
    onBackClick: () -> Unit,
    isEditing: Boolean,
    onEditToggle: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    TopAppBar(
        title = {
            Text(
                text="Modifica Immobile",
                style = typography.titleLarge,
                color = colorScheme.onPrimary
            ) },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro",
                    tint = colorScheme.onPrimary
                )
            }
        },
        actions = {
            Box(
                modifier = Modifier.padding(end = dimensions.paddingMedium)
            ){
                CircularIconActionButton(
                    onClick = onEditToggle,
                    iconVector = if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                    contentDescription = if (isEditing) "Annulla Modifiche" else "Modifica",
                    backgroundColor = colorScheme.primaryContainer,
                    iconTint = if (isEditing) colorScheme.error else colorScheme.onPrimary,
                    iconSize = dimensions.iconSizeMedium
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.primary,
            scrolledContainerColor = colorScheme.surface
        )
    )
}

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
            Box(
                modifier = Modifier.size(imageSize)
            ) {
                Image(
                    painter = painterResource(id = images[index]),
                    contentDescription = "Immagine proprietà ${index + 1}",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(dimensions.cornerRadiusMedium)),
                    contentScale = ContentScale.Crop
                )
                if (isEditing) {
                    IconButton(
                        onClick = { onRemoveImage(index) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(dimensions.paddingExtraSmall)
                            .size(dimensions.cornerRadiusExtraLarge)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Rimuovi immagine",
                            tint = Color.White,
                            modifier = Modifier.size(dimensions.iconSizeSmall)
                        )
                    }
                }
            }
        }
        if (isEditing && images.size < 20) {
            item {
                AddImageButton(
                    onClick = onAddImage,
                    modifier = Modifier.size(imageSize)
                )
            }
        }
    }
}

@Composable
private fun AddImageButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(Dimensions.cornerRadiusMedium),
        border = BorderStroke(Dimensions.borderStrokeSmall, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Aggiungi immagine",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Aggiungi immagine",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = Dimensions.spacingExtraSmall)
            )
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
                Icon(Icons.Default.Remove, contentDescription = "Diminuisci")
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
                Icon(Icons.Default.Add, contentDescription = "Aumenta")
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
            modifier = Modifier.padding(bottom = Dimensions.spacingSmall)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimensions.cornerRadiusMedium),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            elevation = CardDefaults.cardElevation(defaultElevation = Dimensions.elevationNone)
        ) {
            Column(modifier = Modifier.padding(Dimensions.paddingMedium)) {
                content()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditPropertyScreenPreview() {
    DietiEstatesTheme {
        EditPropertyScreen(navController = rememberNavController())
    }
}

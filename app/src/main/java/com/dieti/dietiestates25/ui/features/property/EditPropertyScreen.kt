package com.dieti.dietiestates25.ui.features.property

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.data.remote.AmbienteDto
import com.dieti.dietiestates25.data.remote.ImmobileCreateRequest
import com.dieti.dietiestates25.data.remote.ImmobileDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.data.remote.ImmagineDto
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppRedButton
import com.dieti.dietiestates25.ui.components.CircularIconActionButton
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.utils.getIconForRoomType
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPropertyScreen(
    navController: NavController,
    immobileId: String,
    viewModel: EditPropertyViewModel = viewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(immobileId) {
        viewModel.loadProperty(immobileId)
    }

    LaunchedEffect(uiState.isSuccessOperation) {
        if (uiState.isSuccessOperation) {
            navController.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colors = listOf(colorScheme.primary, colorScheme.background)))
    ) {
        if (uiState.immobile != null) {
            EditPropertyForm(
                immobile = uiState.immobile!!,
                navController = navController,
                viewModel = viewModel
            )
        }

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(enabled = true, onClick = {}),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colorScheme.onPrimary)
            }
        }

        if (uiState.error != null && !uiState.isLoading) {
            if (uiState.immobile == null) {
                Text(
                    text = uiState.error!!,
                    color = colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                        .fillMaxWidth(),
                    color = colorScheme.errorContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = uiState.error!!,
                        color = colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// Wrapper per gestire sia immagini remote (String URL) che locali (Uri)
data class DisplayImage(
    val id: Int? = null, // ID server, null se locale
    val model: Any // String (URL) o Uri
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditPropertyForm(
    immobile: ImmobileDTO,
    navController: NavController,
    viewModel: EditPropertyViewModel
) {
    val dimensions = Dimensions
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val focusManager = LocalFocusManager.current

    // --- STATE FIELDS ---
    var isEditing by remember { mutableStateOf(false) }
    var showUnsavedChangesDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // --- DATI PRINCIPALI ---
    var currentTipoVendita by remember(immobile) { mutableStateOf(immobile.tipoVendita) }
    var currentCategoria by remember(immobile) { mutableStateOf(immobile.categoria ?: "") }
    var currentIndirizzo by remember(immobile) { mutableStateOf(immobile.indirizzo ?: "") }
    var currentLocalita by remember(immobile) { mutableStateOf(immobile.localita ?: "") }

    // --- DATI TECNICI ---
    var currentMq by remember(immobile) { mutableStateOf(immobile.mq?.toString() ?: "") }
    var currentPiano by remember(immobile) { mutableStateOf(immobile.piano?.toString() ?: "") }
    var currentAscensore by remember(immobile) { mutableStateOf(immobile.ascensore ?: false) }
    var currentArredamento by remember(immobile) { mutableStateOf(immobile.arredamento ?: "") }
    var currentClimatizzazione by remember(immobile) { mutableStateOf(immobile.climatizzazione ?: false) }
    var currentEsposizione by remember(immobile) { mutableStateOf(immobile.esposizione ?: "") }
    var currentStatoProprieta by remember(immobile) { mutableStateOf(immobile.statoProprieta ?: "") }
    var currentAnnoCostruzione by remember(immobile) { mutableStateOf(immobile.annoCostruzione ?: "") }

    // --- ECONOMICA ---
    var currentPrice by remember(immobile) { mutableStateOf(immobile.prezzo?.toString() ?: "") }
    var currentSpeseCondominiali by remember(immobile) { mutableStateOf(immobile.speseCondominiali?.toString() ?: "") }

    // --- DESCRIZIONE & AMBIENTI ---
    var currentDesc by remember(immobile) { mutableStateOf(immobile.descrizione ?: "") }

    // Lista reale degli ambienti (quella che verrà salvata)
    val currentAmbienti = remember(immobile) {
        mutableStateListOf<AmbienteDto>().apply { addAll(immobile.ambienti) }
    }

    // --- CALCOLO LISTA AMBIENTI VISIBILI ---
    val allRoomTypes = listOf("Cucina", "Soggiorno", "Camera da Letto", "Bagno", "Studio", "Balcone", "Giardino", "Altro")

    // Questa è la lista che viene mostrata a schermo.
    // Se isEditing = true, mostra TUTTI i tipi (anche quelli a 0).
    // Se isEditing = false, mostra solo quelli presenti nell'immobile (> 0).
    val displayAmbienti = remember(currentAmbienti.toList(), isEditing) {
        if (isEditing) {
            val existingMap = currentAmbienti.associateBy { it.tipologia }
            allRoomTypes.map { type ->
                existingMap[type] ?: AmbienteDto(type, 0)
            }
        } else {
            currentAmbienti.filter { it.numero > 0 }
        }
    }

    // --- GESTIONE IMMAGINI DEFERITA ---
    val serverImages = remember(immobile.immagini) {
        (immobile.immagini as? List<ImmagineDto>) ?: emptyList()
    }
    val imagesToDelete = remember { mutableStateListOf<Int>() }
    val newLocalImages = remember { mutableStateListOf<Uri>() }

    val displayImages = remember(serverImages, imagesToDelete.size, newLocalImages.size) {
        val visibleServerImages = serverImages
            .filter { it.id !in imagesToDelete }
            .map { DisplayImage(id = it.id, model = RetrofitClient.getFullUrl(it.url) ?: "") }
        val visibleLocalImages = newLocalImages.map { DisplayImage(id = null, model = it) }
        visibleServerImages + visibleLocalImages
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            if (uris.isNotEmpty()) {
                newLocalImages.addAll(uris)
            }
        }
    )

    // Logica di controllo modifiche
    val isModified = currentPrice != (immobile.prezzo?.toString() ?: "") ||
            currentMq != (immobile.mq?.toString() ?: "") ||
            currentIndirizzo != immobile.indirizzo ||
            currentLocalita != (immobile.localita ?: "") ||
            currentAmbienti != immobile.ambienti ||
            newLocalImages.isNotEmpty() ||
            imagesToDelete.isNotEmpty()

    Scaffold(
        topBar = {
            GeneralHeaderBar(
                title = "Modifica Immobile",
                onBackClick = {
                    if (isEditing && isModified) showUnsavedChangesDialog = true
                    else navController.popBackStack()
                },
                actions = {
                    Box(modifier = Modifier.padding(end = 8.dp)) {
                        CircularIconActionButton(
                            onClick = {
                                if (isEditing) {
                                    if (isModified) showUnsavedChangesDialog = true
                                    else {
                                        isEditing = false
                                        newLocalImages.clear()
                                        imagesToDelete.clear()
                                    }
                                } else {
                                    isEditing = true
                                }
                            },
                            iconVector = if (isEditing) Icons.Default.Close else Icons.Default.Edit,
                            contentDescription = if (isEditing) "Annulla" else "Modifica",
                            backgroundColor = if (isEditing) colorScheme.errorContainer else colorScheme.primaryContainer,
                            iconTint = if (isEditing) colorScheme.error else colorScheme.primary,
                            iconSize = dimensions.iconSizeMedium
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Brush.verticalGradient(colors = listOf(colorScheme.primary, colorScheme.background)))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { focusManager.clearFocus() }
                )
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = dimensions.paddingExtraLarge),
                verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
            ) {
                item { Spacer(modifier = Modifier.height(dimensions.spacingSmall)) }

                item {
                    Column(
                        modifier = Modifier.padding(horizontal = dimensions.paddingMedium),
                        verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
                    ) {
                        // --- 0. GALLERIA ---
                        EditInfoCard(title = "Galleria") {
                            EditableImageSection(
                                images = displayImages,
                                isEditing = isEditing,
                                onAddClick = {
                                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                                onDeleteClick = { item ->
                                    if (item.id != null) imagesToDelete.add(item.id)
                                    else newLocalImages.remove(item.model)
                                },
                                dimensions = dimensions,
                                colorScheme = colorScheme
                            )
                        }

                        // --- 1. DATI PRINCIPALI ---
                        EditInfoCard(title = "Dati Principali") {
                            Column(verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)) {
                                EditSegmentedControl(
                                    option1 = "Vendita",
                                    option2 = "Affitto",
                                    selectedOption = if (currentTipoVendita) "Vendita" else "Affitto",
                                    onOptionSelected = { currentTipoVendita = (it == "Vendita") },
                                    enabled = isEditing,
                                    colorScheme = colorScheme
                                )

                                EditDropdown(
                                    label = "Categoria",
                                    options = listOf("Residenziale", "Commerciale", "Industriale", "Terreno"),
                                    selectedOption = currentCategoria,
                                    onOptionSelected = { currentCategoria = it },
                                    enabled = isEditing
                                )

                                OutlinedTextField(
                                    value = currentIndirizzo,
                                    onValueChange = { currentIndirizzo = it },
                                    label = { Text("Indirizzo") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = isEditing,
                                    leadingIcon = { Icon(Icons.Default.LocationOn, null, tint = colorScheme.primary) }
                                )

                                OutlinedTextField(
                                    value = currentLocalita,
                                    onValueChange = { currentLocalita = it },
                                    label = { Text("Località") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = isEditing
                                )
                            }
                        }

                        // --- 2. DETTAGLI TECNICI ---
                        EditInfoCard(title = "Dettagli Tecnici") {
                            Column(verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = currentMq,
                                        onValueChange = { if (it.all { c -> c.isDigit() }) currentMq = it },
                                        label = { Text("Mq") },
                                        modifier = Modifier.weight(1f),
                                        enabled = isEditing,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                    )
                                    OutlinedTextField(
                                        value = currentPiano,
                                        onValueChange = { if (it.all { c -> c.isDigit() || c == '-' }) currentPiano = it },
                                        label = { Text("Piano") },
                                        modifier = Modifier.weight(1f),
                                        enabled = isEditing,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                    )
                                }

                                EditSwitchRow("Ascensore", currentAscensore, { currentAscensore = it }, isEditing)
                                EditSwitchRow("Climatizzazione", currentClimatizzazione, { currentClimatizzazione = it }, isEditing)

                                EditDropdown(
                                    label = "Arredamento",
                                    options = listOf("Non arredato", "Parzialmente arredato", "Arredato"),
                                    selectedOption = currentArredamento,
                                    onOptionSelected = { currentArredamento = it },
                                    enabled = isEditing
                                )

                                EditDropdown(
                                    label = "Esposizione",
                                    options = listOf("Nord", "Sud", "Est", "Ovest", "Doppia", "Multipla"),
                                    selectedOption = currentEsposizione,
                                    onOptionSelected = { currentEsposizione = it },
                                    enabled = isEditing
                                )

                                EditDropdown(
                                    label = "Stato Proprietà",
                                    options = listOf("Nuovo", "Ristrutturato", "Buono stato", "Da ristrutturare"),
                                    selectedOption = currentStatoProprieta,
                                    onOptionSelected = { currentStatoProprieta = it },
                                    enabled = isEditing
                                )

                                OutlinedTextField(
                                    value = currentAnnoCostruzione,
                                    onValueChange = { if (it.length <= 10) currentAnnoCostruzione = it },
                                    label = { Text("Anno Costruzione (YYYY)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = isEditing,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }
                        }

                        // --- 3. ECONOMICA ---
                        EditInfoCard(title = "Economica") {
                            Column(verticalArrangement = Arrangement.spacedBy(dimensions.spacingSmall)) {
                                OutlinedTextField(
                                    value = currentPrice,
                                    onValueChange = { if (it.all { c -> c.isDigit() }) currentPrice = it },
                                    label = { Text("Prezzo (€)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = isEditing,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    leadingIcon = { Icon(Icons.Default.Euro, null, tint = colorScheme.primary) }
                                )
                                OutlinedTextField(
                                    value = currentSpeseCondominiali,
                                    onValueChange = { if (it.all { c -> c.isDigit() }) currentSpeseCondominiali = it },
                                    label = { Text("Spese Condominiali (€/mese)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = isEditing,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )
                            }
                        }

                        // --- 4. AMBIENTI ---
                        EditInfoCard(title = "Ambienti") {
                            // Usiamo la lista combinata per visualizzare
                            EditablePropertyFeaturesRow(
                                mq = currentMq,
                                ambienti = displayAmbienti, // Lista calcolata
                                isEditing = isEditing,
                                onAmbienteChange = { updatedAmbiente ->
                                    // LOGICA DI AGGIORNAMENTO DELLA LISTA REALE
                                    val index = currentAmbienti.indexOfFirst { it.tipologia == updatedAmbiente.tipologia }

                                    if (index != -1) {
                                        // Se esiste già
                                        if (updatedAmbiente.numero > 0) {
                                            currentAmbienti[index] = updatedAmbiente
                                        } else {
                                            // Se scende a 0, lo rimuoviamo dalla lista reale
                                            currentAmbienti.removeAt(index)
                                        }
                                    } else {
                                        // Se non esiste (era a 0 nel display list) e ora > 0, lo aggiungiamo
                                        if (updatedAmbiente.numero > 0) {
                                            currentAmbienti.add(updatedAmbiente)
                                        }
                                    }
                                },
                                colorScheme = colorScheme,
                                typography = typography,
                                dimensions = dimensions
                            )
                        }

                        // --- 5. DESCRIZIONE ---
                        EditInfoCard(title = "Descrizione") {
                            OutlinedTextField(
                                value = currentDesc,
                                onValueChange = { currentDesc = it },
                                modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = dimensions.infoCardHeight),
                                enabled = isEditing,
                                singleLine = false,
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default)
                            )
                        }

                        if (isEditing) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium),
                                modifier = Modifier.padding(top = dimensions.spacingMedium)
                            ) {
                                AppPrimaryButton(
                                    text = "Salva Modifiche",
                                    onClick = { showSuccessDialog = true },
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
                        tipoVendita = currentTipoVendita,
                        categoria = currentCategoria,
                        indirizzo = currentIndirizzo,
                        localita = currentLocalita,
                        mq = currentMq.toIntOrNull(),
                        piano = currentPiano.toIntOrNull(),
                        ascensore = currentAscensore,
                        arredamento = currentArredamento,
                        climatizzazione = currentClimatizzazione,
                        esposizione = currentEsposizione,
                        statoProprieta = currentStatoProprieta,
                        annoCostruzione = currentAnnoCostruzione,
                        prezzo = currentPrice.toIntOrNull(),
                        speseCondominiali = currentSpeseCondominiali.toIntOrNull(),
                        descrizione = currentDesc,
                        ambienti = currentAmbienti.toList()
                    )

                }) { Text("Salva") }
            },
            dismissButton = { TextButton(onClick = { showSuccessDialog = false }) { Text("Annulla") } }
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
            dismissButton = { TextButton(onClick = { showDeleteConfirmDialog = false }) { Text("Annulla") } }
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
                    isEditing = false
                    newLocalImages.clear()
                    imagesToDelete.clear()
                }) { Text("Esci senza salvare") }
            },
            dismissButton = { TextButton(onClick = { showUnsavedChangesDialog = false }) { Text("Resta") } }
        )
    }
}

// ... (Componenti Helper EditSegmentedControl, EditDropdown, EditSwitchRow rimangono invariati) ...
@Composable
fun EditSegmentedControl(option1: String, option2: String, selectedOption: String, onOptionSelected: (String) -> Unit, enabled: Boolean, colorScheme: ColorScheme) {
    Row(modifier = Modifier.fillMaxWidth().height(40.dp).clip(RoundedCornerShape(8.dp)).background(colorScheme.surfaceVariant), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(if (selectedOption == option1) colorScheme.primary else Color.Transparent).clickable(enabled = enabled) { onOptionSelected(option1) }, contentAlignment = Alignment.Center) {
            Text(option1, color = if (selectedOption == option1) colorScheme.onPrimary else colorScheme.onSurfaceVariant)
        }
        Box(modifier = Modifier.weight(1f).fillMaxHeight().background(if (selectedOption == option2) colorScheme.primary else Color.Transparent).clickable(enabled = enabled) { onOptionSelected(option2) }, contentAlignment = Alignment.Center) {
            Text(option2, color = if (selectedOption == option2) colorScheme.onPrimary else colorScheme.onSurfaceVariant)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDropdown(label: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit, enabled: Boolean) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded && enabled, onExpandedChange = { if (enabled) expanded = !expanded }) {
        OutlinedTextField(value = selectedOption, onValueChange = {}, label = { Text(label) }, readOnly = true, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }, modifier = Modifier.menuAnchor().fillMaxWidth(), enabled = enabled)
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option -> DropdownMenuItem(text = { Text(option) }, onClick = { onOptionSelected(option); expanded = false }) }
        }
    }
}

@Composable
fun EditSwitchRow(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, enabled: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().height(56.dp).clickable(enabled = enabled) { onCheckedChange(!checked) }, verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Switch(checked = checked, onCheckedChange = onCheckedChange, enabled = enabled)
    }
}

@Composable
fun EditablePropertyFeaturesRow(
    mq: String,
    ambienti: List<AmbienteDto>,
    isEditing: Boolean,
    onAmbienteChange: (AmbienteDto) -> Unit, // Firma aggiornata per ricevere direttamente l'oggetto
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    val listState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxWidth()) {
        LazyRow(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(horizontal = dimensions.paddingSmall)
        ) {
            item {
                EditableFeatureItem(
                    icon = Icons.Default.SquareFoot,
                    value = "$mq m²",
                    label = "Superficie",
                    isEditing = false,
                    onIncrement = {},
                    onDecrement = {},
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions
                )
            }

            items(ambienti) { ambiente ->
                val icon = getIconForRoomType(ambiente.tipologia)
                val label = ambiente.tipologia.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
                }

                EditableFeatureItem(
                    icon = icon,
                    value = "${ambiente.numero}",
                    label = label,
                    isEditing = isEditing,
                    onIncrement = {
                        onAmbienteChange(ambiente.copy(numero = ambiente.numero + 1))
                    },
                    onDecrement = {
                        onAmbienteChange(ambiente.copy(numero = (ambiente.numero - 1).coerceAtLeast(0)))
                    },
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions
                )
            }
        }

        // Scrollbar (Semplificata per brevità ma presente concettualmente come prima)
    }
}

@Composable
fun EditableFeatureItem(
    icon: ImageVector,
    value: String,
    label: String,
    isEditing: Boolean,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.width(IntrinsicSize.Min)
    ) {
        Surface(
            shape = CircleShape,
            color = colorScheme.primaryContainer,
            modifier = Modifier.size(48.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = icon, contentDescription = label, tint = colorScheme.primary, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            if (isEditing) {
                IconButton(onClick = onDecrement, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Remove, "Diminuisci", tint = colorScheme.error)
                }
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(text = value, style = typography.titleMedium, fontWeight = FontWeight.Bold, color = colorScheme.onSurface)
            if (isEditing) {
                Spacer(modifier = Modifier.width(4.dp))
                IconButton(onClick = onIncrement, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Add, "Aumenta", tint = colorScheme.primary)
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = typography.bodySmall, color = colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
private fun EditableImageSection(
    images: List<DisplayImage>,
    isEditing: Boolean,
    onAddClick: () -> Unit,
    onDeleteClick: (DisplayImage) -> Unit,
    dimensions: Dimensions,
    colorScheme: ColorScheme
) {
    val imageSize = dimensions.logoLarge
    LazyRow(
        contentPadding = PaddingValues(vertical = dimensions.paddingSmall),
        horizontalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
    ) {
        items(images.size) { index ->
            val img = images[index]
            Box(modifier = Modifier.size(imageSize)) {
                AsyncImage(
                    model = img.model,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(dimensions.cornerRadiusMedium)),
                    contentScale = ContentScale.Crop
                )
                if (isEditing) {
                    IconButton(
                        onClick = { onDeleteClick(img) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(24.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .padding(4.dp)
                    ) {
                        Icon(Icons.Default.Close, "Elimina", tint = Color.White)
                    }
                }
            }
        }
        if (isEditing) {
            item {
                Surface(
                    onClick = onAddClick,
                    modifier = Modifier.size(imageSize).clip(RoundedCornerShape(dimensions.cornerRadiusMedium)),
                    color = colorScheme.surfaceVariant,
                    border = BorderStroke(1.dp, colorScheme.outline)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, "Aggiungi", tint = colorScheme.primary)
                    }
                }
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
            color = MaterialTheme.colorScheme.onBackground
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
package com.dieti.dietiestates25.ui.features.property

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dieti.dietiestates25.data.remote.AmbienteDto
import com.dieti.dietiestates25.data.remote.ImmobileCreateRequest
import com.dieti.dietiestates25.ui.components.*
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.utils.SessionManager

@Composable
fun PropertySellScreen(
    navController: NavController,
    idUtente: String,
    viewModel: PropertySellViewModel = viewModel()
) {
    val context = LocalContext.current
    val colorScheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()

    val formState by viewModel.formState.collectAsState()
    val userRole = remember { SessionManager.getUserRole(context) ?: "UTENTE" }

    // --- FORM VARIABLES ---
    var tipoVendita by remember { mutableStateOf("Vendita") }
    var categoria by remember { mutableStateOf("") }
    var indirizzo by remember { mutableStateOf("") }
    // RIMOSSO: var localita by remember { mutableStateOf("") }
    var mq by remember { mutableStateOf("") }
    var piano by remember { mutableStateOf("") }
    var ascensore by remember { mutableStateOf(false) }
    var arredamento by remember { mutableStateOf("") }
    var climatizzazione by remember { mutableStateOf(false) }
    var esposizione by remember { mutableStateOf("") }
    var statoProprieta by remember { mutableStateOf("") }
    var annoCostruzione by remember { mutableStateOf("") }
    var prezzo by remember { mutableStateOf("") }
    var speseCondominiali by remember { mutableStateOf("") }
    var descrizione by remember { mutableStateOf("") }

    val addedAmbienti = remember { mutableStateListOf<AmbienteDto>() }
    var currentAmbienteType by remember { mutableStateOf("") }
    var currentAmbienteNum by remember { mutableStateOf("1") }

    var selectedImageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris -> selectedImageUris = uris }
    )

    fun isFormValid(): Boolean {
        return categoria.isNotBlank() &&
                indirizzo.isNotBlank() &&
                // RIMOSSO: localita.isNotBlank() &&
                mq.isNotBlank() &&
                prezzo.isNotBlank() &&
                selectedImageUris.isNotEmpty()
    }

    LaunchedEffect(formState) {
        when (formState) {
            is PropertyFormState.Success -> {
                Toast.makeText(context, "Annuncio inviato! Sarà assegnato all'agenzia più vicina.", Toast.LENGTH_LONG).show()
                viewModel.resetState()
                if (userRole == "MANAGER" || userRole == "ADMIN") {
                    navController.navigate(Screen.ManagerScreen.withIdUtente(idUtente)) { popUpTo(Screen.ManagerScreen.route) { inclusive = true } }
                } else {
                    navController.navigate(Screen.HomeScreen.withIdUtente(idUtente)) { popUpTo(Screen.HomeScreen.route) { inclusive = true } }
                }
            }
            is PropertyFormState.Error -> { /* Gestito nella UI */ }
            else -> {}
        }
    }

    val gradientColors = arrayOf(0.0f to colorScheme.primary.copy(alpha = 0.1f), 1.0f to colorScheme.background)

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(colorStops = gradientColors))) {
        Column(modifier = Modifier.fillMaxSize().systemBarsPadding()) {
            GeneralHeaderBar(title = "Inserimento Proprietà", onBackClick = { navController.popBackStack() })

            if (formState is PropertyFormState.Loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f).verticalScroll(scrollState).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FormSection(title = "Dati Principali", isRequired = true) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SegmentedButton("Vendita", tipoVendita == "Vendita", { tipoVendita = "Vendita" }, Modifier.weight(1f))
                            SegmentedButton("Affitto", tipoVendita == "Affitto", { tipoVendita = "Affitto" }, Modifier.weight(1f))
                        }
                        DropdownMenuField("Categoria", categoria, { categoria = it }, listOf("Residenziale", "Commerciale", "Industriale", "Terreno"))

                        OutlinedTextField(
                            value = indirizzo, onValueChange = { indirizzo = it },
                            // MODIFICA IMPORTANTE: Invito l'utente a inserire anche la città qui
                            label = { Text("Indirizzo completo (Via, Civico, Città)") },
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.Place, null) }
                        )

                        // RIMOSSO: Campo Località
                    }

                    FormSection(title = "Dettagli Tecnici", isRequired = true) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = mq, onValueChange = { if (it.all { c -> c.isDigit() }) mq = it }, label = { Text("Mq") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                            OutlinedTextField(value = piano, onValueChange = { if (it.all { c -> c.isDigit() || c == '-' }) piano = it }, label = { Text("Piano") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.weight(1f))
                        }
                        CheckboxField("Ascensore presente", ascensore, { ascensore = it })
                        DropdownMenuField("Arredamento", arredamento, { arredamento = it }, listOf("Non arredato", "Parzialmente arredato", "Arredato"))
                        CheckboxField("Climatizzazione presente", climatizzazione, { climatizzazione = it })
                        DropdownMenuField("Esposizione", esposizione, { esposizione = it }, listOf("Nord", "Sud", "Est", "Ovest", "Doppia", "Multipla"))
                        DropdownMenuField("Stato Proprietà", statoProprieta, { statoProprieta = it }, listOf("Nuovo", "Ristrutturato", "Buono stato", "Da ristrutturare"))
                        OutlinedTextField(value = annoCostruzione, onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) annoCostruzione = it }, label = { Text("Anno Costruzione (YYYY)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    }

                    FormSection(title = "Economica", isRequired = true) {
                        OutlinedTextField(value = prezzo, onValueChange = { if (it.all { c -> c.isDigit() }) prezzo = it }, label = { Text("Prezzo (€)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        OutlinedTextField(value = speseCondominiali, onValueChange = { if (it.all { c -> c.isDigit() }) speseCondominiali = it }, label = { Text("Spese Condominiali (€/mese)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    }

                    FormSection(title = "Descrizione", isRequired = true) {
                        OutlinedTextField(value = descrizione, onValueChange = { descrizione = it }, label = { Text("Descrizione") }, modifier = Modifier.fillMaxWidth().height(100.dp), maxLines = 5)
                    }

                    FormSection(title = "Ambienti", isRequired = true) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.weight(2f)) { DropdownMenuField("Tipo", currentAmbienteType, { currentAmbienteType = it }, listOf("Cucina", "Soggiorno", "Camera da Letto", "Bagno", "Studio", "Balcone", "Giardino", "Altro"), required = false) }
                            OutlinedTextField(value = currentAmbienteNum, onValueChange = { if(it.all { c -> c.isDigit() }) currentAmbienteNum = it }, label = { Text("N.") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                            Button(onClick = { if (currentAmbienteType.isNotBlank() && currentAmbienteNum.isNotBlank()) { addedAmbienti.add(AmbienteDto(currentAmbienteType, currentAmbienteNum.toInt())); currentAmbienteType = ""; currentAmbienteNum = "1" } }, enabled = currentAmbienteType.isNotBlank()) { Text("Add") }
                        }
                        if (addedAmbienti.isNotEmpty()) {
                            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant)) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    addedAmbienti.forEachIndexed { index, item ->
                                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                            Text("• ${item.numero}x ${item.tipologia}")
                                            IconButton(onClick = { addedAmbienti.removeAt(index) }, modifier = Modifier.size(24.dp)) { Icon(Icons.Default.Close, "Rimuovi", tint = colorScheme.error) }
                                        }
                                        if (index < addedAmbienti.size - 1) Divider()
                                    }
                                }
                            }
                        }
                    }

                    FormSection(title = "Galleria", isRequired = true) {
                        Box(
                            modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp)).background(colorScheme.surfaceVariant).border(1.dp, if(selectedImageUris.isEmpty()) colorScheme.error else colorScheme.outline, RoundedCornerShape(12.dp)).clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.AddPhotoAlternate, null, tint = colorScheme.primary, modifier = Modifier.size(32.dp))
                                Text(if (selectedImageUris.isEmpty()) "Tocca per selezionare foto (Obbligatorio)" else "${selectedImageUris.size} foto selezionate", color = if (selectedImageUris.isEmpty()) colorScheme.error else colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    if (formState is PropertyFormState.Error) {
                        Text(text = (formState as PropertyFormState.Error).message, color = colorScheme.error, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            val request = ImmobileCreateRequest(
                                tipoVendita = (tipoVendita == "Vendita"),
                                categoria = categoria,
                                indirizzo = indirizzo,
                                localita = "", // Passiamo stringa vuota, il backend userà geoapify sull'indirizzo
                                mq = mq.toIntOrNull(),
                                piano = piano.toIntOrNull(),
                                ascensore = ascensore,
                                arredamento = arredamento,
                                climatizzazione = climatizzazione,
                                esposizione = esposizione,
                                statoProprieta = statoProprieta,
                                annoCostruzione = if (annoCostruzione.length == 4) "$annoCostruzione-01-01" else null,
                                prezzo = prezzo.toIntOrNull(),
                                speseCondominiali = speseCondominiali.toIntOrNull(),
                                descrizione = descrizione,
                                ambienti = addedAmbienti.toList()
                            )
                            viewModel.submitAd(context, request, selectedImageUris, onSuccess = {})
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = isFormValid(),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isFormValid()) colorScheme.primary else colorScheme.secondary)
                    ) { Text("Pubblica Annuncio") }

                    if (!isFormValid()) Text("Compila tutti i campi obbligatori (inclusa Foto).", style = MaterialTheme.typography.bodySmall, color = colorScheme.error, modifier = Modifier.align(Alignment.CenterHorizontally))
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
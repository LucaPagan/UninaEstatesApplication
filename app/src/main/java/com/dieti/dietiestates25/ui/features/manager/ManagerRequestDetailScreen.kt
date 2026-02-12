package com.dieti.dietiestates25.ui.features.manager

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.data.remote.TrattativaSummaryDTO
import com.dieti.dietiestates25.ui.components.AppOutlinedTextField
import com.dieti.dietiestates25.ui.features.property.PropertyContent
import com.dieti.dietiestates25.ui.features.property.PropertyTopAppBar
import com.dieti.dietiestates25.ui.features.property.PropertyViewModel
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.utils.SessionManager
import kotlinx.coroutines.launch

@Composable
fun ManagerRequestDetailScreen(
    navController: NavController,
    offertaId: String,
    offersViewModel: ManagerOffersViewModel = viewModel(),
    propertyViewModel: PropertyViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val colorScheme = MaterialTheme.colorScheme
    val dimensions = Dimensions

    val agentId = remember { SessionManager.getUserId(context) }
    LaunchedEffect(agentId) {
        if (!agentId.isNullOrBlank() && offersViewModel.offerte.value.isEmpty()) {
            offersViewModel.loadOfferte(agentId)
        }
    }

    val offerte by offersViewModel.offerte.collectAsState()
    // FIX: Uso offertaId (campo corretto del DTO aggiornato)
    val offertaTarget = offerte.find { it.offertaId == offertaId }

    LaunchedEffect(offertaTarget) {
        if (offertaTarget != null) {
            // FIX: Uso immobileId (campo corretto)
            propertyViewModel.loadProperty(offertaTarget.immobileId)
        }
    }

    val property by propertyViewModel.property.collectAsState()
    val isLoading by propertyViewModel.isLoading.collectAsState()

    var showCounterDialog by remember { mutableStateOf(false) }
    var counterPrice by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }

    Scaffold { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            if (offertaTarget == null || isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else if (property != null) {
                val imm = property!!

                PropertyContent(
                    immobile = imm,
                    navController = navController,
                    coroutineScope = scope,
                    colorScheme = colorScheme,
                    typography = MaterialTheme.typography,
                    dimensions = dimensions,
                    context = context,
                    bottomActions = {
                        // FIX: Passiamo il nuovo oggetto TrattativaSummaryDTO
                        OfferActionCard(
                            offerta = offertaTarget,
                            colorScheme = colorScheme,
                            isProcessing = isProcessing,
                            onReject = {
                                submitOfferDecision(scope, context, offertaTarget.offertaId, "RIFIUTATA", null, null, navController) { isProcessing = it }
                            },
                            onCounter = { showCounterDialog = true },
                            onAccept = {
                                submitOfferDecision(scope, context, offertaTarget.offertaId, "ACCETTATA", null, null, navController) { isProcessing = it }
                            }
                        )
                    }
                )
            }
            PropertyTopAppBar(colorScheme = colorScheme, navController = navController, dimensions = dimensions)
        }
    }

    if (showCounterDialog) {
        AlertDialog(
            onDismissRequest = { showCounterDialog = false },
            title = { Text("Fai una Controproposta") },
            text = {
                Column {
                    Text("Prezzo originale: € ${String.format("%,d", offertaTarget?.prezzoOfferto ?: 0)}", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                    AppOutlinedTextField(
                        value = counterPrice,
                        onValueChange = { if (it.all { c -> c.isDigit() }) counterPrice = it },
                        placeholder = { Text("Nuovo Prezzo (€)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    AppOutlinedTextField(
                        value = note,
                        onValueChange = { note = it },
                        placeholder = { Text("Messaggio per l'utente") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val price = counterPrice.toIntOrNull()
                    if (price != null && price > 0) {
                        submitOfferDecision(scope, context, offertaId, "CONTROPROPOSTA", price, note, navController) { isProcessing = it }
                        showCounterDialog = false
                    } else {
                        Toast.makeText(context, "Inserisci un prezzo valido", Toast.LENGTH_SHORT).show()
                    }
                }) { Text("Invia") }
            },
            dismissButton = { TextButton(onClick = { showCounterDialog = false }) { Text("Annulla") } }
        )
    }
}

@Composable
fun OfferActionCard(
    offerta: TrattativaSummaryDTO, // FIX: Tipo aggiornato
    colorScheme: ColorScheme,
    isProcessing: Boolean,
    onReject: () -> Unit,
    onCounter: () -> Unit,
    onAccept: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("OFFERTA ATTUALE", style = MaterialTheme.typography.labelSmall, color = colorScheme.primary)
            Spacer(Modifier.height(4.dp))
            // FIX: Accesso ai campi corretti
            Text("€ ${String.format("%,d", offerta.prezzoOfferto)}", style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
            Text("da ${offerta.nomeOfferente}", style = MaterialTheme.typography.bodyMedium)

            Spacer(Modifier.height(16.dp))

            if (isProcessing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            } else {
                // I bottoni si mostrano solo se lo stato permette una risposta
                // (Es. se è NUOVA o l'utente ha fatto una CONTROPROPOSTA)
                // Se abbiamo accettato o rifiutato noi, non dovremmo vedere bottoni, ma questa logica
                // può essere gestita anche lato visualizzazione o backend (stato 'closed').
                // Per ora lasciamo i bottoni attivi per agire.
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = onReject,
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.errorContainer, contentColor = colorScheme.onErrorContainer),
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                    ) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.Close, null); Text("Rifiuta") } }

                    Button(
                        onClick = onCounter,
                        colors = ButtonDefaults.buttonColors(containerColor = colorScheme.secondaryContainer, contentColor = colorScheme.onSecondaryContainer),
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                    ) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.Edit, null); Text("Tratta") } }

                    Button(
                        onClick = onAccept,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32), contentColor = Color.White),
                        modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)
                    ) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.Check, null); Text("Accetta") } }
                }
            }
        }
    }
}

fun submitOfferDecision(
    scope: kotlinx.coroutines.CoroutineScope,
    context: android.content.Context,
    offertaId: String,
    esito: String,
    nuovoPrezzo: Int?,
    messaggio: String?,
    navController: NavController,
    setLoading: (Boolean) -> Unit
) {
    scope.launch {
        setLoading(true)
        val agenteId = SessionManager.getUserId(context) ?: ""

        val request = RispostaRequest(
            offertaId = offertaId,
            venditoreId = agenteId,
            esito = esito,
            nuovoPrezzo = nuovoPrezzo,
            messaggio = messaggio
        )

        try {
            val response = RetrofitClient.managerService.inviaRisposta(request)
            if (response.isSuccessful) {
                Toast.makeText(context, "Operazione completata!", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            } else {
                Toast.makeText(context, "Errore API", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            // FIX JSON crash (Response<Unit>)
            if (e is java.io.EOFException) {
                Toast.makeText(context, "Operazione completata!", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            } else {
                Toast.makeText(context, "Errore connessione", Toast.LENGTH_SHORT).show()
            }
        } finally {
            setLoading(false)
        }
    }
}
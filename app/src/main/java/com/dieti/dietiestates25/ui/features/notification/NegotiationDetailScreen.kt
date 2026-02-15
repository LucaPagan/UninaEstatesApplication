package com.dieti.dietiestates25.ui.features.notification

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dieti.dietiestates25.data.remote.MessaggioTrattativaDTO
import com.dieti.dietiestates25.data.remote.RetrofitClient
import com.dieti.dietiestates25.data.remote.UserResponseRequest
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
import com.dieti.dietiestates25.ui.utils.SessionManager
import kotlinx.coroutines.launch

@Composable
fun NegotiationDetailScreen(
    navController: NavController,
    offertaId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userId = SessionManager.getUserId(context) ?: ""

    var history by remember { mutableStateOf<List<MessaggioTrattativaDTO>>(emptyList()) }
    var canReply by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) } // Per controproposta

    // Funzione per caricare i dati
    fun loadHistory() {
        scope.launch {
            isLoading = true
            try {
                // Ora notificationService è disponibile in RetrofitClient
                val res = RetrofitClient.notificationService.getStoriaTrattativa(offertaId, userId)
                if (res.isSuccessful) {
                    history = res.body()?.cronologia ?: emptyList()
                    canReply = res.body()?.canUserReply ?: false
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Errore caricamento: ${e.message}", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { loadHistory() }

    // Funzione invio risposta
    fun sendResponse(esito: String, prezzo: Int? = null, msg: String? = null) {
        scope.launch {
            isLoading = true
            try {
                val req = UserResponseRequest(offertaId, userId, esito, prezzo, msg)
                // Ora notificationService è disponibile
                val res = RetrofitClient.notificationService.inviaRispostaUtente(req)
                if (res.isSuccessful) {
                    Toast.makeText(context, "Risposta inviata!", Toast.LENGTH_SHORT).show()
                    loadHistory() // Ricarica la chat
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Errore rete", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            // FIX: Parametro nominato onBackClick obbligatorio
            GeneralHeaderBar(
                title = "Dettaglio Trattativa",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {

            // CHAT LIST
            LazyColumn(
                modifier = Modifier.weight(1f).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(history) { msg ->
                    ChatBubble(msg)
                }
            }

            // ACTION BUTTONS (Solo se tocca all'utente)
            if (canReply) {
                Surface(shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(15.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { sendResponse("RIFIUTATA") }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Rifiuta") }
                        Button(onClick = { showDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00))) { Text("Controproposta") }
                        Button(onClick = { sendResponse("ACCETTATA") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) { Text("Accetta") }
                    }
                }
            } else {
                Surface(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(
                        text = if (history.lastOrNull()?.tipo == "ACCETTATA") "Trattativa conclusa con successo!"
                        else if (history.lastOrNull()?.tipo == "RIFIUTATA") "Trattativa chiusa."
                        else "In attesa di risposta dall'agente...",
                        modifier = Modifier.padding(16.dp),
                        color = Color.Gray
                    )
                }
            }
        }
    }

    // DIALOG CONTROPROPOSTA
    if (showDialog) {
        var price by remember { mutableStateOf("") }
        var msg by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Fai una Controproposta") },
            text = {
                Column {
                    OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Prezzo €") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(value = msg, onValueChange = { msg = it }, label = { Text("Messaggio (Opzionale)") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    val p = price.toIntOrNull()
                    if (p != null) {
                        sendResponse("CONTROPROPOSTA", p, msg)
                        showDialog = false
                    }
                }) { Text("Invia") }
            },
            dismissButton = { TextButton({ showDialog = false }) { Text("Annulla") } }
        )
    }
}

@Composable
fun ChatBubble(msg: MessaggioTrattativaDTO) {
    val align = if (msg.isMe) Alignment.End else Alignment.Start
    val bg = if (msg.isMe) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant

    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = align) {
        Surface(shape = RoundedCornerShape(12.dp), color = bg, shadowElevation = 1.dp) {
            Column(Modifier.padding(12.dp)) {
                Text(if (msg.isMe) "Tu" else msg.autoreNome, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                if (msg.prezzo != null) {
                    Text("Proposta: € ${String.format("%,d", msg.prezzo)}", fontWeight = FontWeight.Bold, color = if (msg.isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary)
                }
                Text(msg.testo, color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(Modifier.height(4.dp))
                Text(msg.tipo, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
        }
    }
}
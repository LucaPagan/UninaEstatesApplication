package com.dieti.dietiestates25.ui.features.manager

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
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
import com.dieti.dietiestates25.ui.features.notification.ChatBubble
import com.dieti.dietiestates25.ui.utils.SessionManager
import kotlinx.coroutines.launch

@Composable
fun ManagerNegotiationChatScreen(
    navController: NavController,
    offertaId: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // Qui otteniamo l'ID dell'Agente
    val agentId = SessionManager.getUserId(context) ?: ""

    var history by remember { mutableStateOf<List<MessaggioTrattativaDTO>>(emptyList()) }
    var canReply by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }

    fun loadHistory() {
        scope.launch {
            isLoading = true
            try {
                // Usiamo lo stesso endpoint della history, passando l'ID Agente come viewer
                val res = RetrofitClient.notificationService.getStoriaTrattativa(offertaId, agentId)
                if (res.isSuccessful) {
                    history = res.body()?.cronologia ?: emptyList()
                    // Il backend calcola automaticamente 'canReply' basandosi sull'ID viewer (Agente)
                    canReply = res.body()?.canUserReply ?: false
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Errore caricamento chat", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) { loadHistory() }

    fun sendResponse(esito: String, prezzo: Int? = null, msg: String? = null) {
        scope.launch {
            isLoading = true
            try {
                // Usiamo il DTO di risposta per il manager
                val req = RispostaRequest(offertaId, agentId, esito, prezzo, msg)
                val res = RetrofitClient.managerService.inviaRisposta(req)
                
                if (res.isSuccessful) {
                    Toast.makeText(context, "Risposta inviata", Toast.LENGTH_SHORT).show()
                    loadHistory()
                } else {
                    Toast.makeText(context, "Errore invio: ${res.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Gestione crash JSON (Response<Unit>)
                if (e is java.io.EOFException) {
                    loadHistory()
                } else {
                    Toast.makeText(context, "Errore rete", Toast.LENGTH_SHORT).show()
                }
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            // FIX: Uso del parametro nominato 'onBackClick' per evitare ambiguità con 'actions'
            GeneralHeaderBar(
                title = "Trattativa",
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.weight(1f).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(history) { msg -> ChatBubble(msg) }
            }

            if (canReply) {
                Surface(shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth()) {
                    Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = { sendResponse("RIFIUTATA") }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) { Text("Rifiuta") }
                        Button(onClick = { showDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00))) { Text("Controproposta") }
                        Button(onClick = { sendResponse("ACCETTATA") }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))) { Text("Accetta") }
                    }
                }
            } else {
                val lastStatus = history.lastOrNull()?.tipo
                val infoText = when(lastStatus) {
                    "ACCETTATA" -> "Trattativa conclusa con successo."
                    "RIFIUTATA" -> "Trattativa interrotta."
                    else -> "In attesa dell'utente..."
                }
                Surface(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.fillMaxWidth()) {
                    Text(infoText, Modifier.padding(16.dp), color = Color.Gray)
                }
            }
        }
    }

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
                    OutlinedTextField(value = msg, onValueChange = { msg = it }, label = { Text("Messaggio") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    price.toIntOrNull()?.let {
                        sendResponse("CONTROPROPOSTA", it, msg)
                        showDialog = false
                    }
                }) { Text("Invia") }
            },
            dismissButton = { TextButton({ showDialog = false }) { Text("Annulla") } }
        )
    }
}
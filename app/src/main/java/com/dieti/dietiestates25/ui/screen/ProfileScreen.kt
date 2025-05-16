package com.dieti.dietiestates25.ui.screen

import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppRedButton
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalMaterial3Api::class) // Mantenuto se usi altri componenti M3 sperimentali
@Composable
fun ProfileScreen(
    navController: NavController,
) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography

        var isEditMode by remember { mutableStateOf(false) }
        val focusManager = LocalFocusManager.current

        var name by remember { mutableStateOf("Lorenzo") }
        var email by remember { mutableStateOf("LorenzoTrignano@gmail.com") }
        var phone by remember { mutableStateOf("+39 123456789") }

        val imageSize = 100.dp
        val topBarColor = colorScheme.primary
        val coloredBackgroundSectionHeight = 56.dp + (imageSize / 2)
        val totalTopBarHeight = coloredBackgroundSectionHeight + (imageSize / 2) // Altezza fino al fondo dell'immagine

        val borderWidth = 3.dp
        val borderColor = colorScheme.onPrimary

        Scaffold(
            topBar = {
                // Fornisci direttamente il tuo Composable Box personalizzato qui
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(totalTopBarHeight) // Altezza totale della top bar custom
                        .statusBarsPadding()
                ) {
                    // Sfondo colorato (si estende fino al centro dell'immagine)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(coloredBackgroundSectionHeight)
                            .background(topBarColor)
                    )

                    Text(
                        text = "Modifica Profilo",
                        style = typography.titleMedium,
                        color = colorScheme.onPrimary,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                    )

                    IconButton(
                        onClick = { isEditMode = !isEditMode },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 8.dp, end = 12.dp)
                    ) {
                        Icon(
                            imageVector = if (isEditMode) Icons.Filled.Close else Icons.Filled.Edit,
                            contentDescription = if (isEditMode) "Salva Modifiche" else "Modifica Dati",
                            tint = if (isEditMode) colorScheme.error else colorScheme.onPrimary
                        )
                    }

                    // Box per l'immagine profilo (con bordo)
                    // Posizionato in modo che il suo fondo sia allineato con il fondo della totalTopBarHeight
                    Box(
                        modifier = Modifier
                            .size(imageSize)
                            .align(Alignment.BottomCenter) // Allinea al fondo della 'totalTopBarHeight'
                            .border(BorderStroke(borderWidth, borderColor), CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.profilo),
                            contentDescription = "Profile Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .matchParentSize()
                                .clip(CircleShape)
                        )
                    }
                }
            },
            bottomBar = {
                AppBottomNavigation(navController = navController, idUtente = "")
            }
        ) { scaffoldPaddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(scaffoldPaddingValues) // Applica i padding per topBar e bottomBar
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { focusManager.clearFocus() }
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Il padding superiore ora è relativo all'inizio dell'area di contenuto
                    // che è già sotto la topBar. Un padding standard è sufficiente.
                    Text(
                        text = "Dati personali",
                        style = typography.bodyLarge,
                        modifier = Modifier.padding(top = 24.dp, bottom = 16.dp) // Padding standard
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nome Utente") },
                        enabled = isEditMode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Id") },
                        enabled = isEditMode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Numero di telefono") },
                        enabled = isEditMode,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                    )

                    Text(
                        "Altro", style = typography.labelLarge, modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    ProfileOptionRow("Controlla immobili")
                    ProfileOptionRow("Controlla richieste agenzia")

                    Spacer(modifier = Modifier.height(24.dp))

                    AppPrimaryButton(
                        onClick = {
                            if (isEditMode) {
                                isEditMode = false
                                focusManager.clearFocus()
                            }
                        },
                        text = if (isEditMode) "Salva Modifiche" else "Aggiorna Profilo",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    AppPrimaryButton( // Presumo sia un bottone per il logout
                        onClick = {
                            // Logica per uscire dal profilo
                        },
                        text = "Esci Dal Profilo",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    AppRedButton(
                        onClick = {
                            // Logica per eliminare il profilo
                        },
                        text = "Elimina Profilo",
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun ProfileOptionRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.NightsStay, contentDescription = null)
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, modifier = Modifier.weight(1f))
        Icon(Icons.Default.KeyboardArrowRight, contentDescription = null)
    }
}

@Preview(showBackground = true, device = "spec:width=390dp,height=844dp,dpi=460")
@Composable
fun ProfileScreenPreview() {
    val navController = rememberNavController()
    ProfileScreen(navController = navController)
}
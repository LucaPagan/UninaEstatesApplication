package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

@Composable
fun SearchFilterScreen(onNavigateBack: () -> Unit = {}) {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
        ) {
            // Header
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                color = colorScheme.primary,
                shape = RoundedCornerShape(bottomStart = 15.dp, bottomEnd = 15.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "SELEZIONA FILTRI",
                        color = colorScheme.onPrimary,
                        style = typography.titleMedium,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Navigation and action buttons
            var selectedPurchaseType by remember { mutableStateOf<String?>(null) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Pulsante indietro
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Indietro",
                        tint = colorScheme.onBackground
                    )
                }

                // Box centrale per i pulsanti Compra/Affitta
                Box(
                    modifier = Modifier
                        .padding(start = 26.dp)
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .wrapContentWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(colorScheme.onBackground.copy(alpha = 0.1f))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf("Compra", "Affitta").forEach { option ->
                            val selected = option == selectedPurchaseType
                            Button(
                                onClick = {
                                    selectedPurchaseType = if (selectedPurchaseType == option) null else option
                                },
                                modifier = Modifier
                                    .height(36.dp)
                                    .padding(horizontal = 2.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selected) colorScheme.primary else colorScheme.surface,
                                    contentColor = if (selected) colorScheme.onPrimary else colorScheme.onSurface
                                ),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = option,
                                    style = typography.labelMedium
                                )
                            }
                        }
                    }
                }

                // Pulsante Cancella
                TextButton(onClick = { /* Reset filters */ }) {
                    Text(
                        text = "Cancella",
                        color = colorScheme.primary,
                        style = typography.labelMedium
                    )
                }
            }

            // Scrollable filter content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = true)
            ) {
                // State per i vari filtri
                var selectedBathrooms by remember { mutableStateOf<Int?>(null) }
                var selectedCondition by remember { mutableStateOf<String?>(null) }

                // Contenuto scrollabile per i filtri
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    // Range di prezzo
                    FilterSection(title = "Prezzo", typography = typography, colorScheme = colorScheme) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                InputField(
                                    label = "Minimo",
                                    modifier = Modifier.fillMaxWidth(),
                                    colorScheme = colorScheme,
                                    typography = typography
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                InputField(
                                    label = "Massimo",
                                    modifier = Modifier.fillMaxWidth(),
                                    colorScheme = colorScheme,
                                    typography = typography
                                )
                            }
                        }
                    }

                    // Range di superficie
                    FilterSection(title = "Superficie (mq)", typography = typography, colorScheme = colorScheme) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                InputField(
                                    label = "Minimo",
                                    modifier = Modifier.fillMaxWidth(),
                                    colorScheme = colorScheme,
                                    typography = typography
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                InputField(
                                    label = "Massimo",
                                    modifier = Modifier.fillMaxWidth(),
                                    colorScheme = colorScheme,
                                    typography = typography
                                )
                            }
                        }
                    }

                    // Range di locali
                    FilterSection(title = "Locali", typography = typography, colorScheme = colorScheme) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                InputField(
                                    label = "Minimo",
                                    modifier = Modifier.fillMaxWidth(),
                                    colorScheme = colorScheme,
                                    typography = typography
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                InputField(
                                    label = "Massimo",
                                    modifier = Modifier.fillMaxWidth(),
                                    colorScheme = colorScheme,
                                    typography = typography
                                )
                            }
                        }
                    }

                    // Selezione dei bagni
                    FilterSection(title = "Bagni", typography = typography, colorScheme = colorScheme) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(1, 2, 3, ">3").forEach { count ->
                                val isSelected = when {
                                    count is Int && selectedBathrooms == count -> true
                                    count is String && selectedBathrooms != null && selectedBathrooms!! > 3 && count == ">3" -> true
                                    else -> false
                                }

                                Box(modifier = Modifier.weight(1f)) {
                                    Button(
                                        onClick = {
                                            selectedBathrooms = if (selectedBathrooms == (if (count is String) 4 else count as Int)) {
                                                null // Deseleziona se già selezionato
                                            } else {
                                                if (count is String) 4 else count as Int
                                            }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSelected) colorScheme.secondary else colorScheme.secondary.copy(alpha = 0.5f)
                                        ),
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text(
                                            text = count.toString(),
                                            color = colorScheme.onSecondary,
                                            style = typography.labelMedium
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Stato immobile - layout migliorato
                    FilterSection(title = "Stato immobile", typography = typography, colorScheme = colorScheme) {
                        // Grid layout organizzato con 2 elementi per riga
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Prima riga: 2 elementi
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Nuovo
                                Box(modifier = Modifier.weight(1f)) {
                                    Button(
                                        onClick = { selectedCondition = if (selectedCondition == "Nuovo") null else "Nuovo" },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectedCondition == "Nuovo") colorScheme.secondary else colorScheme.secondary.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Text(
                                            text = "Nuovo",
                                            color = colorScheme.onSecondary,
                                            style = typography.labelMedium
                                        )
                                    }
                                }

                                // Ottimo
                                Box(modifier = Modifier.weight(1f)) {
                                    Button(
                                        onClick = { selectedCondition = if (selectedCondition == "Ottimo") null else "Ottimo" },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectedCondition == "Ottimo") colorScheme.secondary else colorScheme.secondary.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Text(
                                            text = "Ottimo",
                                            color = colorScheme.onSecondary,
                                            style = typography.labelMedium
                                        )
                                    }
                                }
                            }

                            // Seconda riga: 2 elementi
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Buono
                                Box(modifier = Modifier.weight(1f)) {
                                    Button(
                                        onClick = { selectedCondition = if (selectedCondition == "Buono") null else "Buono" },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectedCondition == "Buono") colorScheme.secondary else colorScheme.secondary.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Text(
                                            text = "Buono",
                                            color = colorScheme.onSecondary,
                                            style = typography.labelMedium
                                        )
                                    }
                                }

                                // Da ristrutturare
                                Box(modifier = Modifier.weight(1f)) {
                                    Button(
                                        onClick = { selectedCondition = if (selectedCondition == "Da ristrutturare") null else "Da ristrutturare" },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectedCondition == "Da ristrutturare") colorScheme.secondary else colorScheme.secondary.copy(alpha = 0.5f)
                                        )
                                    ) {
                                        Text(
                                            text = "Da ristrutturare",
                                            color = colorScheme.onSecondary,
                                            style = typography.labelMedium,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Spazio aggiuntivo per eventuali altri filtri
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Search button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { /* TODO: Applica filtri e esegui la ricerca */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorScheme.primary
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Cerca",
                            tint = colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cerca",
                            color = colorScheme.onPrimary,
                            style = typography.labelLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterSection(
    title: String,
    typography: Typography,
    colorScheme: ColorScheme,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            style = typography.bodyLarge.copy(fontWeight = typography.titleSmall.fontWeight),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
fun InputField(
    label: String,
    modifier: Modifier = Modifier,
    colorScheme: ColorScheme,
    typography: Typography
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(colorScheme.secondary.copy(alpha = 0.5f))
            .height(48.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 4.dp),
            style = typography.labelSmall,
            color = colorScheme.onSurface.copy(alpha = 0.7f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFilterScreen() {
    SearchFilterScreen()
}
package com.dieti.dietiestates25.ui.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieti.dietiestates25.ui.theme.TealPrimary
import com.dieti.dietiestates25.ui.theme.TealLightest
import com.dieti.dietiestates25.ui.theme.White

@Composable
fun FilterScreen(onNavigateBack: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        // Header
        FilterHeader()

        // Navigation and action buttons
        NavigationButtons(onBackClick = onNavigateBack)

        // Scrollable filter content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = true)
        ) {
            FilterContent()
        }

        // Search button
        SearchButton()
    }
}

@Composable
fun FilterHeader() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        color = TealPrimary,
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
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun NavigationButtons(onBackClick: () -> Unit) {
    var selectedPurchaseType by remember { mutableStateOf("Compra") }

    // Una singola riga che contiene tutti gli elementi
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pulsante indietro
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Indietro"
            )
        }

        // Box centrale per i pulsanti Compra/Affitta, centrati orizzontalmente
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(start = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.LightGray.copy(alpha = 0.3f))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("Compra", "Affitta").forEach { option ->
                    val selected = option == selectedPurchaseType
                    Button(
                        onClick = { selectedPurchaseType = option },
                        modifier = Modifier
                            .height(36.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selected) TealPrimary else Color.Transparent,
                            contentColor = if (selected) White else Color.Black
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(option)
                    }
                }
            }
        }

        // Pulsante Cancella
        TextButton(onClick = { /* Reset filters */ }) {
            Text(
                text = "Cancella",
                color = Color.Blue
            )
        }
    }
}

@Composable
fun FilterContent() {
    // State per i vari filtri
    var selectedBathrooms by remember { mutableStateOf(1) }
    val propertyConditions = listOf("Ottimo", "Buono", "Da ristrutturare")
    var selectedCondition by remember { mutableStateOf<String?>("Nuovo") }

    // Contenuto scrollabile per i filtri
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        // Range di prezzo
        FilterSection(title = "Prezzo") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    InputField(
                        label = "Minimo",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    InputField(
                        label = "Massimo",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Range di superficie
        FilterSection(title = "Superficie (mq)") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    InputField(
                        label = "Minimo",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    InputField(
                        label = "Massimo",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Range di locali
        FilterSection(title = "Locali") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    InputField(
                        label = "Minimo",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Box(modifier = Modifier.weight(1f)) {
                    InputField(
                        label = "Massimo",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Selezione dei bagni
        FilterSection(title = "Bagni") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(1, 2, 3, ">3").forEach { count ->
                    val isSelected = when {
                        count is Int && selectedBathrooms == count -> true
                        count is String && selectedBathrooms > 3 && count == ">3" -> true
                        else -> false
                    }

                    Box(modifier = Modifier.weight(1f)) {
                        Button(
                            onClick = {
                                selectedBathrooms = if (count is String) 4 else count as Int
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSelected) TealLightest else TealLightest.copy(alpha = 0.5f)
                            ),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                text = count.toString(),
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }

        // Stato immobile
        FilterSection(title = "Stato immobile") {
            // Opzione "Nuovo"
            Button(
                onClick = { selectedCondition = if (selectedCondition == "Nuovo") null else "Nuovo" },
                modifier = Modifier
                    .width(150.dp)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedCondition == "Nuovo") TealLightest else TealLightest.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = "Nuovo",
                    color = Color.Black
                )
            }

            // Altre condizioni
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                propertyConditions.forEach { condition ->
                    Box(modifier = Modifier.weight(1f)) {
                        Button(
                            onClick = { selectedCondition = if (selectedCondition == condition) null else condition },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedCondition == condition) TealLightest else TealLightest.copy(alpha = 0.5f)
                            )
                        ) {
                            Text(
                                text = condition,
                                color = Color.Black,
                                fontSize = 12.sp,
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

@Composable
fun FilterSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            text = title,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@Composable
fun InputField(
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(TealLightest.copy(alpha = 0.5f))
            .height(48.dp)
    ) {
        Text(
            text = label,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 4.dp),
            fontSize = 10.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun SearchButton() {
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
                containerColor = TealPrimary
            )
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Cerca",
                    tint = White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Cerca",
                    color = White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Esempio di inclusione di FilterScreen in un'app con navigazione
@Composable
fun ExampleAppWithNavigation() {
    var currentScreen by remember { mutableStateOf("search") }

    when (currentScreen) {
        "search" -> {
            // Qui potresti chiamare il tuo SearchScreen
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Button(onClick = { currentScreen = "filter" }) {
                    Text("Vai ai filtri")
                }
            }
        }
        "filter" -> FilterScreen(onNavigateBack = { currentScreen = "search" })
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFilterScreen() {
    FilterScreen()
}

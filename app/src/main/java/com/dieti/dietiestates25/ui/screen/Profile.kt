package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dieti.dietiestates25.R
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color


@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            Spacer(modifier = Modifier.weight(1f))
            Text("Modifica Profilo", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.Share, contentDescription = "Share")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Immagine profilo
        Image(
            painter = painterResource(id = R.drawable.profilo),
            contentDescription = "Profile",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
        )

        Text(
            text = "Dati personali",
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        var name by remember { mutableStateOf("Lorenzo") }
        var email by remember { mutableStateOf("LorenzoTrignano@gmail.com") }
        var phone by remember { mutableStateOf("+39 123456789" ) }

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nome Utente") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email Id") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Numero di telefono") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        )

        Text("Altro", fontWeight = FontWeight.Medium, modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp))

        ProfileOptionRow("Controlla immobili")
        ProfileOptionRow("Controlla richieste agenzia")

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* update logic */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00695C)),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)

        ) {
            Text("Aggiorna")
        }

        Spacer(modifier = Modifier.height(32.dp))

        BottomNavigationBar()
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

//Qui dobbiamo decidere che colore mettere quando si seleziona qualosa nella navigation bar, per ora qui ho messo tutti uguali, con la possibilità di evidenziare
@Composable
fun BottomNavigationBar() {
    NavigationBar (
        containerColor = Color(0xFF00695C)
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Explore, contentDescription = null) },
            label = { Text("Esplora") },
            selected = false,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.LightGray,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.LightGray,
                indicatorColor = Color.Transparent

            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Notifications, contentDescription = null) },
            label = { Text("Notifiche") },
            selected = false,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.LightGray,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.LightGray,
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profilo") },
            selected = true,
            onClick = {},
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.LightGray,
                selectedTextColor = Color.White,
                unselectedTextColor = Color.LightGray,
                indicatorColor = Color.Transparent
            )
        )
    }

}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}

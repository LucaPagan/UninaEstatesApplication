package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.model.FilterModel
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.utils.capitalizeFirstLetter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchTypeSelectionScreen(
    navController: NavController,
    idUtente: String,
    comune: String,
    ricerca: String,
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val dimensions = Dimensions

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    val initialFiltersFromNav = remember(currentBackStackEntry) {
        currentBackStackEntry?.arguments?.let { args ->
            val pType = args.getString("purchaseType")
            val mPrice = args.getFloat("minPrice").takeIf { it != -1f && it != 0f }
            val mxPrice = args.getFloat("maxPrice").takeIf { it != -1f }
            val mSurf = args.getFloat("minSurface").takeIf { it != -1f && it != 0f }
            val mxSurf = args.getFloat("maxSurface").takeIf { it != -1f }
            val mRooms = args.getInt("minRooms").takeIf { it != -1 }
            val mxRooms = args.getInt("maxRooms").takeIf { it != -1 }
            val baths = args.getInt("bathrooms").takeIf { it != -1 }
            val cond = args.getString("condition")

            if (pType != null || mPrice != null || mxPrice != null || mSurf != null || mxSurf != null || mRooms != null || mxRooms != null || baths != null || cond != null) {
                FilterModel(
                    purchaseType = pType, minPrice = mPrice, maxPrice = mxPrice,
                    minSurface = mSurf, maxSurface = mxSurf, minRooms = mRooms,
                    maxRooms = mxRooms, bathrooms = baths, condition = cond
                )
            } else { null }
        }
    }

    val gradientColors = listOf(
        colorScheme.primary.copy(alpha = 0.7f),
        colorScheme.background,
        colorScheme.background,
        colorScheme.primary.copy(alpha = 0.6f)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Scegli Visualizzazione",
                        style = typography.titleLarge, // Titolo più grande per la schermata
                        color = colorScheme.onPrimary
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Indietro",
                            tint = colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorScheme.primary)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Brush.verticalGradient(colors = gradientColors))
                .padding(dimensions.paddingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Centra i bottoni verticalmente
        ) {
            Text(
                text = "Risultati per: ${comune.capitalizeFirstLetter()}" +
                        if (ricerca.isNotBlank() && ricerca.lowercase() != comune.lowercase()) {
                            " - ${ricerca.capitalizeFirstLetter()}"
                        } else {
                            ""
                        },
                style = typography.headlineSmall,
                color = colorScheme.onBackground,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = dimensions.spacingExtraLarge)
            )

            AppPrimaryButton(
                text = "Visualizza come Lista",
                onClick = {
                    navController.navigate(
                        Screen.ApartmentListingScreen.buildRoute(
                            idUtentePath = idUtente,
                            comunePath = comune,
                            ricercaPath = ricerca, // Passa la query originale
                            filters = initialFiltersFromNav
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.AutoMirrored.Filled.ListAlt
            )

            Spacer(modifier = Modifier.height(dimensions.spacingMedium))

            AppPrimaryButton(
                text = "Visualizza su Mappa",
                onClick = {
                    navController.navigate(
                        Screen.MapSearchScreen.buildRoute(idUtente, comune, ricerca, initialFiltersFromNav)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Filled.Map
            )

            Spacer(modifier = Modifier.height(dimensions.spacingMedium))
            AppPrimaryButton(
                text = "Visualizza per Stazioni Metro",
                onClick = {
            //       // TODO: Naviga alla schermata di ricerca per metro con i filtri
            //       // navController.navigate(Screen.MetroSearchScreen.buildRoute(idUtente, comune, ricercaQueryText, appliedFilters))
            },
            modifier = Modifier.fillMaxWidth(),
            icon = Icons.Filled.Train
            )
        }
    }


}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp,dpi=420")
@Composable
fun PreviewSearchTypeSelectionScreen() {
    DietiEstatesTheme {
        SearchTypeSelectionScreen(
            navController = rememberNavController(),
            idUtente = "previewUser",
            comune = "Napoli",
            ricerca = "Centro",
        )
    }
}
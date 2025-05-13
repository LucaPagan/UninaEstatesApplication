package com.dieti.dietiestates25.ui.screen

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
// import androidx.compose.ui.res.stringResource // Import if you use stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.R
import com.dieti.dietiestates25.ui.AppIconDisplay
import com.dieti.dietiestates25.ui.AppSecondaryButton
import com.dieti.dietiestates25.ui.ClickableSearchBar
import com.dieti.dietiestates25.ui.TitledSection
import com.dieti.dietiestates25.ui.components.AppBottomNavigation
import com.dieti.dietiestates25.ui.navigation.Screen
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

// --- Dimensions for HomeScreen ---
private val HeaderClipBottomRadius_Home = 24.dp
private val HeaderPaddingHorizontal_Home = 24.dp
private val HeaderPaddingTop_Home = 40.dp
private val HeaderPaddingBottom_Home = 24.dp
private val HeaderIconSize_Home = 60.dp // Parameter for AppIconDisplay
private val HeaderIconShapeRadius_Home = 16.dp // Parameter for AppIconDisplay
private val HeaderIconTextSpacing_Home = 16.dp

private val MainContentSpacerHeight_Home = 32.dp

// SearchBar dimensions are now mostly within ClickableSearchBar or its parameters
// private val SearchBarOuterPaddingHorizontal_Home = 24.dp // Used by ClickableSearchBar default

private val SectionPaddingVertical_Home = 16.dp
// SectionTitlePaddingHorizontal_Home now DefaultSectionTitlePaddingHorizontal from TitledSection
// SectionTitleContentSpacing_Home now DefaultSectionTitleContentSpacing from TitledSection

private val HorizontalListPadding_Home = 24.dp
private val HorizontalListItemSpacing_Home = 16.dp

private val PropertyCardWidth_Home = 260.dp
private val PropertyCardHeight_Home = 200.dp
private val PropertyCardShapeRadius_Home = 12.dp
private val PropertyCardElevation_Home = 4.dp
private val PropertyCardImageHeight_Home = 140.dp
private val PropertyCardGradientOverlayHeight_Home = 60.dp
private val PropertyCardTextPadding_Home = 12.dp

private val PostAdSectionOuterPaddingHorizontal_Home = 24.dp
private val PostAdSectionOuterPaddingVertical_Home = 24.dp
private val PostAdTitleSubtitleSpacing_Home = 12.dp
private val PostAdSubtitleButtonSpacing_Home = 24.dp
// PostAdButtonHeight and ShapeRadius now part of AppSecondaryButton defaults

// Sample Data for HomeScreen (remains the same)
data class Property(
    val id: Int,
    val price: String,
    val type: String,
    val imageRes: Int,
    val location: String
)

val sampleProperties_Home = listOf( // Renamed to avoid conflict if in same file scope
    Property(1, "400.000 €", "Appartamento", R.drawable.property1, "Napoli"),
    Property(2, "320.000 €", "Villa", R.drawable.property2, "Roma"),
    Property(3, "250.000 €", "Attico", R.drawable.property1, "Milano"),
    Property(4, "180.000 €", "Bilocale", R.drawable.property2, "Torino")
)

@Composable
fun HomeScreen(navController: NavController, idUtente: String = "sconosciuto") {
    DietiEstatesTheme {
        val colorScheme = MaterialTheme.colorScheme
        // val typography = MaterialTheme.typography // Retrieve if needed directly

        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                val window = (view.context as Activity).window
                val primaryColorArgb = colorScheme.primary.toArgb()
                window.statusBarColor = primaryColorArgb
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                window.navigationBarColor = primaryColorArgb
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }

        val gradientColors = listOf(
            colorScheme.primary.copy(alpha = 0.7f),
            colorScheme.background,
            colorScheme.background,
            colorScheme.primary.copy(alpha = 0.6f)
        )

        Scaffold(
            bottomBar = {
                AppBottomNavigation(navController = navController, idUtente = idUtente)
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(colors = gradientColors))
                    .padding(paddingValues)
                    .statusBarsPadding()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    HomeScreenHeader(idUtente = idUtente) // Renamed to avoid conflict if Header is too generic
                    Spacer(modifier = Modifier.height(MainContentSpacerHeight_Home))

                    ClickableSearchBar(
                        // placeholderText = stringResource(R.string.home_search_placeholder),
                        placeholderText = "Cerca comune, zona...",
                        onClick = { navController.navigate(Screen.SearchScreen.withArgs(idUtente)) }
                        // Modifier can be passed if specific layout needs beyond default padding are required
                    )

                    Spacer(modifier = Modifier.height(MainContentSpacerHeight_Home))

                    HomeScreenRecentSearchesSection( // Renamed
                        navController = navController,
                        idUtente = idUtente,
                        properties = sampleProperties_Home
                    )

                    Spacer(modifier = Modifier.height(MainContentSpacerHeight_Home))

                    HomeScreenPostAdSection(navController = navController, idUtente = idUtente) // Renamed
                    Spacer(modifier = Modifier.height(MainContentSpacerHeight_Home))
                }
            }
        }
    }
}

@Composable
fun HomeScreenHeader(idUtente: String) { // Renamed from Header
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(colorScheme.primary)
            .clip(RoundedCornerShape(bottomStart = HeaderClipBottomRadius_Home, bottomEnd = HeaderClipBottomRadius_Home))
            .padding(horizontal = HeaderPaddingHorizontal_Home)
            .padding(top = HeaderPaddingTop_Home, bottom = HeaderPaddingBottom_Home),
        contentAlignment = Alignment.BottomStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            AppIconDisplay(
                size = HeaderIconSize_Home,
                shapeRadius = HeaderIconShapeRadius_Home
                // Other AppIconDisplay params use defaults or can be specified
            )

            Spacer(modifier = Modifier.width(HeaderIconTextSpacing_Home))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    // text = stringResource(R.string.home_welcome_back),
                    text = "Bentornato",
                    color = colorScheme.onPrimary.copy(alpha = 0.8f),
                    style = typography.titleSmall
                )
                Text(
                    text = idUtente.ifEmpty { "Utente" /* stringResource(R.string.default_user) */ },
                    color = colorScheme.onPrimary,
                    style = typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Composable
fun HomeScreenRecentSearchesSection( // Renamed
    navController: NavController,
    idUtente: String,
    properties: List<Property>
) {
    TitledSection(
        // title = stringResource(R.string.recent_searches_title),
        title = "Ultime ricerche",
        modifier = Modifier.padding(vertical = SectionPaddingVertical_Home),
        onSeeAllClick = {
            // TODO: Implement navigation to a dedicated recent searches screen.
            // navController.navigate(Screen.RecentSearchesScreen.withArgs(idUtente))
        }
        // seeAllText = stringResource(R.string.see_all_button), // Default in TitledSection
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = HorizontalListPadding_Home),
            horizontalArrangement = Arrangement.spacedBy(HorizontalListItemSpacing_Home)
        ) {
            items(properties) { property ->
                PropertyCard_Home( // Renamed
                    property = property,
                    navController = navController,
                    modifier = Modifier.width(PropertyCardWidth_Home)
                )
            }
        }
    }
}

@Composable
fun PropertyCard_Home( // Renamed
    property: Property,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme

    Card(
        modifier = modifier
            .height(PropertyCardHeight_Home)
            .clickable {
                // TODO: Navigate to specific property details screen, passing property.id
                // navController.navigate(Screen.PropertyScreen.withId(property.id))
                navController.navigate(Screen.PropertyScreen.route) // Generic placeholder
            },
        shape = RoundedCornerShape(PropertyCardShapeRadius_Home),
        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = PropertyCardElevation_Home)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = property.imageRes),
                // contentDescription = stringResource(R.string.property_image_description, property.type),
                contentDescription = "Property Image: ${property.type}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(PropertyCardImageHeight_Home)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(PropertyCardGradientOverlayHeight_Home)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(PropertyCardTextPadding_Home)
            ) {
                Text(
                    text = property.price,
                    color = Color.White,
                    style = typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = property.type,
                    color = Color.White.copy(alpha = 0.9f),
                    style = typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (property.location.isNotEmpty()) {
                    Text(
                        text = property.location,
                        color = Color.White.copy(alpha = 0.7f),
                        style = typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreenPostAdSection(navController: NavController, idUtente: String) { // Renamed
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = PostAdSectionOuterPaddingHorizontal_Home)
            .padding(vertical = PostAdSectionOuterPaddingVertical_Home),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            // text = stringResource(R.string.post_ad_title),
            text = "Vuoi vendere o affittare il tuo immobile?",
            color = colorScheme.onBackground,
            style = typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(PostAdTitleSubtitleSpacing_Home))
        Text(
            // text = stringResource(R.string.post_ad_subtitle),
            text = "Inserisci il tuo annuncio in pochi semplici passaggi.",
            color = colorScheme.onBackground.copy(alpha = 0.8f),
            style = typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(PostAdSubtitleButtonSpacing_Home))
        AppSecondaryButton(
            // text = stringResource(R.string.post_ad_button),
            text = "Pubblica annuncio",
            onClick = {
                navController.navigate(Screen.PropertySellScreen.withArgs(idUtente))
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview(showBackground = true, device = "id:pixel_4")
@Composable
fun PreviewHomeScreen() {
    DietiEstatesTheme {
        val navController = rememberNavController()
        HomeScreen(navController = navController, idUtente = "Danilo")
    }
}
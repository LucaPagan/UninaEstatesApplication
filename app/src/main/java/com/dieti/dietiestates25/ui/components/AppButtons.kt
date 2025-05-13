package com.dieti.dietiestates25.ui // Assuming a base package

// Common Imports (organize as needed if splitting into multiple files)
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
// import androidx.compose.ui.res.stringResource // Uncomment if you use stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.R // Assuming R file is in this base package for now
import com.dieti.dietiestates25.ui.navigation.Screen // Assuming Screen class location
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme

// Reusable Component Dimensions (can be in a central Dimens.kt)
private val DefaultButtonHeight = 56.dp
private val PrimaryButtonShapeRadius = 12.dp
private val SecondaryButtonShapeRadius = 8.dp
private val DefaultButtonElevation = 8.dp
private val PressedButtonElevation = 4.dp
private val SecondaryButtonElevation = 4.dp
private val SecondaryPressedButtonElevation = 2.dp

private val DefaultAppIconSize = 60.dp
private val DefaultAppIconShapeRadius = 16.dp
private val DefaultAppIconInternalPadding = 8.dp
private val DefaultAppIconImageClipRadius = 8.dp
private val DefaultAppIconElevation = 4.dp

private val ClickableSearchBarPaddingHorizontal = 24.dp
private val ClickableSearchBarShapeRadius = 28.dp
private val ClickableSearchBarInternalPaddingHorizontal = 16.dp
private val ClickableSearchBarInternalPaddingVertical = 12.dp
private val ClickableSearchBarIconTextSpacing = 8.dp

private val DefaultSectionTitlePaddingHorizontal = 24.dp
private val DefaultSectionTitleContentSpacing = 12.dp


// =============================================================================================
// REUSABLE COMPONENTS (Conceptually in com.dieti.dietiestates25.ui.components)
// =============================================================================================

@Composable
fun AppPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleMedium // Allow style override
) {
    val colorScheme = MaterialTheme.colorScheme
    Button(
        onClick = onClick,
        modifier = modifier.height(DefaultButtonHeight),
        enabled = enabled,
        shape = RoundedCornerShape(PrimaryButtonShapeRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary,
            disabledContainerColor = colorScheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = DefaultButtonElevation,
            pressedElevation = PressedButtonElevation
        )
    ) {
        Text(text, style = textStyle)
    }
}

@Composable
fun AppSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.labelLarge // Allow style override
) {
    val colorScheme = MaterialTheme.colorScheme
    Button(
        onClick = onClick,
        modifier = modifier.height(DefaultButtonHeight),
        enabled = enabled,
        shape = RoundedCornerShape(SecondaryButtonShapeRadius),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.secondary,
            contentColor = colorScheme.onSecondary,
            disabledContainerColor = colorScheme.onSurface.copy(alpha = 0.12f),
            disabledContentColor = colorScheme.onSurface.copy(alpha = 0.38f)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = SecondaryButtonElevation,
            pressedElevation = SecondaryPressedButtonElevation
        )
    ) {
        Text(text, style = textStyle)
    }
}

@Composable
fun AppIconDisplay(
    modifier: Modifier = Modifier,
    iconResId: Int = R.drawable.appicon1, // Default app icon
    // contentDescription: String = stringResource(R.string.app_icon_description), // Use string resource
    contentDescription: String = "App Icon",
    size: Dp = DefaultAppIconSize,
    shapeRadius: Dp = DefaultAppIconShapeRadius,
    internalPadding: Dp = DefaultAppIconInternalPadding,
    imageClipRadius: Dp = DefaultAppIconImageClipRadius,
    elevation: Dp = DefaultAppIconElevation
) {
    Surface(
        modifier = modifier.size(size),
        shape = RoundedCornerShape(shapeRadius),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = elevation
    ) {
        Box(
            modifier = Modifier
                .padding(internalPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = iconResId),
                contentDescription = contentDescription,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(imageClipRadius)),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun ClickableSearchBar(
    // placeholderText: String = stringResource(R.string.search_placeholder_default),
    placeholderText: String = "Cerca...",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ClickableSearchBarPaddingHorizontal)
            .clip(RoundedCornerShape(ClickableSearchBarShapeRadius))
            .background(colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(
                horizontal = ClickableSearchBarInternalPaddingHorizontal,
                vertical = ClickableSearchBarInternalPaddingVertical
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ClickableSearchBarIconTextSpacing)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = placeholderText, // Or dedicated description
                tint = colorScheme.onSurface.copy(alpha = 0.7f)
            )
            Text(
                text = placeholderText,
                color = colorScheme.onSurface.copy(alpha = 0.7f),
                style = typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TitledSection(
    title: String,
    modifier: Modifier = Modifier,
    onSeeAllClick: (() -> Unit)? = null,
    // seeAllText: String = stringResource(R.string.see_all),
    seeAllText: String = "Vedi tutte",
    titleStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleMedium,
    seeAllTextStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.labelLarge,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: @Composable () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .padding(horizontal = DefaultSectionTitlePaddingHorizontal) // Use constant
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = colorScheme.onBackground,
                style = titleStyle,
                fontWeight = FontWeight.SemiBold // Common styling for titles
            )
            onSeeAllClick?.let { onClick ->
                TextButton(
                    onClick = onClick,
                    colors = ButtonDefaults.textButtonColors(contentColor = colorScheme.primary)
                ) {
                    Text(seeAllText, style = seeAllTextStyle)
                }
            }
        }
        Spacer(modifier = Modifier.height(DefaultSectionTitleContentSpacing)) // Use constant
        Box(modifier = Modifier.padding(contentPadding)) {
            content()
        }
    }
}
package com.dieti.dietiestates25.ui.features.notification

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.dieti.dietiestates25.data.model.NotificationDetail
import com.dieti.dietiestates25.data.model.NotificationIconType
import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppRedButton
import com.dieti.dietiestates25.ui.components.CircularIconActionButton
import com.dieti.dietiestates25.ui.components.GeneralHeaderBar
import com.dieti.dietiestates25.ui.theme.Dimensions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    navController: NavController,
    notificationId: String?,
    onToggleMasterFavorite: (String) -> Unit,
    viewModel: NotificationDetailViewModel = viewModel()
) {
    val context = LocalContext.current
    val notificationDetail by viewModel.currentNotification.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val formattedMessage = remember(notificationDetail) {
        viewModel.getFormattedMessage(notificationDetail)
    }
    val dimensions = Dimensions
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    LaunchedEffect(notificationId) {
        if (notificationId != null) {
            viewModel.loadNotificationById(notificationId)
        }
    }

    Scaffold(
        topBar = {
            GeneralHeaderBar(
                title = "Dettagli Notifica",
                onBackClick = { navController.popBackStack() },
                actions = {}
            )
            NotificationDetailTopAppBar(
                navController = navController,
                notificationDetail = notificationDetail,
                onToggleFavorite = {
                    notificationDetail?.id?.let { id ->
                        onToggleMasterFavorite(id)
                        viewModel.loadNotificationById(id)
                    }
                },
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colorScheme.primary)
            }
        } else if (notificationDetail != null) {
            NotificationDetailContent(
                modifier = Modifier.padding(paddingValues),
                notificationDetail = notificationDetail!!,
                formattedMessage = formattedMessage,
                onAccept = { viewModel.acceptProposal(context) },
                onReject = { viewModel.rejectProposal(context) },
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (notificationId == null) "ID notifica mancante." else "Dettagli notifica non disponibili.",
                    style = typography.bodyLarge,
                    color = colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationDetailTopAppBar(
    navController: NavController,
    notificationDetail: NotificationDetail?,
    onToggleFavorite: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(colorScheme.primaryContainer)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.primary)
                .padding(
                    horizontal = dimensions.paddingMedium,
                    vertical = dimensions.paddingMedium
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                CircularIconActionButton(
                    onClick = { navController.popBackStack() },
                    iconVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro",
                    backgroundColor = colorScheme.primaryContainer,
                    iconTint = colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.width(dimensions.spacingSmall))
                Text(
                    text = notificationDetail?.senderType ?: "Dettaglio Notifica",
                    style = typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            notificationDetail?.let { detail ->
                CircularIconActionButton(
                    onClick = onToggleFavorite,
                    iconVector = if (detail.isFavorite) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = if (detail.isFavorite) "Rimuovi dai preferiti" else "Aggiungi ai preferiti",
                    backgroundColor = colorScheme.primaryContainer,
                    iconTint = if (detail.isFavorite) colorScheme.tertiary else colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun NotificationDetailContent(
    modifier: Modifier = Modifier,
    notificationDetail: NotificationDetail,
    formattedMessage: String,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorScheme.primary)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(dimensions.paddingMedium),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())) {
                NotificationHeaderCard(
                    senderType = notificationDetail.senderType,
                    senderName = notificationDetail.senderName,
                    iconType = notificationDetail.iconType,
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions
                )
                Spacer(modifier = Modifier.height(dimensions.spacingMedium))
                ProposalDetailsCard(
                    message = formattedMessage,
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions
                )
            }
            if (notificationDetail.isProposal) {
                ActionButtonsSection(
                    onAccept = onAccept,
                    onReject = onReject,
                    modifier = Modifier.padding(top = dimensions.paddingMedium),
                    dimensions = dimensions
                )
            }
        }
    }
}

@Composable
private fun NotificationHeaderCard(
    senderType: String,
    senderName: String,
    iconType: NotificationIconType,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    val icon = when(iconType) {
        NotificationIconType.PHONE -> Icons.Filled.Phone
        NotificationIconType.PERSON -> Icons.Filled.Person
        NotificationIconType.BADGE -> Icons.AutoMirrored.Filled.Comment
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
            .background(colorScheme.surfaceVariant)
            .padding(dimensions.paddingMedium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(dimensions.iconSizeExtraLarge)
                .clip(RoundedCornerShape(dimensions.cornerRadiusExtraSmall))
                .background(colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = "Icona Notifica",
                modifier = Modifier.size(dimensions.iconSizeLarge),
                tint = colorScheme.onPrimaryContainer
            )
        }
        Spacer(modifier = Modifier.width(dimensions.spacingMedium))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = senderType,
                style = typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = colorScheme.onSurfaceVariant
            )
            Text(
                text = senderName,
                style = typography.bodyMedium,
                color = colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun ProposalDetailsCard(
    message: String,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = dimensions.logoLarge)
            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
            .background(colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(dimensions.paddingMedium)
    ) {
        Text(
            text = message,
            style = typography.bodyLarge,
            color = colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ActionButtonsSection(
    onAccept: () -> Unit,
    onReject: () -> Unit,
    modifier: Modifier = Modifier,
    dimensions: Dimensions
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensions.spacingMedium)
    ) {
        AppPrimaryButton(
            onClick = onAccept,
            modifier = Modifier.fillMaxWidth(),
            text = "Accetta Proposta",
        )
        AppRedButton(
            onClick = onReject,
            modifier = Modifier.fillMaxWidth(),
            text = "Rifiuta Proposta"
        )
    }
}

@Preview(showBackground = true, name = "Notification Detail Light")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Notification Detail Dark")
@Composable
fun NotificationDetailScreenPreview() {
    val navController = rememberNavController()
    val previewViewModel = viewModel<NotificationDetailViewModel>()
    // In preview mode loadNotificationById would need to be mocked or fail gracefully
    NotificationDetailScreen(
        navController = navController,
        notificationId = "",
        onToggleMasterFavorite = { },
        viewModel = previewViewModel
    )
}
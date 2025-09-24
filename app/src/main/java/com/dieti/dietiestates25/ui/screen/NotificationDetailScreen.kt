package com.dieti.dietiestates25.ui.screen

import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppRedButton
import com.dieti.dietiestates25.ui.theme.Dimensions
import com.dieti.dietiestates25.ui.model.NotificationDetailViewModel
import com.dieti.dietiestates25.ui.model.NotificationDetail // Importa la data class aggiornata
import com.dieti.dietiestates25.ui.model.NotificationIconType // Importa l'enum aggiornato
import com.dieti.dietiestates25.ui.components.CircularIconActionButton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    navController: NavController,
    notificationId: Int?,
    onToggleMasterFavorite: (Int) -> Unit,
    viewModel: NotificationDetailViewModel = viewModel()
) {
    val notificationDetail by viewModel.currentNotification.collectAsState()
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
            NotificationDetailTopAppBar(
                navController = navController,
                notificationDetail = notificationDetail,
                onToggleFavorite = {
                    notificationDetail?.id?.let { id ->
                        onToggleMasterFavorite(id)
                        // Ricarica per riflettere immediatamente il cambiamento di isFavorite
                        viewModel.loadNotificationById(id)
                    }
                },
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        }
    ) { paddingValues ->
        if (notificationDetail == null && notificationId != null) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (notificationDetail != null) {
            NotificationDetailContent(
                modifier = Modifier.padding(paddingValues),
                notificationDetail = notificationDetail!!, // Sappiamo che non è nullo qui
                formattedMessage = formattedMessage,
                onAccept = viewModel::acceptProposal,
                onReject = viewModel::rejectProposal,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        } else {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues), contentAlignment = Alignment.Center) {
                Text("Dettagli notifica non disponibili.")
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationDetailTopAppBar(
    navController: NavController,
    notificationDetail: NotificationDetail?, // Usa la data class aggiornata
    onToggleFavorite: () -> Unit,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    Column (
        modifier = Modifier.fillMaxWidth()
    ) {
        // Status Bar con colore TealDeep fisso
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(WindowInsets.statusBars)
                .background(colorScheme.primaryContainer)
        )
        // Main content Row for buttons and title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(colorScheme.primary)
                .padding(
                    horizontal = dimensions.paddingMedium, // MODIFICATO: Più padding orizzontale
                    vertical = dimensions.paddingMedium    // MODIFICATO: Leggermente più padding verticale
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween // Pushes elements to sides
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f) // Allows title group to take available
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
                .clip(
                    RoundedCornerShape(
                        topStart = dimensions.cornerRadiusMedium,
                        topEnd = dimensions.cornerRadiusMedium
                    )
                )
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
                    iconType = notificationDetail.iconType, // Passa l'iconType corretto
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions
                )
                Spacer(modifier = Modifier.height(dimensions.spacingMedium))
                ProposalDetailsCard(
                    message = formattedMessage, // Usa il messaggio formattato
                    colorScheme = colorScheme,
                    typography = typography,
                    dimensions = dimensions
                )
            }
            // Mostra i bottoni solo se è una proposta
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
    iconType: NotificationIconType, // Usa l'enum corretto
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
    val icon = when(iconType) {
        NotificationIconType.PHONE -> Icons.Filled.Phone
        NotificationIconType.PERSON -> Icons.Filled.Person
        NotificationIconType.BADGE -> Icons.AutoMirrored.Filled.Comment // Esempio per badge
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
@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, name = "Notification Detail Dark")
@Composable
fun NotificationDetailScreenPreview() {
    val navController = rememberNavController()
    val previewViewModel = viewModel<NotificationDetailViewModel>()
    LaunchedEffect(Unit) {
        previewViewModel.loadNotificationById(1)
    }

    NotificationDetailScreen(
        navController = navController,
        notificationId = 1,
        onToggleMasterFavorite = { /* no-op in preview */ },
        viewModel = previewViewModel
    )
}
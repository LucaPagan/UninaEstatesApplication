package com.dieti.dietiestates25.ui.screen

import com.dieti.dietiestates25.ui.components.AppPrimaryButton
import com.dieti.dietiestates25.ui.components.AppRedButton
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import com.dieti.dietiestates25.ui.theme.Dimensions // Importa il tuo oggetto
import com.dieti.dietiestates25.ui.model.NotificationDetailViewModel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp // Mantieni per valori hardcoded non in Dimensions
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    navController: NavController,
    viewModel: NotificationDetailViewModel = viewModel() // viewModel ora importato da ui.model
) {
    DietiEstatesTheme { // DietiEstatesTheme applica già MaterialTheme internamente
        val notificationDetail by viewModel.currentNotification.collectAsState()
        val formattedMessage = remember(notificationDetail) {
            viewModel.getFormattedMessage(notificationDetail)
        }
        val dimensions = Dimensions
        val colorScheme = MaterialTheme.colorScheme // Prendi da MaterialTheme applicato da DietiEstatesTheme
        val typography = MaterialTheme.typography   // Prendi da MaterialTheme applicato da DietiEstatesTheme

        Scaffold(
            topBar = {
                NotificationDetailTopAppBar(
                    navController = navController,
                    colorScheme = colorScheme,
                    typography = typography
                )
            }
        ) { paddingValues ->
            NotificationDetailContent(
                modifier = Modifier.padding(paddingValues),
                notificationDetail = notificationDetail,
                formattedMessage = formattedMessage,
                onAccept = viewModel::acceptProposal,
                onReject = viewModel::rejectProposal,
                colorScheme = colorScheme,
                typography = typography,
                dimensions = dimensions
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationDetailTopAppBar(
    navController: NavController,
    colorScheme: ColorScheme,
    typography: Typography
) {
    TopAppBar(
        title = {
            Text(
                text = "Dettaglio Notifica",
                style = typography.titleLarge,
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Indietro"
                )
            }
        },
        actions = {
            IconButton(onClick = { /* TODO: Implementa menu opzioni */ }) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Menu"
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = colorScheme.primary,
            titleContentColor = colorScheme.onPrimary,
            navigationIconContentColor = colorScheme.onPrimary,
            actionIconContentColor = colorScheme.onPrimary
        )
    )
}

@Composable
private fun NotificationDetailContent(
    modifier: Modifier = Modifier,
    // Usa il tipo completo se NotificationDetail è inner class, o solo NotificationDetail se importata
    notificationDetail: NotificationDetailViewModel.NotificationDetail,
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
            .background(colorScheme.primary) // Sfondo primario per l'effetto "notch" superiore
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)) // 20.dp non in Dimensions
                .background(colorScheme.background)
                .padding(dimensions.paddingMedium),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                NotificationHeaderCard(
                    senderType = notificationDetail.senderType,
                    senderName = notificationDetail.senderName,
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
            ActionButtonsSection(
                onAccept = onAccept,
                onReject = onReject,
                modifier = Modifier.padding(top = dimensions.paddingMedium),
                dimensions = dimensions
            )
        }
    }
}

@Composable
private fun NotificationHeaderCard(
    senderType: String,
    senderName: String,
    colorScheme: ColorScheme,
    typography: Typography,
    dimensions: Dimensions
) {
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
                .size(dimensions.bottomNavHeight) // 64.dp
                .clip(RoundedCornerShape(10.dp)) // 10.dp non in Dimensions
                .background(colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Phone,
                contentDescription = "Icona Notifica",
                modifier = Modifier.size(dimensions.iconSizeLarge), // 36.dp
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
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimensions.cornerRadiusMedium))
            .background(colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(
                horizontal = dimensions.paddingMedium,
                vertical = 12.dp // 12.dp non in Dimensions
            )
    ) {
        Text(
            text = message,
            style = typography.bodyLarge,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxSize() // Riempe il Box
                .verticalScroll(scrollState)
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
    NotificationDetailScreen(
        navController = navController,
        viewModel = previewViewModel
    )
}
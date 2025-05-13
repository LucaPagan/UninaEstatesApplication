package com.dieti.dietiestates25.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavController
import com.dieti.dietiestates25.ui.theme.DietiEstatesTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// ViewModel for Notification Details
class NotificationDetailViewModel : ViewModel() {
    // You can add more complex logic here as needed
    data class NotificationDetail(
        val id: Int,
        val senderType: String,
        val senderName: String,
        val message: String,
        val propertyAddress: String,
        val propertyPrice: Double
    )

    // Sample notification detail
    private val _currentNotification = MutableStateFlow(
        NotificationDetail(
            id = 1,
            senderType = "Compratore",
            senderName = "Mario rossi",
            message = "Il venditore Mario rossi ha offerto 120.000 da lei messo in vendita a via Ripuaria 48.",
            propertyAddress = "Via Ripuaria 48",
            propertyPrice = 120000.0
        )
    )
    val currentNotification = _currentNotification.asStateFlow()

    // Methods to handle accept/reject actions
    fun acceptProposal() {
        // Implement accept logic
        // This could involve API calls, database updates, etc.
    }

    fun rejectProposal() {
        // Implement reject logic
        // This could involve API calls, database updates, etc.
    }
}

@Composable
fun NotificationDetailScreen(
    navController: NavController,
    viewModel: NotificationDetailViewModel = viewModel()
) {
    DietiEstatesTheme {

        val notification by viewModel.currentNotification.collectAsState()

        val colorScheme = MaterialTheme.colorScheme
        val typography = MaterialTheme.typography

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.primary)
        ) {
            // Top App Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = colorScheme.onPrimary
                    )
                }

                Text(
                    text = "Notifica",
                    style = typography.titleLarge,
                    color = colorScheme.onPrimary
                )

                IconButton(
                    onClick = { /* Open menu */ },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = colorScheme.onPrimary
                    )
                }
            }

            // Main content
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        colorScheme.background,
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Notification Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(colorScheme.primary)
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(colorScheme.secondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = "Notification Icon",
                                modifier = Modifier.size(32.dp),
                                tint = colorScheme.onSecondary
                            )
                        }

                        // Sender Info
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                text = notification.senderType,
                                style = typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                color = colorScheme.onPrimary
                            )
                        }
                    }

                    // Proposal Details
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(colorScheme.primary)
                            .padding(16.dp)
                    ) {
                        Text(
                            text = notification.message,
                            style = typography.bodyLarge,
                            color = colorScheme.onPrimary
                        )
                    }

                    // Action Buttons
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Accept Button
                        Button(
                            onClick = { viewModel.acceptProposal() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary,
                                contentColor = colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Accetta",
                                style = typography.labelLarge
                            )
                        }

                        // Reject Button
                        Button(
                            onClick = { viewModel.rejectProposal() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.error.copy(alpha = 0.7f),
                                contentColor = colorScheme.onPrimary
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = "Rifiuta",
                                style = typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NotificationDetailScreenPreview() {
    val navController = rememberNavController()
    NotificationDetailScreen(
        navController = navController,
    )
}
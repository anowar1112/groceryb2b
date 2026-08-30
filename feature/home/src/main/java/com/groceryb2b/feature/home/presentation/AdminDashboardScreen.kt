package com.groceryb2b.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: AdminDashboardViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToProductForm: () -> Unit = {},
    onNavigateToPermissionManagement: () -> Unit = {}
) {
    val uiState = viewModel.uiState
    val state = uiState.collectAsState().value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("অ্যাডমিন ড্যাশবোর্ড") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Icon(
                imageVector = Icons.Default.AdminPanelSettings,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (state.isAdmin) "স্বাগতম, অ্যাডমিন!" else "অ্যাক্সেস অস্বীকার করা হয়েছে",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (state.canCreateProduct) {
                AdminActionCard(
                    icon = Icons.Default.Add,
                    label = "নতুন পণ্য যোগ করুন",
                    onClick = onNavigateToProductForm
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (state.canUpdateProduct) {
                AdminActionCard(
                    icon = Icons.Default.Edit,
                    label = "পণ্য আপডেট করুন",
                    onClick = onNavigateToProductForm
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (state.canDeleteProduct) {
                AdminActionCard(
                    icon = Icons.Default.Delete,
                    label = "পণ্য মুছে ফেলুন",
                    onClick = onNavigateToProductForm
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (state.canAccessAdminPanel) {
                AdminActionCard(
                    icon = Icons.AutoMirrored.Filled.ListAlt,
                    label = "অর্ডার ম্যানেজ করুন",
                    onClick = onNavigateToPermissionManagement
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = if (state.canAccessAdminPanel) "ভবিষ্যৎ আপডেটে এখানে আরও ফিচার যোগ করা হবে।" else "আপনার কাছে এই প্যানেলের অনুমতি নেই।",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
private fun AdminActionCard(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = label, style = MaterialTheme.typography.titleMedium)
        }
    }
}

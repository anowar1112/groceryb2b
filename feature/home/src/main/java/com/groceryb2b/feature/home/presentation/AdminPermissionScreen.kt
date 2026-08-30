package com.groceryb2b.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groceryb2b.core.network.PermissionAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPermissionScreen(
    viewModel: AdminPermissionViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Permission Management") },
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Admin Permission Control",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedTextField(
                value = uiState.targetMobile,
                onValueChange = viewModel::updateTargetMobile,
                label = { Text("User Mobile Number") },
                modifier = Modifier.fillMaxWidth()
            )

            PermissionItemCard(
                label = "Product Add",
                action = PermissionAction.PRODUCT_CREATE,
                isGranted = uiState.grantedPermissions.contains(PermissionAction.PRODUCT_CREATE),
                onGrant = { viewModel.grantPermission(PermissionAction.PRODUCT_CREATE) },
                onRevoke = { viewModel.revokePermission(PermissionAction.PRODUCT_CREATE) }
            )
            PermissionItemCard(
                label = "Product Update",
                action = PermissionAction.PRODUCT_UPDATE,
                isGranted = uiState.grantedPermissions.contains(PermissionAction.PRODUCT_UPDATE),
                onGrant = { viewModel.grantPermission(PermissionAction.PRODUCT_UPDATE) },
                onRevoke = { viewModel.revokePermission(PermissionAction.PRODUCT_UPDATE) }
            )
            PermissionItemCard(
                label = "Product Delete",
                action = PermissionAction.PRODUCT_DELETE,
                isGranted = uiState.grantedPermissions.contains(PermissionAction.PRODUCT_DELETE),
                onGrant = { viewModel.grantPermission(PermissionAction.PRODUCT_DELETE) },
                onRevoke = { viewModel.revokePermission(PermissionAction.PRODUCT_DELETE) }
            )
            PermissionItemCard(
                label = "Admin Panel",
                action = PermissionAction.ADMIN_PANEL,
                isGranted = uiState.grantedPermissions.contains(PermissionAction.ADMIN_PANEL),
                onGrant = { viewModel.grantPermission(PermissionAction.ADMIN_PANEL) },
                onRevoke = { viewModel.revokePermission(PermissionAction.ADMIN_PANEL) }
            )
        }
    }
}

@Composable
private fun PermissionItemCard(
    label: String,
    action: PermissionAction,
    isGranted: Boolean,
    onGrant: () -> Unit,
    onRevoke: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (isGranted) "Granted" else "Not granted",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isGranted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row {
                Button(onClick = onGrant) {
                    Text("Grant")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onRevoke) {
                    Text("Revoke")
                }
            }
        }
    }
}

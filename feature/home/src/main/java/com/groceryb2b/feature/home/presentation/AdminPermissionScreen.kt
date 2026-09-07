package com.groceryb2b.feature.home.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("পারমিশন ম্যানেজমেন্ট", fontWeight = FontWeight.Bold) },
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
        ) {
            // Search Section
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = viewModel::updateSearchQuery,
                placeholder = { Text("দোকানের নাম বা মোবাইল নম্বর দিয়ে খুঁজুন") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.shops.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (state.searchQuery.isBlank()) "কোনো অ্যাডমিন পাওয়া যায়নি" else "এই নম্বরে কোনো ইউজার পাওয়া যায়নি",
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.shops, key = { it.shop.id }) { shopWithPermissions ->
                        UserPermissionCard(
                            shopWithPermissions = shopWithPermissions,
                            onTogglePermission = viewModel::togglePermission
                        )
                    }
                    item { Spacer(Modifier.height(40.dp)) }
                }
            }
        }
    }
}

@Composable
private fun UserPermissionCard(
    shopWithPermissions: ShopWithPermissions,
    onTogglePermission: (String, PermissionAction, Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val shop = shopWithPermissions.shop
    val permissions = shopWithPermissions.permissions
    val isSuperAdmin = shop.mobileNumber == "01557775958"

    val cardBackgroundColor = if (isSuperAdmin) {
        // High premium look: subtle navy/darker surface with light primary accent
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colorScheme.surface
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = cardBackgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSuperAdmin) 4.dp else 2.dp),
        shape = RoundedCornerShape(16.dp),
        border = if (isSuperAdmin) BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSuperAdmin) MaterialTheme.colorScheme.primary 
                                else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSuperAdmin) Icons.Default.VerifiedUser else Icons.Default.Store,
                            contentDescription = null,
                            tint = if (isSuperAdmin) Color.White else MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = shop.shopName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (isSuperAdmin) MaterialTheme.colorScheme.primary else Color.Unspecified
                            )
                            if (isSuperAdmin) {
                                Spacer(Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFFEF6C00), // AccentOrange touch
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "সুপার এডমিন",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                        Text(
                            text = shop.mobileNumber,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = if (isSuperAdmin) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(16.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(16.dp))
                    
                    Text(
                        text = "অনুমতিসমূহ (Permissions)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isSuperAdmin) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    PermissionToggleItem(
                        label = "পণ্য যোগ করুন",
                        description = "নতুন পণ্য ক্যাটালগে যুক্ত করার ক্ষমতা",
                        isGranted = permissions.contains(PermissionAction.PRODUCT_CREATE) || isSuperAdmin,
                        enabled = !isSuperAdmin,
                        onToggle = { onTogglePermission(shop.mobileNumber, PermissionAction.PRODUCT_CREATE, it) }
                    )

                    PermissionToggleItem(
                        label = "পণ্য আপডেট",
                        description = "বিদ্যমান পণ্যের তথ্য বা দাম পরিবর্তন",
                        isGranted = permissions.contains(PermissionAction.PRODUCT_UPDATE) || isSuperAdmin,
                        enabled = !isSuperAdmin,
                        onToggle = { onTogglePermission(shop.mobileNumber, PermissionAction.PRODUCT_UPDATE, it) }
                    )

                    PermissionToggleItem(
                        label = "পণ্য মুছে ফেলুন",
                        description = "ক্যাটালগ থেকে পণ্য স্থায়ীভাবে মুছে ফেলা",
                        isGranted = permissions.contains(PermissionAction.PRODUCT_DELETE) || isSuperAdmin,
                        enabled = !isSuperAdmin,
                        onToggle = { onTogglePermission(shop.mobileNumber, PermissionAction.PRODUCT_DELETE, it) }
                    )

                    PermissionToggleItem(
                        label = "অ্যাডমিন প্যানেল অ্যাক্সেস",
                        description = "সম্পূর্ণ ড্যাশবোর্ড এবং পারমিশন কন্ট্রোল",
                        isGranted = permissions.contains(PermissionAction.ADMIN_PANEL) || isSuperAdmin,
                        enabled = !isSuperAdmin,
                        onToggle = { onTogglePermission(shop.mobileNumber, PermissionAction.ADMIN_PANEL, it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PermissionToggleItem(
    label: String,
    description: String,
    isGranted: Boolean,
    enabled: Boolean = true,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
        }
        Switch(
            checked = isGranted,
            onCheckedChange = onToggle,
            enabled = enabled,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                disabledCheckedThumbColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                disabledCheckedTrackColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            )
        )
    }
}

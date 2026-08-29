package com.groceryb2b.feature.home.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.groceryb2b.core.database.order.OrderEntity
import com.groceryb2b.core.database.shop.ShopEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onOrderClick: (Long) -> Unit = {},
    onEditClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("প্রোফাইল") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (uiState) {
            ProfileUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is ProfileUiState.Success -> {
                val success = uiState as ProfileUiState.Success
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        ShopProfileCard(success.shop, onEditClick)
                    }
                    
                    item {
                        Text("অর্ডার ইতিহাস", style = MaterialTheme.typography.headlineSmall)
                    }
                    
                    if (success.orders.isEmpty()) {
                        item {
                            Text("কোনো অর্ডার পাওয়া যায়নি", style = MaterialTheme.typography.bodyMedium)
                        }
                    } else {
                        items(success.orders, key = { it.id }) { order ->
                            OrderHistoryCard(order, onOrderClick)
                        }
                    }
                }
            }
            
            is ProfileUiState.Error -> {
                val error = uiState as ProfileUiState.Error
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("ত্রুটি", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Text(error.message, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
private fun ShopProfileCard(shop: ShopEntity, onEditClick: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onEditClick() }
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("দোকানের তথ্য", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("✎ সম্পাদনা", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(Modifier.height(12.dp))
            
            InfoRow("দোকানের নাম:", shop.shopName)
            InfoRow("মালিকের নাম:", shop.ownerName)
            InfoRow("মোবাইল:", shop.mobileNumber)
            InfoRow("ঠিকানা:", shop.address)
            InfoRow("ডেলিভারি লোকেশন:", shop.deliveryLocation)
            shop.landmark?.takeIf { it.isNotBlank() }?.let { landmark ->
                InfoRow("ল্যান্ডমার্ক:", landmark)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Text(value, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun OrderHistoryCard(order: OrderEntity, onOrderClick: (Long) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { onOrderClick(order.id) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("অর্ডার #${order.id}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("মূল্য: ৳${order.totalPrice}", style = MaterialTheme.typography.bodyMedium)
                Text("স্ট্যাটাস: ${getStatusBn(order.status)}", style = MaterialTheme.typography.labelMedium)
            }
            Text("→", style = MaterialTheme.typography.headlineSmall)
        }
    }
}

private fun getStatusBn(status: String): String = when (status) {
    "PENDING" -> "অপেক্ষমাণ"
    "CONFIRMED" -> "নিশ্চিত"
    "DELIVERED" -> "সরবরাহ করা হয়েছে"
    "CANCELLED" -> "বাতিল"
    else -> status
}

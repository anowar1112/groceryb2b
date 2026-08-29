package com.groceryb2b.feature.home.presentation

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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groceryb2b.core.database.order.OrderItemEntity
import com.groceryb2b.feature.home.data.OrderSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailsScreen(
    orderId: Long,
    viewModel: OrderDetailsViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(orderId) {
        viewModel.loadOrder(orderId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("অর্ডার বিস্তারিত") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (uiState) {
            OrderDetailsUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is OrderDetailsUiState.Success -> {
                val success = uiState as OrderDetailsUiState.Success
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        OrderSummaryCard(success.order)
                    }
                    
                    item {
                        Text("পণ্যসমূহ", style = MaterialTheme.typography.headlineSmall)
                    }
                    
                    items(success.order.items, key = { it.productId }) { item ->
                        OrderItemCard(item)
                    }
                    
                    item {
                        TotalPriceCard(success.order.totalPrice)
                    }
                }
            }
            
            is OrderDetailsUiState.Error -> {
                val error = uiState as OrderDetailsUiState.Error
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
private fun OrderSummaryCard(order: OrderSummary) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("অর্ডার নম্বর: ${order.id}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Text("স্ট্যাটাস: ${getStatusBn(order.status)}", style = MaterialTheme.typography.labelMedium)
                }
                Text(getStatusColor(order.status), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
            
            Spacer(Modifier.height(8.dp))
            Text("তারিখ: ${formatDate(order.createdAtEpochMillis)}", style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun OrderItemCard(item: OrderItemEntity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.productNameBn, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Text("পরিমাণ: ${item.quantity}", style = MaterialTheme.typography.labelMedium)
                Text("প্রতিটি: ৳${item.pricePerUnit}", style = MaterialTheme.typography.labelMedium)
            }
            Text("৳${item.totalPrice}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TotalPriceCard(totalPrice: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("মোট মূল্য:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text("৳$totalPrice", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
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

private fun getStatusColor(status: String): String = when (status) {
    "PENDING" -> "⏳"
    "CONFIRMED" -> "✓"
    "DELIVERED" -> "✓✓"
    "CANCELLED" -> "✗"
    else -> ""
}

private fun formatDate(millis: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale("bn"))
    return sdf.format(Date(millis))
}

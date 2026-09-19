package com.groceryb2b.feature.home.presentation.checkout

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groceryb2b.core.ui.components.PrimaryButton
import com.groceryb2b.feature.home.data.CartItem
import com.groceryb2b.feature.home.data.CartSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    viewModel: CheckoutViewModel = hiltViewModel(),
    onOrderPlaced: (orderId: Long) -> Unit = {},
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val cartSummary by viewModel.cartSummary.collectAsState(CartSummary())
    val isProcessing by viewModel.isProcessing.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("চেকআউট") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (uiState) {
            CheckoutUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }
            
            is CheckoutUiState.Success -> {
                val success = uiState as CheckoutUiState.Success
                
                if (success.orderId != null) {
                    // Order successfully placed
                    Column(
                        modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("✓ অর্ডার সফলভাবে তৈরি হয়েছে!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Text("অর্ডার নম্বর: ${success.orderId}", style = MaterialTheme.typography.titleLarge)
                        Spacer(Modifier.height(24.dp))
                        PrimaryButton(
                            onClick = onNavigateBack,
                            text = "হোম এ ফিরুন"
                        )
                    }
                } else {
                    // Show cart items
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(padding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text("কার্ট সমীক্ষা", style = MaterialTheme.typography.headlineSmall)
                            Spacer(Modifier.height(4.dp))
                            Text("মোট আইটেম: ${cartSummary.totalItems}", style = MaterialTheme.typography.bodyMedium)
                        }
                        
                        if (cartSummary.items.isEmpty()) {
                            item {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("আপনার কার্ট খালি", style = MaterialTheme.typography.titleLarge)
                                    Spacer(Modifier.height(16.dp))
                                    PrimaryButton(
                                        onClick = onNavigateBack,
                                        text = "শপিং চালিয়ে যান"
                                    )
                                }
                            }
                        } else {
                            items(cartSummary.items, key = { it.productId }) { item ->
                                CartItemCard(item)
                            }
                            
                            item {
                                Spacer(Modifier.height(16.dp))
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("মোট মূল্য:", style = MaterialTheme.typography.titleMedium)
                                            Text("৳${cartSummary.totalPrice}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(Modifier.height(16.dp))
                                        PrimaryButton(
                                            onClick = { viewModel.placeOrder() },
                                            text = if (isProcessing) "প্রক্রিয়াকরণ..." else "অর্ডার নিশ্চিত করুন",
                                            modifier = Modifier.fillMaxWidth(),
                                            enabled = !isProcessing
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            is CheckoutUiState.Error -> {
                val error = uiState as CheckoutUiState.Error
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("ত্রুটি", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Text(error.message, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(24.dp))
                    Button(onClick = onNavigateBack) {
                        Text("ফিরে যান")
                    }
                }
            }
        }
    }
}

@Composable
private fun CartItemCard(item: CartItem) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.productNameBn, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(2.dp))
                Text(item.brand, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(4.dp))
                Text(item.unit, style = MaterialTheme.typography.bodySmall)
                Spacer(Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "৳${item.pricePerUnit}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (item.originalPricePerUnit > item.pricePerUnit) {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "৳${item.originalPricePerUnit}",
                            style = MaterialTheme.typography.labelSmall,
                            textDecoration = TextDecoration.LineThrough,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    }
                    Text(
                        text = " × ${item.quantity}",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("পরিমাণ: ${item.quantity}", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(8.dp))
                Text("৳${item.totalPrice}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

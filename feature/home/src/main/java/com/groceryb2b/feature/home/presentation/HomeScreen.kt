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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.groceryb2b.core.database.catalog.ProductEntity
import com.groceryb2b.core.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val cartCount = state.quantities.values.sum()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Grocery B2B") }, actions = { if (cartCount > 0) Text("কার্ট: $cartCount", modifier = Modifier.padding(end = 16.dp), style = MaterialTheme.typography.labelLarge) }) }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Text("দ্রুত অর্ডার করুন", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(4.dp))
                Text("পণ্য খুঁজুন, পরিমাণ দিন এবং কার্টে যোগ করুন", style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = state.query, onValueChange = viewModel::updateQuery, label = { Text("পণ্য বা ব্র্যান্ড খুঁজুন") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { AssistChip(onClick = { viewModel.selectCategory(null) }, label = { Text("সব") }, leadingIcon = if (state.selectedCategoryId == null) ({ Text("✓") }) else null) }
                    items(state.categories, key = { it.id }) { category ->
                        AssistChip(onClick = { viewModel.selectCategory(category.id) }, label = { Text(category.nameBn) }, leadingIcon = if (state.selectedCategoryId == category.id) ({ Text("✓") }) else null)
                    }
                }
            }
            item { Text("পণ্য (${state.products.size})", style = MaterialTheme.typography.titleLarge) }
            if (state.products.isEmpty()) item { Text("কোনো পণ্য পাওয়া যায়নি।", style = MaterialTheme.typography.bodyLarge) }
            items(state.products, key = { it.id }) { product -> ProductCard(product, state.quantities[product.id] ?: 0, { viewModel.changeQuantity(product.id, -1) }, { viewModel.changeQuantity(product.id, 1) }) }
        }
    }
}

@Composable
private fun ProductCard(product: ProductEntity, quantity: Int, onDecrease: () -> Unit, onIncrease: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.fillMaxWidth().padding(16.dp)) {
            Text(product.nameBn, style = MaterialTheme.typography.titleMedium)
            Text("${product.brand} • ${product.unit}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("৳${product.price}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                if (product.discountPercent > 0) { Spacer(Modifier.width(8.dp)); Text("${product.discountPercent}% ছাড়", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge) }
            }
            Text(if (product.stock > 0) "স্টক আছে: ${product.stock}" else "স্টক নেই", color = if (product.stock > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            HorizontalDivider(Modifier.padding(vertical = 10.dp))
            if (quantity == 0) PrimaryButton("কার্টে যোগ করুন", onIncrease, enabled = product.stock > 0) else Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = onDecrease, modifier = Modifier.width(72.dp)) { Text("−") }
                Text("$quantity", style = MaterialTheme.typography.titleLarge)
                Button(onClick = onIncrease, modifier = Modifier.width(72.dp), enabled = quantity < product.stock) { Text("+") }
            }
        }
    }
}

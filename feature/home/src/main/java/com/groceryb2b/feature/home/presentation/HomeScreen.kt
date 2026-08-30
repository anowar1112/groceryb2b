package com.groceryb2b.feature.home.presentation

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groceryb2b.core.database.catalog.ProductEntity
import com.groceryb2b.core.database.shop.ShopEntity
import com.groceryb2b.core.ui.components.PrimaryButton
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToCheckout: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToOrderHistory: () -> Unit = {},
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToContactUs: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val cartCount = state.quantities.values.sum()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(300.dp)
            ) {
                NavDrawerContent(
                    shop = state.shop,
                    onHeaderClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToProfile()
                    },
                    onEditProfileClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToEditProfile()
                    },
                    onOrderHistoryClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToOrderHistory()
                    },
                    onContactUsClick = {
                        scope.launch { drawerState.close() }
                        onNavigateToContactUs()
                    },
                    onLogoutClick = {
                        scope.launch { drawerState.close() }
                        viewModel.logout()
                        onLogout()
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Grocery B2B") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        if (cartCount > 0) {
                            Text(
                                "কার্ট: $cartCount",
                                modifier = Modifier.padding(end = 16.dp),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                if (cartCount > 0) {
                    PrimaryButton(
                        text = "চেকআউট ($cartCount)",
                        onClick = onNavigateToCheckout
                    )
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("দ্রুত অর্ডার করুন", style = MaterialTheme.typography.headlineSmall)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "পণ্য খুঁজুন, পরিমাণ দিন এবং কার্টে যোগ করুন",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = viewModel::updateQuery,
                        label = { Text("পণ্য বা ব্র্যান্ড খুঁজুন") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = state.selectedCategoryId == null,
                                onClick = { viewModel.selectCategory(null) },
                                label = { Text("সব") }
                            )
                        }
                        items(state.categories, key = { it.id }) { category ->
                            FilterChip(
                                selected = state.selectedCategoryId == category.id,
                                onClick = { viewModel.selectCategory(category.id) },
                                label = { Text(category.nameBn) }
                            )
                        }
                    }
                }
                item {
                    Text(
                        "পণ্য (${state.products.size})",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
                if (state.products.isEmpty()) item {
                    Text(
                        "কোনো পণ্য পাওয়া যায়নি।",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                items(
                    state.products,
                    key = { it.id }) { product ->
                    ProductCard(
                        product,
                        state.quantities[product.id] ?: 0,
                        { viewModel.changeQuantity(product.id, -1) },
                        { viewModel.changeQuantity(product.id, 1) })
                }
            }
        }
    }
}

@Composable
fun NavDrawerContent(
    shop: ShopEntity?,
    onHeaderClick: () -> Unit,
    onEditProfileClick: () -> Unit,
    onOrderHistoryClick: () -> Unit,
    onContactUsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp)
    ) {
        // Profile Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                .clickable { onHeaderClick() }
                .padding(24.dp)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = shop?.shopName ?: "অজানা দোকান",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = shop?.mobileNumber ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                shop?.id?.let { id ->
                    Text(
                        text = "ID: $id",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Menu Items
        DrawerMenuItem(
            icon = Icons.Default.Edit,
            label = "প্রোফাইল এডিট করুন",
            onClick = onEditProfileClick
        )
        DrawerMenuItem(
            icon = Icons.Default.History,
            label = "অর্ডার ইতিহাস",
            onClick = onOrderHistoryClick
        )
        DrawerMenuItem(
            icon = Icons.Default.Call,
            label = "আমাদের সাথে যোগাযোগ",
            onClick = onContactUsClick
        )

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        
        Spacer(modifier = Modifier.height(8.dp))

        // Logout Button
        NavigationDrawerItem(
            label = { 
                Text(
                    "লগ আউট", 
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                ) 
            },
            selected = false,
            onClick = onLogoutClick,
            icon = { 
                Icon(
                    Icons.Default.ExitToApp, 
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                ) 
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@Composable
private fun DrawerMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(label, fontWeight = FontWeight.Medium) },
        selected = false,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = null) },
        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

@Composable
private fun ProductCard(
    product: ProductEntity,
    quantity: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit
) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(16.dp)) {
            Text(product.nameBn, style = MaterialTheme.typography.titleMedium)
            Text("${product.brand} • ${product.unit}", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    "৳${product.price}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                if (product.discountPercent > 0) {
                    Spacer(Modifier.width(8.dp)); Text(
                    "${product.discountPercent}% ছাড়",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge
                )
                }
            }
            Text(
                if (product.stock > 0) "স্টক আছে: ${product.stock}" else "স্টক নেই",
                color = if (product.stock > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
            HorizontalDivider(Modifier.padding(vertical = 10.dp))
            if (quantity == 0) PrimaryButton(
                "কার্টে যোগ করুন",
                onIncrease,
                enabled = product.stock > 0
            ) else Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onDecrease, modifier = Modifier.width(72.dp)) { Text("−") }
                Text("$quantity", style = MaterialTheme.typography.titleLarge)
                Button(
                    onClick = onIncrease,
                    modifier = Modifier.width(72.dp),
                    enabled = quantity < product.stock
                ) { Text("+") }
            }
        }
    }
}

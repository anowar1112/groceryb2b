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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.BrandingWatermark
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groceryb2b.core.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductFormScreen(
    viewModel: AdminProductFormViewModel = hiltViewModel(),
    productId: Long? = null,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
    }

    // Navigate back when saved successfully
    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) {
            onNavigateBack()
        }
    }

    val categories = listOf(
        "rice" to "চাল",
        "dal" to "ডাল",
        "oil" to "তেল",
        "salt_sugar" to "লবণ ও চিনি",
        "biscuits" to "বিস্কুট",
        "noodles" to "নুডলস",
        "drinks" to "পানীয়",
        "soap" to "সাবান",
        "shampoo" to "শ্যাম্পু",
        "detergent" to "ডিটারজেন্ট",
        "tissue" to "টিস্যু",
        "spices" to "মসলা",
        "other" to "অন্যান্য"
    )
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        if (productId == null) "নতুন পণ্য যোগ করুন" else "পণ্য এডিট করুন",
                        fontWeight = FontWeight.Bold
                    ) 
                },
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
                .verticalScroll(scrollState)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Category Dropdown
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = categories.firstOrNull { it.first == uiState.categoryId }?.second ?: "ক্যাটাগরি সিলেক্ট করুন",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("ক্যাটাগরি") },
                    leadingIcon = { Icon(Icons.Default.Category, contentDescription = null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = MaterialTheme.shapes.medium
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { (id, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.updateField("categoryId", id)
                                expanded = false
                            }
                        )
                    }
                }
            }

            // Product Names
            OutlinedTextField(
                value = uiState.nameBn,
                onValueChange = { viewModel.updateField("nameBn", it) },
                label = { Text("পণ্যের নাম (বাংলা)") },
                placeholder = { Text("যেমন: মিনিকেট চাল") },
                leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            OutlinedTextField(
                value = uiState.nameEn,
                onValueChange = { viewModel.updateField("nameEn", it) },
                label = { Text("Product Name (English)") },
                placeholder = { Text("e.g. Miniket Rice") },
                leadingIcon = { Icon(Icons.Default.Language, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            )

            // Brand and Unit
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.brand,
                    onValueChange = { viewModel.updateField("brand", it) },
                    label = { Text("ব্র্যান্ড") },
                    leadingIcon = { Icon(Icons.AutoMirrored.Filled.BrandingWatermark, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(Modifier.width(12.dp))
                OutlinedTextField(
                    value = uiState.unit,
                    onValueChange = { viewModel.updateField("unit", it) },
                    label = { Text("ইউনিট") },
                    placeholder = { Text("কেজি/পিস") },
                    leadingIcon = { Icon(Icons.Default.Scale, contentDescription = null) },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                )
            }

            // Price and Stock
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.price,
                    onValueChange = { viewModel.updateField("price", it) },
                    label = { Text("মূল্য (৳)") },
                    leadingIcon = { Icon(Icons.Default.Payments, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                )
                Spacer(Modifier.width(12.dp))
                OutlinedTextField(
                    value = uiState.stock,
                    onValueChange = { viewModel.updateField("stock", it) },
                    label = { Text("স্টক পরিমাণ") },
                    leadingIcon = { Icon(Icons.Default.Inventory, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                )
            }

            if (uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = if (productId == null) "পণ্য যোগ করুন" else "আপডেট করুন",
                onClick = { viewModel.saveProduct() },
                isLoading = uiState.isSaving,
                enabled = !uiState.isSaving
            )
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

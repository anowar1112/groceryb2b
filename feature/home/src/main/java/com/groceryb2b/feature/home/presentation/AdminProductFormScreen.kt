package com.groceryb2b.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminProductFormScreen(
    viewModel: AdminProductFormViewModel = hiltViewModel(),
    productId: Long? = null,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(productId) {
        viewModel.loadProduct(productId)
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
                title = { Text("Product Form") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = categories.firstOrNull { it.first == uiState.categoryId }?.second ?: "Select category",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
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
            OutlinedTextField(
                value = uiState.nameBn,
                onValueChange = { viewModel.updateField("nameBn", it) },
                label = { Text("বাংলা নাম") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.nameEn,
                onValueChange = { viewModel.updateField("nameEn", it) },
                label = { Text("English Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.brand,
                onValueChange = { viewModel.updateField("brand", it) },
                label = { Text("Brand") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.unit,
                onValueChange = { viewModel.updateField("unit", it) },
                label = { Text("Unit") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.price,
                onValueChange = { viewModel.updateField("price", it) },
                label = { Text("Price") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = uiState.stock,
                onValueChange = { viewModel.updateField("stock", it) },
                label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth()
            )

            uiState.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (uiState.isSaved) {
                Text(
                    text = "Product saved successfully",
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { viewModel.saveProduct() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving
            ) {
                Text(if (uiState.isSaving) "Saving..." else "Save Product")
            }
        }
    }
}

package com.groceryb2b.feature.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groceryb2b.core.ui.components.PrimaryButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val shopName by viewModel.shopNameState.collectAsState()
    val ownerName by viewModel.ownerNameState.collectAsState()
    val address by viewModel.addressState.collectAsState()
    val deliveryLocation by viewModel.deliveryLocationState.collectAsState()
    val landmark by viewModel.landmarkState.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("প্রোফাইল সম্পাদনা") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        when (uiState) {
            EditProfileUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            is EditProfileUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text("আপনার তথ্য আপডেট করুন", style = MaterialTheme.typography.headlineSmall)
                    }

                    item {
                        OutlinedTextField(
                            value = shopName,
                            onValueChange = { viewModel.updateShopName(it) },
                            label = { Text("দোকানের নাম") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = ownerName,
                            onValueChange = { viewModel.updateOwnerName(it) },
                            label = { Text("মালিকের নাম") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = address,
                            onValueChange = { viewModel.updateAddress(it) },
                            label = { Text("ঠিকানা") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = deliveryLocation,
                            onValueChange = { viewModel.updateDeliveryLocation(it) },
                            label = { Text("ডেলিভারি লোকেশন") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = landmark,
                            onValueChange = { viewModel.updateLandmark(it) },
                            label = { Text("ল্যান্ডমার্ক (ঐচ্ছিক)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    item {
                        Spacer(Modifier.height(16.dp))
                        PrimaryButton(
                            text = "পরিবর্তন সংরক্ষণ করুন",
                            onClick = { viewModel.saveChanges() },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            EditProfileUiState.Saving -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(16.dp))
                    Text("সংরক্ষণ করছে...", style = MaterialTheme.typography.bodyMedium)
                }
            }

            EditProfileUiState.SaveSuccess -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("✓ সফলভাবে সংরক্ষিত হয়েছে!", style = MaterialTheme.typography.headlineMedium)
                    Spacer(Modifier.height(24.dp))
                    PrimaryButton(
                        text = "ফিরে যান",
                        onClick = onSaveSuccess
                    )
                }
            }

            is EditProfileUiState.Error -> {
                val error = uiState as EditProfileUiState.Error
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("ত্রুটি", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Text(error.message, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(24.dp))
                    PrimaryButton(
                        text = "ফিরে যান",
                        onClick = onNavigateBack
                    )
                }
            }
        }
    }
}

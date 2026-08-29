package com.groceryb2b.feature.shopsetup.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groceryb2b.core.ui.components.ErrorText
import com.groceryb2b.core.ui.components.PrimaryButton

@Composable
fun ShopSetupScreen(onCompleted: () -> Unit, viewModel: ShopSetupViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(state.isCompleted) { if (state.isCompleted) onCompleted() }

    Scaffold { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp)
                .imePadding().verticalScroll(rememberScrollState())
        ) {
            Text("দোকানের তথ্য দিন", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text("একবার তথ্য দিলে পরেরবার আরও দ্রুত অর্ডার করতে পারবেন।", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(20.dp))
            ProfileField("দোকানের নাম *", state.shopName) { value -> viewModel.update { it.copy(shopName = value, errorMessage = null) } }
            ProfileField("মালিকের নাম *", state.ownerName) { value -> viewModel.update { it.copy(ownerName = value, errorMessage = null) } }
            ProfileField(
                label = "যাচাইকৃত মোবাইল নম্বর *",
                value = state.mobileNumber,
                keyboardType = KeyboardType.Phone,
                readOnly = true,
                onValueChange = {}
            )
            ProfileField("দোকানের ঠিকানা *", state.address, singleLine = false) { value -> viewModel.update { it.copy(address = value, errorMessage = null) } }
            ProfileField("ডেলিভারি লোকেশন *", state.deliveryLocation, singleLine = false) { value -> viewModel.update { it.copy(deliveryLocation = value, errorMessage = null) } }
            ProfileField("ল্যান্ডমার্ক / অতিরিক্ত তথ্য (ঐচ্ছিক)", state.landmark, singleLine = false) { value -> viewModel.update { it.copy(landmark = value, errorMessage = null) } }
            ErrorText(state.errorMessage)
            Spacer(Modifier.height(20.dp))
            PrimaryButton("তথ্য সংরক্ষণ করুন", viewModel::save, isLoading = state.isSaving)
        }
    }
}

@Composable
private fun ProfileField(label: String, value: String, keyboardType: KeyboardType = KeyboardType.Text, singleLine: Boolean = true, readOnly: Boolean = false, onValueChange: (String) -> Unit) {
    OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label) }, singleLine = singleLine, readOnly = readOnly, minLines = if (singleLine) 1 else 2, keyboardOptions = KeyboardOptions(keyboardType = keyboardType), modifier = Modifier.fillMaxWidth())
    Spacer(Modifier.height(12.dp))
}

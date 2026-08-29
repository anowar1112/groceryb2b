package com.groceryb2b.feature.auth.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.groceryb2b.core.ui.components.ErrorText
import com.groceryb2b.core.ui.components.PrimaryButton

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).padding(24.dp).fillMaxSize()) {
            Text(
                text = "মোবাইল নম্বর দিয়ে লগইন করুন",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "এই ডিভাইসে আপনার দোকান খুঁজতে বা নতুন দোকান নিবন্ধন করতে মোবাইল নম্বর দিন",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = state.mobileNumber,
                onValueChange = viewModel::onMobileNumberChanged,
                label = { Text("মোবাইল নম্বর (01XXXXXXXXX)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            ErrorText(state.errorMessage)
            Spacer(modifier = Modifier.height(24.dp))

            PrimaryButton(
                text = "চালিয়ে যান",
                onClick = viewModel::requestOtp,
                enabled = state.mobileNumber.length == 11,
                isLoading = state.isLoading
            )
        }
    }
}

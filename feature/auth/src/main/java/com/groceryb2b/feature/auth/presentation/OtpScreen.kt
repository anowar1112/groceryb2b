package com.groceryb2b.feature.auth.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.TextButton
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
fun OtpScreen(
    onNavigateToShopSetup: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.navigateToShopSetup) {
        if (state.navigateToShopSetup) onNavigateToShopSetup()
    }
    LaunchedEffect(state.navigateToHome) {
        if (state.navigateToHome) onNavigateToHome()
    }

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).padding(24.dp).fillMaxSize()) {
            Text(
                text = "OTP যাচাই করুন",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${state.mobileNumber} নম্বরে পাঠানো ৬ সংখ্যার কোডটি দিন",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = state.otp,
                onValueChange = viewModel::onOtpChanged,
                label = { Text("OTP") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))
            ErrorText(state.errorMessage)
            Spacer(modifier = Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                val resendText = if (state.resendCooldownSeconds > 0) {
                    "আবার কোড পাঠান (${state.resendCooldownSeconds}s)"
                } else {
                    "আবার কোড পাঠান"
                }
                TextButton(
                    onClick = viewModel::resendOtp,
                    enabled = state.resendCooldownSeconds == 0
                ) {
                    Text(resendText)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = "যাচাই করুন",
                onClick = viewModel::verifyOtp,
                enabled = state.otp.length == 6,
                isLoading = state.isLoading
            )
        }
    }
}

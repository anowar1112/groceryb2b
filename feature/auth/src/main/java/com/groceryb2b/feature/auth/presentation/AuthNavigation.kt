package com.groceryb2b.feature.auth.presentation

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation

object AuthRoutes {
    const val GRAPH = "auth_graph"
    const val LOGIN = "login"
    const val OTP = "otp"
}

/**
 * Adds the auth flow to the app's nav graph. LoginScreen and OtpScreen
 * share the same AuthViewModel instance (scoped to AuthRoutes.GRAPH) so
 * state like the mobile number carries over between the two screens.
 */
fun NavGraphBuilder.authGraph(
    navController: NavHostController,
    onAuthenticatedNewShop: () -> Unit,
    onAuthenticatedExistingShop: () -> Unit
) {
    navigation(startDestination = AuthRoutes.LOGIN, route = AuthRoutes.GRAPH) {
        composable(AuthRoutes.LOGIN) { backStackEntry ->
            val parentEntry = navController.getBackStackEntry(AuthRoutes.GRAPH)
            val viewModel: AuthViewModel = hiltViewModel(parentEntry)
            val state by viewModel.uiState.collectAsState()

            LoginScreen(viewModel = viewModel)

            if (state.step == AuthStep.OTP_ENTRY) {
                navController.navigate(AuthRoutes.OTP)
            }
        }
        composable(AuthRoutes.OTP) {
            val parentEntry = navController.getBackStackEntry(AuthRoutes.GRAPH)
            val viewModel: AuthViewModel = hiltViewModel(parentEntry)

            OtpScreen(
                onNavigateToShopSetup = onAuthenticatedNewShop,
                onNavigateToHome = onAuthenticatedExistingShop,
                viewModel = viewModel
            )
        }
    }
}

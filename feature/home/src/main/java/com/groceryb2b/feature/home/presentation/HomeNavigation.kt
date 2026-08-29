package com.groceryb2b.feature.home.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

object HomeRoutes { 
    const val HOME = "home"
    const val CHECKOUT = "checkout"
}

fun NavGraphBuilder.homeScreen(navController: NavController) { 
    composable(HomeRoutes.HOME) { 
        HomeScreen(
            onNavigateToCheckout = { navController.navigate(HomeRoutes.CHECKOUT) }
        )
    }
    composable(HomeRoutes.CHECKOUT) {
        CheckoutScreen(
            onOrderPlaced = { orderId ->
                navController.popBackStack()
            },
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

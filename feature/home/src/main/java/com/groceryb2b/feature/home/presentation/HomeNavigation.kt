package com.groceryb2b.feature.home.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

object HomeRoutes { 
    const val HOME = "home"
    const val CHECKOUT = "checkout"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val ORDER_DETAILS = "order_details/{orderId}"
    
    fun orderDetails(orderId: Long) = "order_details/$orderId"
}

fun NavGraphBuilder.homeScreen(navController: NavController) { 
    composable(HomeRoutes.HOME) { 
        HomeScreen(
            onNavigateToCheckout = { navController.navigate(HomeRoutes.CHECKOUT) },
            onNavigateToProfile = { navController.navigate(HomeRoutes.PROFILE) }
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
    composable(HomeRoutes.PROFILE) {
        ProfileScreen(
            onNavigateBack = { navController.popBackStack() },
            onOrderClick = { orderId ->
                navController.navigate(HomeRoutes.orderDetails(orderId))
            },
            onEditClick = { navController.navigate(HomeRoutes.EDIT_PROFILE) }
        )
    }
    composable(HomeRoutes.EDIT_PROFILE) {
        EditProfileScreen(
            onNavigateBack = { navController.popBackStack() },
            onSaveSuccess = {
                navController.popBackStack(HomeRoutes.HOME, false)
            }
        )
    }
    composable(HomeRoutes.ORDER_DETAILS) { backStackEntry ->
        val orderId = backStackEntry.arguments?.getString("orderId")?.toLong() ?: return@composable
        OrderDetailsScreen(
            orderId = orderId,
            onNavigateBack = { navController.popBackStack() }
        )
    }
}

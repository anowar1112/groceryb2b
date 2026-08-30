package com.groceryb2b.feature.home.presentation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

object HomeRoutes { 
    const val HOME = "home"
    const val CHECKOUT = "checkout"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"
    const val ORDER_HISTORY = "order_history"
    const val CONTACT_US = "contact_us"
    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val ADMIN_PERMISSION = "admin_permission"
    const val ADMIN_PRODUCT_FORM = "admin_product_form/{productId}"
    const val ADMIN_PRODUCT_LIST = "admin_product_list"
    const val ORDER_DETAILS = "order_details/{orderId}"
    
    fun orderDetails(orderId: Long) = "order_details/$orderId"
    fun adminProductForm(productId: Long = 0L) = if (productId > 0L) "admin_product_form/$productId" else "admin_product_form/0"
}

fun NavGraphBuilder.homeScreen(navController: NavController, onLogout: () -> Unit) { 
    composable(HomeRoutes.HOME) { 
        HomeScreen(
            onNavigateToCheckout = { navController.navigate(HomeRoutes.CHECKOUT) },
            onNavigateToOrderHistory = { navController.navigate(HomeRoutes.ORDER_HISTORY) },
            onNavigateToEditProfile = { navController.navigate(HomeRoutes.EDIT_PROFILE) },
            onNavigateToContactUs = { navController.navigate(HomeRoutes.CONTACT_US) },
            onNavigateToAdminDashboard = { navController.navigate(HomeRoutes.ADMIN_DASHBOARD) },
            onLogout = onLogout
        )
    }
    composable(HomeRoutes.ADMIN_DASHBOARD) {
        AdminDashboardScreen(
            onNavigateBack = { navController.popBackStack() },
            onNavigateToProductForm = { navController.navigate(HomeRoutes.ADMIN_PRODUCT_LIST) },
            onNavigateToPermissionManagement = { navController.navigate(HomeRoutes.ADMIN_PERMISSION) }
        )
    }
    composable(HomeRoutes.ADMIN_PERMISSION) {
        AdminPermissionScreen(
            onNavigateBack = { navController.popBackStack() }
        )
    }
    composable(HomeRoutes.ADMIN_PRODUCT_LIST) {
        AdminProductListScreen(
            onNavigateBack = { navController.popBackStack() },
            onAddProduct = { navController.navigate(HomeRoutes.adminProductForm()) },
            onEditProduct = { productId ->
                navController.navigate(HomeRoutes.adminProductForm(productId))
            }
        )
    }
    composable(HomeRoutes.ADMIN_PRODUCT_FORM) { backStackEntry ->
        val productId = backStackEntry.arguments?.getString("productId")?.toLongOrNull()
        AdminProductFormScreen(
            productId = productId,
            onNavigateBack = { navController.popBackStack() }
        )
    }
    composable(HomeRoutes.CONTACT_US) {
        ContactUsScreen(onNavigateBack = { navController.popBackStack() })
    }
    composable(HomeRoutes.ORDER_HISTORY) {
        OrderHistoryScreen(
            onNavigateBack = { navController.popBackStack() },
            onOrderClick = { orderId ->
                navController.navigate(HomeRoutes.orderDetails(orderId))
            }
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

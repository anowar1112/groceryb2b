package com.groceryb2b.feature.shopsetup.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation

object ShopSetupRoutes {
    const val GRAPH = "shop_setup_graph"
    const val SETUP = "shop_setup"
}

fun NavGraphBuilder.shopSetupGraph(navController: NavHostController, onSetupCompleted: () -> Unit) {
    navigation(startDestination = ShopSetupRoutes.SETUP, route = ShopSetupRoutes.GRAPH) {
        composable(ShopSetupRoutes.SETUP) { ShopSetupScreen(onCompleted = onSetupCompleted) }
    }
}

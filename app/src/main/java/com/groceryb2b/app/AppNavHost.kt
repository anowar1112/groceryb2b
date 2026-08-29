package com.groceryb2b.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.groceryb2b.feature.auth.presentation.AuthRoutes
import com.groceryb2b.feature.auth.presentation.authGraph
import com.groceryb2b.feature.shopsetup.presentation.ShopSetupRoutes
import com.groceryb2b.feature.shopsetup.presentation.shopSetupGraph
import com.groceryb2b.feature.home.presentation.HomeRoutes
import com.groceryb2b.feature.home.presentation.homeScreen


@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AuthRoutes.GRAPH) {
        authGraph(
            navController = navController,
            onAuthenticatedNewShop = {
                navController.navigate(ShopSetupRoutes.GRAPH) {
                    popUpTo(AuthRoutes.GRAPH) { inclusive = true }
                }
            },
            onAuthenticatedExistingShop = {
                navController.navigate(HomeRoutes.HOME) {
                    popUpTo(AuthRoutes.GRAPH) { inclusive = true }
                }
            }
        )

        shopSetupGraph(
            navController = navController,
            onSetupCompleted = {
                navController.navigate(HomeRoutes.HOME) {
                    popUpTo(ShopSetupRoutes.GRAPH) { inclusive = true }
                }
            }
        )

        homeScreen(navController)
    }
}

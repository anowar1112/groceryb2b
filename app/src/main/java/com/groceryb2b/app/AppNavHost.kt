package com.groceryb2b.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.groceryb2b.feature.auth.presentation.AuthRoutes
import com.groceryb2b.feature.auth.presentation.authGraph
import com.groceryb2b.feature.shopsetup.presentation.ShopSetupRoutes
import com.groceryb2b.feature.shopsetup.presentation.shopSetupGraph

private const val ROUTE_HOME_PLACEHOLDER = "home_placeholder"

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
                navController.navigate(ROUTE_HOME_PLACEHOLDER) {
                    popUpTo(AuthRoutes.GRAPH) { inclusive = true }
                }
            }
        )

        shopSetupGraph(
            navController = navController,
            onSetupCompleted = {
                navController.navigate(ROUTE_HOME_PLACEHOLDER) {
                    popUpTo(ShopSetupRoutes.GRAPH) { inclusive = true }
                }
            }
        )

        // TODO(step 3): replace with feature/home's real nav graph.
        composable(ROUTE_HOME_PLACEHOLDER) {
            HomePlaceholderScreen()
        }
    }
}

@Composable
private fun HomePlaceholderScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        androidx.compose.material3.Text("Home — পরবর্তী ধাপে তৈরি হবে")
    }
}

package com.groceryb2b.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.groceryb2b.feature.auth.presentation.AuthRoutes
import com.groceryb2b.feature.auth.presentation.authGraph

private const val ROUTE_SHOP_SETUP_PLACEHOLDER = "shop_setup_placeholder"
private const val ROUTE_HOME_PLACEHOLDER = "home_placeholder"

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = AuthRoutes.GRAPH) {
        authGraph(
            navController = navController,
            onAuthenticatedNewShop = { navController.navigate(ROUTE_SHOP_SETUP_PLACEHOLDER) },
            onAuthenticatedExistingShop = { navController.navigate(ROUTE_HOME_PLACEHOLDER) }
        )

        // TODO(step 2): replace with feature/shop-setup's real nav graph.
        composable(ROUTE_SHOP_SETUP_PLACEHOLDER) {
            PlaceholderScreen("দোকানের তথ্য দিন (Shop Setup) — পরবর্তী ধাপে তৈরি হবে")
        }
        // TODO(step 3): replace with feature/home's real nav graph.
        composable(ROUTE_HOME_PLACEHOLDER) {
            PlaceholderScreen("Home — পরবর্তী ধাপে তৈরি হবে")
        }
    }
}

@Composable
private fun PlaceholderScreen(message: String) {
    Scaffold { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(message)
        }
    }
}

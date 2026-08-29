package com.groceryb2b.feature.home.presentation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

object HomeRoutes { const val HOME = "home" }

fun NavGraphBuilder.homeScreen() { composable(HomeRoutes.HOME) { HomeScreen() } }

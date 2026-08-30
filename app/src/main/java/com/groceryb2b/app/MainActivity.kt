package com.groceryb2b.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.groceryb2b.core.network.SessionManager
import com.groceryb2b.core.ui.theme.GroceryB2BTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GroceryB2BTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(isLoggedIn = sessionManager.isLoggedIn)
                }
            }
        }
    }
}

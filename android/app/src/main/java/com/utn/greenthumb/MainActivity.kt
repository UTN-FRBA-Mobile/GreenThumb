package com.utn.greenthumb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.utn.greenthumb.ui.navigation.AppNavHost
import com.utn.greenthumb.ui.theme.GreenThumbTheme
import com.utn.greenthumb.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GreenThumbTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavHost(authViewModel = authViewModel)
                }
            }
        }
    }
}

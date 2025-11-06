package com.utn.greenthumb

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.utn.greenthumb.domain.model.PlantDTO
import com.utn.greenthumb.ui.navigation.AppNavHost
import com.utn.greenthumb.ui.navigation.NavRoutes
import com.utn.greenthumb.ui.theme.GreenThumbTheme
import com.utn.greenthumb.viewmodel.AuthViewModel
import com.utn.greenthumb.viewmodel.PlantViewModel
import com.utn.greenthumb.viewmodel.WateringConfigViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val plantViewModel: PlantViewModel by viewModels()
    private val wateringConfigViewModel: WateringConfigViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            GreenThumbTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    AppNavHost(
                        authViewModel = authViewModel,
                        plantViewModel = plantViewModel,
                        wateringConfigViewModel = wateringConfigViewModel
                    )
                }
            }
        }

        if (intent != null) {
            handleIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val plant = intent.getSerializableExtra("plant") as? PlantDTO

        if (plant != null) {
            lifecycleScope.launch {
                plantViewModel.selectPlant(plant, NavRoutes.MyPlants.route)
            }
        }
    }

}

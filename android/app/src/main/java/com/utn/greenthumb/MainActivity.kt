package com.utn.greenthumb

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0
            )
        }

        createNotificationChannel()

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

    private fun createNotificationChannel() {
        val channelId = "watering_channel"
        val channelName = "Watering Reminders"
        val channelDescription = "Notifications for watering your plants"
        val importance = NotificationManager.IMPORTANCE_HIGH

        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDescription
        }

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
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

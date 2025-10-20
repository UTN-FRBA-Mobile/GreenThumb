package com.utn.greenthumb.ui.main.home

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.utn.greenthumb.domain.model.User
import com.utn.greenthumb.ui.main.BaseScreen
import com.utn.greenthumb.ui.theme.GreenBackground
import com.utn.greenthumb.utils.NotificationHelper
import com.utn.greenthumb.viewmodel.AuthViewModel
import com.utn.greenthumb.viewmodel.NotificationViewModel

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    notificationViewModel: NotificationViewModel,
    currentUser: User?,
    onHome: () -> Unit,
    onMyPlants: () -> Unit,
    onCamera: () -> Unit,
    onRemembers: () -> Unit,
    onProfile: () -> Unit
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("Home", "Granted ${isGranted}")
            notificationViewModel.refreshToken()
        } else {
            Log.w("HomeScreen", "Notification permission denied")
        }
    }

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (NotificationHelper.hasNotificationPermission(context)) {
                    notificationViewModel.refreshToken()
                } else {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                notificationViewModel.refreshToken()
            }
        }
    }

    BaseScreen(
        onHome = onHome,
        onMyPlants = onMyPlants,
        onCamera = onCamera,
        onRemembers = onRemembers,
        onProfile = onProfile
    ) {
        HomeScreenContent(
            userName = currentUser?.displayName ?: authViewModel.getUserName()
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    userName: String?
) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("GreenThumb 🌿") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenBackground)
        )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Bienvenido, ${userName ?: "Usuario no identificado"}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}


@Preview
@Composable
fun HomeScreenPreview(
    authViewModel: AuthViewModel = AuthViewModel(
        authRepository = TODO(),
        authManager = TODO()
    ),
    currentUser: User? = null,
    onHome: () -> Unit = {},
    onProfile: () -> Unit = {},
    onCamera: () -> Unit = {}
) {

}
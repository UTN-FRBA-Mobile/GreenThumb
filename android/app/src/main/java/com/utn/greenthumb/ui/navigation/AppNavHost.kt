package com.utn.greenthumb.ui.navigation

import android.util.Log
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.utn.greenthumb.state.UiState
import com.utn.greenthumb.ui.main.camera.CameraScreen
import com.utn.greenthumb.ui.main.home.HomeScreen
import com.utn.greenthumb.ui.main.login.LoginScreen
import com.utn.greenthumb.ui.main.profile.ProfileScreen
import com.utn.greenthumb.ui.main.result.ResultScreen
import com.utn.greenthumb.viewmodel.AuthViewModel
import com.utn.greenthumb.viewmodel.PlantViewModel

@Composable
fun AppNavHost(
    authViewModel: AuthViewModel,
    plantViewModel: PlantViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    val logoutState by authViewModel.logoutState.collectAsState()

    var hasNavigatedInitially by remember { mutableStateOf(false) }

    // Navegar automáticamente basado en el estado de autenticación
    LaunchedEffect(isUserLoggedIn, currentUser) {
        if (!hasNavigatedInitially) {
            val startDestination = if (isUserLoggedIn && currentUser != null) {
                NavRoutes.Home.route
            } else {
                NavRoutes.Login.route
            }
            navController.navigate(startDestination) {
                popUpTo(0) { inclusive = true }
            }
            hasNavigatedInitially = true
            Log.d("AppNavHost", "Initial navigation to: $startDestination")
        }
    }

    // Manejar logout exitoso
    LaunchedEffect(logoutState) {
        when (logoutState) {
            is UiState.Success -> {
                Log.d("AppNavHost", "Logout successful, navigating to login")
                navController.navigate(NavRoutes.Login.route) {
                    popUpTo(0) { inclusive = true }
                }
                authViewModel.clearAllErrors()
            }
            is UiState.Error -> {
                Log.e("AppNavHost", "Logout failed: ${(logoutState as UiState.Error).message}")
            }
            else -> { }
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.Home.route,
        modifier = modifier
    ) {

        // ===== LOGIN SCREEN =====
        composable(
            route = NavRoutes.Login.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) }
        ) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    Log.d("AppNavHost", "Login successful, navigating to home")
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                }
            )
        }


        // ===== HOME SCREEN =====
        composable(
            route = NavRoutes.Home.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        )  {
            if (!isUserLoggedIn || currentUser == null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Verificando autenticación...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            } else {
                HomeScreen(
                    authViewModel = authViewModel,
                    currentUser = currentUser,
                    onHome = {
                        Log.d("AppNavHost", "Navigating to Home Screen")
                        navController.navigate(NavRoutes.Home.route)
                    },
                    onProfile = {
                        Log.d("AppNavHost", "Navigating to Profile Screen")
                        navController.navigate(NavRoutes.Profile.route)
                    },
                    onCamera = {
                        Log.d("AppNavHost", "Navigating to Camera Screen")
                        navController.navigate(NavRoutes.Camera.route)
                    }
                )
            }
        }


        // ===== CAMERA SCREEN =====
        composable(
            route = NavRoutes.Camera.route,
            enterTransition = { slideInVertically(initialOffsetY = { it }) },
            exitTransition = { slideOutVertically(targetOffsetY = { it }) }
        ) {
            CameraScreen(
                plantViewModel = plantViewModel,
                onNavigateBack = {
                    Log.d("AppNavHost", "Navigating back from camera")
                    navController.popBackStack()
                },
                onNavigateToResult = { imageUri ->
                    Log.d("AppNavHost", "Navigating to result with image: $imageUri")
                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("imageUri", imageUri)
                    Log.d("AppNavHost", "Navigating to results")
                    navController.navigate(NavRoutes.Result.route)
                }
            )
        }


        // ===== RESULT SCREEN =====
        composable(
            route = NavRoutes.Result.route,
            enterTransition = { slideInVertically(initialOffsetY = { it }) },
            exitTransition = { slideOutVertically(targetOffsetY = { it }) }
        ) {
                backStackEntry ->
            val imageUriString: String? = backStackEntry.savedStateHandle.get<String>("imageUri")
            Log.d("AppNavHost", "Going to Result Screen with this URI: $imageUriString")
            ResultScreen(
                imageUri = imageUriString,
                navController = navController,
                onBackPressed = { navController.popBackStack() },
                plantViewModel = plantViewModel
            )
        }


        // ===== PROFILE SCREEN =====
        composable(
            route = NavRoutes.Profile.route,
            enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
        ) {
            if (currentUser != null) {
                ProfileScreen(
                    user = currentUser,
                    onNavigateBack = { navController.popBackStack() },
                    onLogout = { authViewModel.logout() }
                )
            } else {
                // Redireccionar a login si no hay usuario
                LaunchedEffect(Unit) {
                    navController.navigate(NavRoutes.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }
}

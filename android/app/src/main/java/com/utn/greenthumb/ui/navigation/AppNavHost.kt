package com.utn.greenthumb.ui.navigation

import android.util.Log
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.utn.greenthumb.ui.main.my.plants.MyPlantsScreen
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
                    onMyPlants = {
                        Log.d("AppNavHost", "Navigating to My Plants Screen")
                        navController.navigate(NavRoutes.MyPlants.route)
                    },
                    onCamera = {
                        Log.d("AppNavHost", "Navigating to Camera Screen")
                        navController.navigate(NavRoutes.Camera.route)
                    },
                    onRemembers = {
                        Log.d("AppNavHost", "Navigating to Remembers Screen")
                        // TODO: Navegar a la pantalla de Recordatorios
                        //navController.navigate(NavRoutes.Remember.route)
                    },
                    onProfile = {
                        Log.d("AppNavHost", "Navigating to Profile Screen")
                        navController.navigate(NavRoutes.Profile.route)
                    }
                )
            }
        }


        // ===== CAMERA SCREEN =====
        composable(
            route = NavRoutes.Camera.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {

            CameraScreen(
                plantViewModel = plantViewModel,
                onNavigateBack = {
                    Log.d("AppNavHost", "Navigating back from camera")
                    navController.popBackStack()
                },
                onNavigateToResult = {
                    Log.d("AppNavHost", "Plants identified, navigating to results")
                    navController.navigate(NavRoutes.Result.route) {
                        popUpTo(NavRoutes.Home.route) {
                            inclusive = false
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = false
                    }
                }
            )
        }

        // ===== MY PLANTS SCREEN =====
        composable(
            route = NavRoutes.MyPlants.route,
            enterTransition = { slideInVertically(initialOffsetY = { it }) },
            exitTransition = { slideOutVertically(targetOffsetY = { it }) }
        ) {
            MyPlantsScreen(
                authViewModel = authViewModel,
                currentUser = currentUser,
                onHome = {
                    Log.d("AppNavHost", "Navigating to Home Screen")
                    navController.navigate(NavRoutes.Home.route)
                },
                onMyPlants = {
                    Log.d("AppNavHost", "Navigating to My Plants Screen")
                    navController.navigate(NavRoutes.MyPlants.route)
                },
                onCamera = {
                    Log.d("AppNavHost", "Navigating to Camera Screen")
                    navController.navigate(NavRoutes.Camera.route)
                },
                onRemembers = {
                    Log.d("AppNavHost", "Navigating to Remembers Screen")
                    // TODO: Navegar a la pantalla de Recordatorios
                    //navController.navigate(NavRoutes.Remember.route)
                },
                onProfile = {
                    Log.d("AppNavHost", "Navigating to Profile Screen")
                    navController.navigate(NavRoutes.Profile.route)
                }
            )
        }


        // ===== RESULT SCREEN =====
        composable(
            route = NavRoutes.Result.route,
            enterTransition = {
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(300)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            ResultScreen(
                onBackPressed = {
                    Log.d("AppNavHost", "Navigating back from results")
                    navController.popBackStack(NavRoutes.Home.route, inclusive = false)
                },
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
                    onHome = {
                        Log.d("AppNavHost", "Navigating to Home Screen")
                        navController.navigate(NavRoutes.Home.route)
                    },
                    onMyPlants = {
                        Log.d("AppNavHost", "Navigating to My Plants Screen")
                        navController.navigate(NavRoutes.MyPlants.route)
                    },
                    onCamera = {
                        Log.d("AppNavHost", "Navigating to Camera Screen")
                        navController.navigate(NavRoutes.Camera.route)
                    },
                    onRemembers = {
                        Log.d("AppNavHost", "Navigating to Remembers Screen")
                        // TODO: Navegar a la pantalla de Recordatorios
                        //navController.navigate(NavRoutes.Remember.route)
                    },
                    onProfile = {
                        Log.d("AppNavHost", "Navigating to Profile Screen")
                        navController.navigate(NavRoutes.Profile.route)
                    },
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

package com.utn.greenthumb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.utn.greenthumb.ui.main.home.HomeScreen
import com.utn.greenthumb.ui.main.login.LoginScreen
import com.utn.greenthumb.ui.main.result.ResultScreen
import com.utn.greenthumb.viewmodel.AuthViewModel
import com.utn.greenthumb.viewmodel.PlantViewModel

@Composable
fun AppNavHost(
    authViewModel: AuthViewModel,
    plantViewModel: PlantViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (authViewModel.isUserLoggedIn()) {
            NavRoutes.Home.route
        } else {
            NavRoutes.Login.route
        }
    ) {

        composable(NavRoutes.Login.route) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(NavRoutes.Home.route) {
                        popUpTo(NavRoutes.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(NavRoutes.Home.route) {
            HomeScreen(
                authViewModel = authViewModel,
                navController = navController,
                onLogout = {
                    authViewModel.logout {
                        navController.navigate(NavRoutes.Login.route) {
                            popUpTo(NavRoutes.Home.route) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(NavRoutes.Result.route) {
            backStackEntry ->
            val imageUriString: String? = backStackEntry.savedStateHandle.get<String>("imageUri")
            ResultScreen(
                imageUri = imageUriString,
                navController = navController,
                onBackPressed = { navController.popBackStack() },
                plantViewModel = plantViewModel
            )
        }
    }
}

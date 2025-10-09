package com.utn.greenthumb.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController


// Para navegar entre pantallas de la barra de navegación
fun NavHostController.navigateToBottomBarDestination(
    route: String,
    currentRoute: String?
) {
    // Para evitar navegar otra vez en la misma pantalla
    if (route == currentRoute) {
        Log.d("Navigation", "Already at $route - no navigation.")
        return
    }

    try {
        navigate(route) {
            popUpTo(NavRoutes.Home.route) {
                saveState = true
                inclusive = false
            }
            launchSingleTop = true
            restoreState = true
        }

        Log.d("Navigation", "Navigating from $currentRoute to $route")
    } catch (e: Exception) {
        Log.e("Navigation", "Error navigating from $currentRoute to $route", e)
    }
}


// Para navegar a pantallas temporales
fun NavHostController.navigateToTemporaryScreen(
    route: String
) {
    try {
        navigate(route) {
            launchSingleTop = true
        }
        Log.d("Navigation", "Opening temporary screen: $route")

    } catch (e: Exception) {
        Log.e("Navigation", "Error opening temporary screen: $route", e)
    }
}


// Clase para gestionar acciones de la barra de navegación
class BottomBarNavigation(
    val currentRoute: String,
    val navController: NavHostController
) {
    private var lastNavigationTime = 0L
    private val navigationDebounceMs = 300L

    private fun canNavigate(): Boolean {
        val currentTime = System.currentTimeMillis()
        return if (currentTime - lastNavigationTime > navigationDebounceMs) {
            lastNavigationTime = currentTime
            true
        } else {
            Log.d("Navigation", "Navigation debounced - too many clicks")
            false
        }
    }

    fun onHome() {
        if (!canNavigate()) return
        navController.navigateToBottomBarDestination(
            route = NavRoutes.Home.route,
            currentRoute = currentRoute
        )
    }


    fun onMyPlants() {
        if (!canNavigate()) return
        navController.navigateToBottomBarDestination(
            route = NavRoutes.MyPlants.route,
            currentRoute = currentRoute
        )
    }

    fun onCamera() {
        navController.navigateToTemporaryScreen(
            route = NavRoutes.Camera.route
        )
    }

    fun onRemembers() {
        if (!canNavigate()) return
        navController.navigateToBottomBarDestination(
            route = NavRoutes.Remember.route,
            currentRoute = currentRoute
        )
    }

    fun onProfile() {
        if (!canNavigate()) return
        navController.navigateToBottomBarDestination(
            route = NavRoutes.Profile.route,
            currentRoute = currentRoute
        )
    }
}



@Composable
fun ScreenWithBottomBar(
    currentRoute: String,
    navController: NavHostController,
    content: @Composable (BottomBarNavigation) -> Unit
) {
    val navigation = remember(currentRoute, navController.currentBackStackEntry) {
        BottomBarNavigation(currentRoute, navController)
    }

    content(navigation)
}
package com.utn.greenthumb.ui.main.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.utn.greenthumb.ui.main.BaseScreen
import com.utn.greenthumb.viewmodel.AuthViewModel

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    navController: NavController,
    onLogout: () -> Unit
) {
    BaseScreen(navController = navController) {
        HomeScreenContent(
            userName = authViewModel.getUserName(),
            onLogout = { authViewModel.logout(onLogout) }
        )
    }

}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    userName: String?,
    onLogout: () -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("GreenThumb 🌿") })
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
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = onLogout) {
                    Text("Cerrar sesión")
                }
            }
        }
    }
}


@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreenContent(
        userName = "Usuario de Prueba",
        onLogout = {}
    )
}
package com.utn.greenthumb.ui.main.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.utn.greenthumb.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
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
                    "¡Hola, ${authViewModel.getUserName() ?: "Usuario"}!",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(20.dp))

                Button(onClick = { onLogout() }) {
                    Text("Cerrar sesión")
                }
            }
        }
    }
}
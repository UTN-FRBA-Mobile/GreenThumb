package com.utn.greenthumb.ui.main.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.utn.greenthumb.domain.model.User
import com.utn.greenthumb.viewmodel.AuthViewModel

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    currentUser: User?,
    onProfile: () -> Unit,
    onCamera: () -> Unit,
    onLogout: () -> Unit
) {
    HomeScreenContent(
        userName = currentUser?.displayName ?: authViewModel.getUserName(),
        onProfile = onProfile,
        onCamera = onCamera,
        onLogout = onLogout
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    userName: String?,
    onProfile: () -> Unit,
    onCamera: () -> Unit,
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

                Spacer(modifier = Modifier.height(20.dp))

                Button(onClick = onProfile) {
                    Text("Perfil")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(onClick = onCamera) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Identificar Planta")
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
        onProfile = { },
        onCamera = { },
        onLogout = { }
    )
}
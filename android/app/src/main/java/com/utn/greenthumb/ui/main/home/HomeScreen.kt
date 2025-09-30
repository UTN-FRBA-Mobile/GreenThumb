package com.utn.greenthumb.ui.main.home

import androidx.compose.foundation.layout.*
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
    onLogout: () -> Unit
) {
    HomeScreenContent(
        userName = currentUser?.displayName ?: authViewModel.getUserName(),
        onProfile = onProfile,
        onLogout = onLogout
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    userName: String?,
    onProfile: () -> Unit,
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
        onLogout = { }
    )
}
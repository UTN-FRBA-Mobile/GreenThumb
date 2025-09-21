package com.utn.greenthumb.ui.main.login

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.utn.greenthumb.viewmodel.AuthViewModel
import com.utn.greenthumb.R
import com.utn.greenthumb.data.model.plantid.IdentificationRequest
import com.utn.greenthumb.ui.navigation.NavRoutes
import com.utn.greenthumb.utils.ImageUtils
import com.utn.greenthumb.utils.rememberTakePictureLauncher
import com.utn.greenthumb.viewmodel.PlantViewModel

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    plantViewModel: PlantViewModel,
    navController: NavHostController,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val googleSignInClient = authViewModel.getGoogleSignInClient()

    // Google Sign-In Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    authViewModel.loginWithGoogleToken(
                        idToken,
                        onSuccess = onLoginSuccess,
                        onError = { e -> e.printStackTrace() }
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Camera Launcher
    val takePhoto = rememberTakePictureLauncher(
        onSuccess = {  imageUri ->
            try {
                val base64Image = ImageUtils.uriToBase64(context, imageUri)
                if (base64Image != null) {
                    val request = IdentificationRequest(
                        images = listOf(base64Image),
                        // TODO: Utilizar API de Geolocalización
                        longitude = 0.0,
                        latitude = 0.0,
                        similarImages = true
                    )
                    android.util.Log.d("LoginScreen", "Enviando imagen al ViewModel")
                    plantViewModel.identifyPlant(request)
                    android.util.Log.d("LoginScreen", "Imagen enviada al ViewModel")

                    navController.currentBackStackEntry
                        ?.savedStateHandle
                        ?.set("imageUri", imageUri.toString())

                    navController.navigate(NavRoutes.Result.route)

                } else {
                    Toast.makeText(context, "Error: Imagen inválida", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("LoginScreen", "Error al procesar imagen", e)
                Toast.makeText(context, "Error procesando imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        },
        onError = {
            Toast.makeText(context, "Error al capturar foto", Toast.LENGTH_SHORT).show()
        },
        onPermissionRejected = {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.greenthumb),
                    contentDescription = "Logo",
                    alignment = Alignment.Center,
                    modifier = Modifier.size(256.dp)
                )
                Text(
                    text = stringResource(R.string.login_title),
                    textAlign = Center,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.login_body),
                    textAlign = Center
                )
                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Button(onClick = {
                    val signInIntent: Intent = googleSignInClient.signInIntent
                    launcher.launch(signInIntent)
                }) {
                    Text(stringResource(R.string.login_google))
                }

                OutlinedButton(onClick = {
                    takePhoto()
                }) {
                    Text(stringResource(R.string.scan_now))
                }
            }
        }
    }
}

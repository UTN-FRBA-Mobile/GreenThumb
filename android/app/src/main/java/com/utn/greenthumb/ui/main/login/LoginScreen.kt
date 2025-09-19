package com.utn.greenthumb.ui.main.login

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.utn.greenthumb.viewmodel.AuthViewModel
import com.utn.greenthumb.R
import com.utn.greenthumb.ui.util.rememberTakePictureLauncher

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val context = LocalContext.current
    val googleSignInClient = authViewModel.getGoogleSignInClient()

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

    val takePhoto = rememberTakePictureLauncher(
        onSuccess = {  imageUri ->
            // TODO Replace with API call
            val viewIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(imageUri, "image/jpeg")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            try {
                context.startActivity(viewIntent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, "No se encontró una aplicación para abrir la imagen.", Toast.LENGTH_SHORT).show()
            }
        },
        onError = {
        },
        onPermissionRejected = {

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

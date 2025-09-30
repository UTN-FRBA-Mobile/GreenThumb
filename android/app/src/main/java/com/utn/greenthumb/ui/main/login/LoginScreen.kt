package com.utn.greenthumb.ui.main.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.utn.greenthumb.viewmodel.AuthViewModel
import com.utn.greenthumb.R
import com.utn.greenthumb.state.UiState
import com.utn.greenthumb.ui.theme.GreenThumbTheme

@Composable
fun LoginScreen(
    authViewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by authViewModel.uiState.collectAsState()
    val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // Auto-redirección si ya está logueado
    LaunchedEffect(isUserLoggedIn) {
        if (isUserLoggedIn && currentUser != null) {
            Log.d("LoginScreen", "User already logged in: ${currentUser?.email}")
            onLoginSuccess()
            return@LaunchedEffect
        }
    }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Google Sign-In Client
    val googleSignInClient by remember {
        derivedStateOf { authViewModel.getGoogleSignInClient() }
    }

    // Google Sign-In Launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        handleGoogleSignInResult(
            result = result,
            onSuccess = { account ->
                val idToken = account.idToken
                if (!idToken.isNullOrBlank()) {
                    Log.d("LoginScreen", "Google Sign-In successful, processing token")
                    authViewModel.loginWithGoogleToken(idToken)
                } else {
                    Log.e("LoginScreen", "ID Token is null or empty")
                    errorMessage = "Error: Token de Google inválido"
                    showErrorDialog = true
                }
            },
            onError = { error ->
                Log.e("LoginScreen", "Google Sign-In failed", error)
                errorMessage = getErrorMessage(error, context)
                showErrorDialog = true
            }
        )
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is UiState.Success -> {
                Log.d("LoginScreen", "Login successful: ${state.data.email}")
                onLoginSuccess()
            }
            is UiState.Error -> {
                Log.e("LoginScreen", "Login error: ${state.message}")
                errorMessage = state.message
                showErrorDialog = true
            }
            else -> { }
        }
    }

    LoginScreenContent(
        isLoading = uiState is UiState.Loading,
        onGoogleLogin = {
            Log.d("LoginScreen", "Initiating Google Sign-In")
            handleGoogleSignInClick(
                googleSignInClient = googleSignInClient,
                launcher = launcher,
                onError = { error ->
                    errorMessage = error
                    showErrorDialog = true
                }
            )
        }
    )

    if (showErrorDialog) {
        LoginErrorDialog(
            errorMessage = errorMessage,
            onDismiss = {
                showErrorDialog = false
                authViewModel.clearError()
            },
            onRetry = {
                showErrorDialog = false
                authViewModel.retryLogin()
            }
        )
    }
}

@Composable
fun LoginScreenContent(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    onGoogleLogin: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
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
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                // Logo
                LogoSection()

                // Título
                Text(
                    text = stringResource(R.string.login_title),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                // Subtítulo
                Text(
                    text = stringResource(R.string.login_body),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(
                    modifier = Modifier.height(32.dp)
                )

                // Botón de Inicio de Sesión con Google
                GoogleSignInButton(
                    isLoading = isLoading,
                    onClick = onGoogleLogin,
                    modifier = Modifier.fillMaxWidth()
                )

            }
        }
    }
}

@Composable
private fun LogoSection(
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(id = R.drawable.greenthumb),
        contentDescription = stringResource(R.string.app_logo),
        alignment = Alignment.Center,
        modifier = Modifier
            .size(256.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun GoogleSignInButton(
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (isLoading)
                MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.outline
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp,
            disabledElevation = 0.dp
        )
    ){
        AnimatedContent(
            targetState = isLoading,
            transitionSpec = {
                fadeIn(animationSpec = tween(300)) togetherWith
                        fadeOut(animationSpec = tween(300))
            },
            label = "button_content"
        ) { loading ->
            if (loading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.signing_in),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.login_google),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun LoginErrorDialog(
    errorMessage: String,
    onDismiss: () -> Unit,
    onRetry: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = stringResource(R.string.login_error_title),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.login_error_suggestion),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

private fun handleGoogleSignInClick(
    googleSignInClient: GoogleSignInClient,
    launcher: ActivityResultLauncher<Intent>,
    onError: (String) -> Unit,
) {
    try {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    } catch (e: Exception) {
        Log.e("LoginScreen", "Error launching Google Sign-In", e)
        onError("Error al iniciar el proceso de autenticación")
    }
}

private fun handleGoogleSignInResult(
    result: ActivityResult,
    onSuccess: (GoogleSignInAccount) -> Unit,
    onError: (Exception) -> Unit
) {
    when (result.resultCode) {
        Activity.RESULT_OK -> {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                Log.d("LoginScreen", "Google Sign-In successful for: ${account.email}")
                onSuccess(account)
            } catch (e: ApiException) {
                Log.e("LoginScreen", "Google Sign-In API Exception", e)
                onError(e)
            } catch (e: Exception) {
                Log.e("LoginScreen", "Google Sign-In Generic Exception", e)
                onError(e)
            }
        }
        Activity.RESULT_CANCELED -> {
            Log.d("LoginScreen", "Google Sign-In canceled by user")
            onError(Exception("Proceso de autenticación cancelado por el usuario"))
        }
        else -> {
            Log.w("LoginScreen", "Unexpected result code: ${result.resultCode}")
            onError(Exception("Error inesperado en la autenticación"))
        }
    }
}

private fun getErrorMessage(
    exception: Exception,
    context: Context
): String {
    return when (exception) {
        is ApiException -> {
            when (exception.statusCode) {
                CommonStatusCodes.SIGN_IN_REQUIRED ->
                    context.getString(R.string.error_sign_in_required)
                CommonStatusCodes.INVALID_ACCOUNT ->
                    context.getString(R.string.error_invalid_account)
                CommonStatusCodes.RESOLUTION_REQUIRED ->
                    context.getString(R.string.error_resolution_required)
                CommonStatusCodes.NETWORK_ERROR ->
                    context.getString(R.string.error_network)
                CommonStatusCodes.INTERNAL_ERROR ->
                    context.getString(R.string.error_internal)
                CommonStatusCodes.SERVICE_DISABLED ->
                    context.getString(R.string.error_service_disabled)
                CommonStatusCodes.SERVICE_VERSION_UPDATE_REQUIRED ->
                    context.getString(R.string.error_service_update_required)
                CommonStatusCodes.CANCELED ->
                    context.getString(R.string.error_canceled)
                else -> context.getString(R.string.error_unknown, exception.statusCode)
            }
        }
        is java.net.UnknownHostException ->
            context.getString(R.string.error_no_internet)
        is java.net.SocketTimeoutException ->
            context.getString(R.string.error_timeout)
        else ->
            context.getString(R.string.error_generic, exception.message ?: "Desconocido")
    }
}


/**
 * PREVIEWS
 */

@Preview(
    name = "Login - Estado Normal (Claro)",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun LoginScreenNormalPreview() {
    GreenThumbTheme {
        LoginScreenContent(
            onGoogleLogin = { },
            isLoading = false
        )
    }
}


@Preview(
    name = "Login - Estado Normal (Oscuro)",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun LoginScreenNormalDarkPreview() {
    GreenThumbTheme {
        LoginScreenContent(
            onGoogleLogin = { },
            isLoading = false
        )
    }
}


@Preview(
    name = "Login - Estado Cargando",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun LoginScreenLoadingPreview() {
    GreenThumbTheme {
        LoginScreenContent(
            onGoogleLogin = { },
            isLoading = true
        )
    }
}


@Preview(
    name = "Login - Estado Cargando (Oscuro)",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun LoginScreenLoadingDarkPreview() {
    GreenThumbTheme {
        LoginScreenContent(
            onGoogleLogin = { },
            isLoading = true
        )
    }
}


@Preview(
    name = "Login - Teléfono Pequeño",
    showBackground = true,
    widthDp = 320,
    heightDp = 568
)
@Composable
fun LoginScreenSmallPhonePreview() {
    GreenThumbTheme {
        LoginScreenContent(
            onGoogleLogin = { },
            isLoading = false
        )
    }
}


@Preview(
    name = "Login - Tablet",
    showBackground = true,
    widthDp = 768,
    heightDp = 1024
)
@Composable
fun LoginScreenTabletPreview() {
    GreenThumbTheme {
        LoginScreenContent(
            onGoogleLogin = { },
            isLoading = false
        )
    }
}


@Preview(
    name = "Login - Landscape",
    showBackground = true,
    widthDp = 640,
    heightDp = 360
)
@Composable
fun LoginScreenLandscapePreview() {
    GreenThumbTheme {
        LoginScreenContent(
            onGoogleLogin = { },
            isLoading = false
        )
    }
}


@Preview(
    name = "Login - Texto Pequeño",
    showBackground = true,
    fontScale = 0.5f
)
@Composable
fun LoginScreenSmallFontPreview() {
    GreenThumbTheme {
        LoginScreenContent(
            onGoogleLogin = { },
            isLoading = false
        )
    }
}


@Preview(
    name = "Login - Texto Normal",
    showBackground = true,
    fontScale = 1f
)
@Composable
fun LoginScreenNormalFontPreview() {
    GreenThumbTheme {
        LoginScreenContent(
            onGoogleLogin = { },
            isLoading = false
        )
    }
}


@Preview(
    name = "Login - Texto Grande",
    showBackground = true,
    fontScale = 1.5f
)
@Composable
fun LoginScreenLargeFontPreview() {
    GreenThumbTheme {
        LoginScreenContent(
            onGoogleLogin = { },
            isLoading = false
        )
    }
}


@Preview(
    name = "Login - Texto Muy Grande",
    showBackground = true,
    fontScale = 2.0f
)
@Composable
fun LoginScreenExtraLargeFontPreview() {
    GreenThumbTheme {
        LoginScreenContent(
            onGoogleLogin = { },
            isLoading = false
        )
    }
}

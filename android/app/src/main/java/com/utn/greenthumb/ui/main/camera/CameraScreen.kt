package com.utn.greenthumb.ui.main.camera

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.utn.greenthumb.R
import com.utn.greenthumb.data.model.plantid.IdentificationRequest
import com.utn.greenthumb.state.UiState
import com.utn.greenthumb.ui.theme.GreenBackground
import com.utn.greenthumb.ui.theme.GreenThumbTheme
import com.utn.greenthumb.utils.ImageUtils
import com.utn.greenthumb.viewmodel.PlantViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    plantViewModel: PlantViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToResult: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by plantViewModel.uiState.collectAsState()


    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )

    val locationPermissionState = rememberPermissionState(
        permission = Manifest.permission.ACCESS_COARSE_LOCATION
    )

    // Maneja diferentes estados de la pantalla
    var showCameraPermissionDeniedDialog by remember { mutableStateOf(false) }
    var showCameraPermanentlyDeniedDialog by remember { mutableStateOf(false) }
    var showLocationPermissionDialog by remember { mutableStateOf(false) }
    var isProcessingImage by remember { mutableStateOf(false) }
    var hasNavigatedToResult  by remember { mutableStateOf(false) }
    var hasAskedLocationPermission by remember { mutableStateOf(false) }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            Log.d("CameraScreen", "Photo captured successfully")
            isProcessingImage = true
            handleCapturedImage(
                bitmap = bitmap,
                context = context,
                plantViewModel = plantViewModel,
                hasLocationPermission = locationPermissionState.status.isGranted,
                onError = { error ->
                    isProcessingImage = false
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }
            )
        } else {
            Log.d("CameraScreen", "Photo capture cancelled by user")
        }
    }

    // Limpiar los resultados obtenidos previamente
    LaunchedEffect(Unit) {
        if (uiState is UiState.Success || uiState is UiState.Error) {
            plantViewModel.clearResults()
        }
    }

    // Maneja los estados de la API de consulta externa
    LaunchedEffect(uiState) {
        when (uiState) {
            is UiState.Success -> {
                if (!hasNavigatedToResult) {
                    Log.d("CameraScreen", "Plants identified successfully, navigating to results")
                    hasNavigatedToResult = true
                    onNavigateToResult()
                }
            }
            is UiState.Error -> {
                Log.e("CameraScreen", "Error identifying plants: ${(uiState as UiState.Error).message}")
                isProcessingImage = false
                hasNavigatedToResult = false
                Toast.makeText(
                    context,
                    (uiState as UiState.Error).message,
                    Toast.LENGTH_LONG
                ).show()
            }
            is UiState.Loading -> {
                Log.d("CameraScreen", "Loading state detected")
                isProcessingImage = true
            }
            UiState.Idle -> {
                Log.d("CameraScreen", "Idle state")
                if (!isProcessingImage) {
                    hasNavigatedToResult = false
                }
            }
        }
    }

    // Manejar permisos de Cámara
    LaunchedEffect(cameraPermissionState.status) {
        when {
            cameraPermissionState.status.isGranted -> {
                Log.d("CameraScreen", "Camera permission granted")
                showCameraPermissionDeniedDialog = false
                showCameraPermanentlyDeniedDialog = false

                // Una vez que tenemos permiso de cámara, solicitar ubicación si no se ha hecho
                if (!hasAskedLocationPermission && !locationPermissionState.status.isGranted) {
                    showLocationPermissionDialog = true
                }
            }
            cameraPermissionState.status.shouldShowRationale -> {
                Log.d("CameraScreen", "Camera permission denied - should show rationale")
                showCameraPermissionDeniedDialog = true
            }
            !cameraPermissionState.status.isGranted -> {
                Log.d("CameraScreen", "Requesting camera permission")
                cameraPermissionState.launchPermissionRequest()
            }
        }
    }

    GreenThumbTheme {
        CameraScreenContent(
            isProcessingImage = isProcessingImage,
            hasCameraPermission = cameraPermissionState.status.isGranted,
            hasLocationPermission = locationPermissionState.status.isGranted,
            onTakePhoto = {
                if (cameraPermissionState.status.isGranted && !isProcessingImage) {
                    cameraLauncher.launch(null)
                } else if (!cameraPermissionState.status.isGranted){
                    cameraPermissionState.launchPermissionRequest()
                }
            },
            onNavigateBack = onNavigateBack,
            modifier = modifier
        )
    }

    if (showCameraPermissionDeniedDialog) {
        CameraPermissionDeniedDialog(
            onDismiss = onNavigateBack,
            onRequestPermission = {
                showCameraPermissionDeniedDialog = false
                cameraPermissionState.launchPermissionRequest()
            }
        )
    }

    if (showCameraPermanentlyDeniedDialog) {
        CameraPermanentlyDeniedDialog(
            onDismiss = onNavigateBack,
            onOpenSettings  = {
                context.openAppSettings()
                onNavigateBack
            }
        )
    }

    if (showLocationPermissionDialog) {
        LocationPermissionDialog(
            onDismiss = {
                showLocationPermissionDialog = false
                hasAskedLocationPermission = true
                        Log.d("CameraScreen", "Location permission dismissed - continuing without location")
            },
            onRequestPermission = {
                showLocationPermissionDialog = false
                hasAskedLocationPermission = true
                locationPermissionState.launchPermissionRequest()
            }
        )
    }
}


private fun handleCapturedImage(
    bitmap: Bitmap,
    context: Context,
    plantViewModel: PlantViewModel,
    hasLocationPermission: Boolean,
    onError: (String) -> Unit
) {
    CoroutineScope(Dispatchers.IO).launch {
        try {
            val base64Image = ImageUtils.bitmapToBase64(bitmap)
            Log.d("CameraScreen", "Image converted in Base64: $base64Image")

            val location = if (hasLocationPermission) {
                Log.d("CameraScreen", "Attempting to get current location")
                getCurrentLocation(context)
            } else {
                Log.d("CameraScreen", "Location permission not granted, using default coordinates")
                null
            }

            val request = IdentificationRequest(
                images = listOf(base64Image),
                longitude = location?.longitude ?: 0.0,
                latitude = location?.latitude ?: 0.0,
            )

            Log.d("CameraScreen", "Sending request to PlantAPI: ${request.toString()}")

            withContext(Dispatchers.Main) {
                plantViewModel.identifyPlant(request)
            }

        } catch (e: Exception) {
            Log.e("CameraScreen", "Error processing image", e)
            withContext(Dispatchers.Main) {
                onError("Error al procesar la imagen: ${e.message}")
            }
        }
    }
}


private suspend fun getCurrentLocation(
    context: Context
): Location? {
    return try {
        val fusedLocationClient: FusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(context)

        // Intentar obtener la última ubicación conocida primero (más rápido)
        val lastLocation = try {
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            Log.w("CameraScreen", "Could not get last known location: ${e.message}")
            null
        }

        // Si hay última ubicación y no es muy antigua, usarla
        if (lastLocation != null) {
            Log.d("CameraScreen", "Using last known location: lat=${lastLocation.latitude}, lon=${lastLocation.longitude}")
            return lastLocation
        }

        // Si no hay última ubicación, obtener ubicación actual
        val cancellationTokenSource = CancellationTokenSource()
        val currentLocation = try {
            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                cancellationTokenSource.token
            ).await()
        } catch (e: Exception) {
            Log.w("CameraScreen", "Could not get current location: ${e.message}")
            null
        }

        if (currentLocation != null) {
            Log.d("CameraScreen", "Current location obtained: lat=${currentLocation.latitude}, lon=${currentLocation.longitude}")
        } else {
            Log.d("CameraScreen", "Could not obtain any location")
        }

        currentLocation
    } catch (e: SecurityException) {
        Log.e("CameraScreen", "Security exception getting location - permission may have been revoked", e)
        null
    } catch (e: Exception) {
        Log.e("CameraScreen", "Unexpected error getting location", e)
        null
    }
}


private fun Context.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraScreenContent(
    isProcessingImage: Boolean,
    hasCameraPermission: Boolean,
    hasLocationPermission: Boolean,
    onTakePhoto: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
){
    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenBackground),
                title = {
                    Text(stringResource(R.string.camera_screen_title))
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        enabled = !isProcessingImage
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_navigation)
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                isProcessingImage -> {
                    ProcessingImageContent()
                }
                hasCameraPermission -> {
                    ReadyToCaptureContent(
                        onTakePhoto = onTakePhoto,
                        hasLocationPermission = hasLocationPermission
                    )
                }
                else -> {
                    WaitingForPermissionContent()
                }
            }
        }
    }
}


@Composable
private fun ProcessingImageContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(64.dp),
            strokeWidth = 6.dp
        )

        Text(
            text = stringResource(R.string.processing_image),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.processing_image_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
private fun ReadyToCaptureContent(
    onTakePhoto: () -> Unit,
    hasLocationPermission: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CameraAlt,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = stringResource(R.string.ready_to_capture),
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Text(
            text = stringResource(R.string.capture_instructions),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        if (!hasLocationPermission) {
            Text(
                text = stringResource(R.string.location_permission_optional),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.tertiary,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onTakePhoto,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.take_photo),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}


@Composable
private fun WaitingForPermissionContent(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularProgressIndicator()
        Text(
            text = stringResource(R.string.waiting_camera_permission),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
private fun CameraPermissionDeniedDialog(
    onDismiss: () -> Unit,
    onRequestPermission: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(stringResource(R.string.camera_permission_required))
        },
        text = {
            Text(stringResource(R.string.camera_permission_explanation))
        },
        confirmButton = {
            TextButton(onClick = onRequestPermission) {
                Text(stringResource(R.string.grant_permission))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}


@Composable
private fun CameraPermanentlyDeniedDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Block,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(stringResource(R.string.camera_permission_denied))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.camera_permission_permanently_denied))
                Text(
                    text = stringResource(R.string.open_settings_instruction),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onOpenSettings) {
                Text(stringResource(R.string.open_settings))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.back_navigation))
            }
        }
    )
}


@Composable
private fun LocationPermissionDialog(
    onDismiss: () -> Unit,
    onRequestPermission: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(stringResource(R.string.location_permission_title))
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(stringResource(R.string.location_permission_explanation))
                Text(
                    text = stringResource(R.string.location_permission_optional),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onRequestPermission) {
                Text(stringResource(R.string.allow_location))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.continue_without_location))
            }
        }
    )
}
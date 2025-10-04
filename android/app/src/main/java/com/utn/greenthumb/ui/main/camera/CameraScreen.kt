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
import com.google.accompanist.permissions.rememberPermissionState
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import com.utn.greenthumb.R
import com.utn.greenthumb.data.model.plantid.IdentificationRequest
import com.utn.greenthumb.utils.ImageUtils
import com.utn.greenthumb.viewmodel.PlantViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    plantViewModel: PlantViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToResult: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val cameraPermissionState = rememberPermissionState(
        permission = Manifest.permission.CAMERA
    )

    var showPermissionDeniedDialog by remember { mutableStateOf(false) }
    var showPermanentlyDeniedDialog by remember { mutableStateOf(false) }
    var isProcessingImage by remember { mutableStateOf(false) }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            handleCapturedImage(
                bitmap = bitmap,
                context = context,
                plantViewModel = plantViewModel,
                onSuccess = { imageUri ->
                    onNavigateToResult(imageUri)
                },
                onError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                },
                onProcessingStart = { isProcessingImage = true },
                onProcessingEnd = { isProcessingImage = false }
            )
        } else {
            Toast.makeText(context, "Error al capturar la foto",
                Toast.LENGTH_SHORT).show()
        }
    }

    // Verifica los permisos de Cámara al iniciar la pantalla
    LaunchedEffect(Unit) {
        when {
            cameraPermissionState.status.isGranted -> {
                cameraLauncher.launch(null)
            }
            cameraPermissionState.status.shouldShowRationale -> {
                showPermissionDeniedDialog = true
            }
            else -> {
                cameraPermissionState.launchPermissionRequest()
            }
        }
    }

    // Verifica el estado de los permisos de Cámara
    LaunchedEffect(cameraPermissionState.status) {
        when {
            cameraPermissionState.status.isGranted -> {
                showPermissionDeniedDialog = false
                showPermanentlyDeniedDialog = false
                cameraLauncher.launch(null)
            }
            !cameraPermissionState.status.isGranted &&
                    !cameraPermissionState.status.shouldShowRationale -> {
                        showPermanentlyDeniedDialog = true
            }
        }
    }

    CameraScreenContent(
        isProcessingImage = isProcessingImage,
        onTakePhoto = {
            if (cameraPermissionState.status.isGranted) {
                cameraLauncher.launch(null)
            } else {
                cameraPermissionState.launchPermissionRequest()
            }
        },
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )

    if (showPermissionDeniedDialog) {
        PermissionDeniedDialog(
            onDismiss = onNavigateBack,
            onRequestPermission = {
                showPermissionDeniedDialog = false
                cameraPermissionState.launchPermissionRequest()
            }
        )
    }

    if (showPermanentlyDeniedDialog) {
        PermanentlyDeniedDialog(
            onDismiss = onNavigateBack,
            onOpenSettings  = {
                context.openAppSettings()
                onNavigateBack
            }
        )
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CameraScreenContent(
    isProcessingImage: Boolean,
    onTakePhoto: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
){
    Scaffold(
        topBar = {
            TopAppBar(
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
            if (isProcessingImage) {
                ProcessingImageContent()
            } else {
                ReadyToCaptureContent(onTakePhoto = onTakePhoto)
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
private fun PermissionDeniedDialog(
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
private fun PermanentlyDeniedDialog(
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


private fun handleCapturedImage(
    bitmap: Bitmap,
    context: Context,
    plantViewModel: PlantViewModel,
    onSuccess: (String) -> Unit,
    onError: (String) -> Unit,
    onProcessingStart: () -> Unit,
    onProcessingEnd: () -> Unit
) {
    onProcessingStart()

    CoroutineScope(Dispatchers.IO).launch {
        try {
            // Guardar bitmap temporalmente
            val imageUri = saveBitmapToCache(context, bitmap)

            // Convertir a Base64
            val base64Image = ImageUtils.bitmapToBase64(bitmap)

            // Obtener ubicación (opcional)
            val location = getCurrentLocation(context)

            // Crear request
            val request = IdentificationRequest(
                images = listOf(base64Image),
                longitude = location?.longitude ?: 0.0,
                latitude = location?.latitude ?: 0.0,
                similarImages = true
            )

            Log.d("CameraScreen", "Enviando imagen al ViewModel")

            withContext(Dispatchers.Main) {
                plantViewModel.identifyPlant(request)
                onProcessingEnd()
                onSuccess(imageUri.toString())
            }

            Log.d("CameraScreen", "Imagen enviada al ViewModel")

        } catch (e: Exception) {
            Log.e("CameraScreen", "Error processing image", e)
            withContext(Dispatchers.Main) {
                onProcessingEnd()
                onError("Error al procesar la imagen: ${e.message}")
            }
        }
    }
}


private fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "plant_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}


private fun getCurrentLocation(
    context: Context
): Location? {
    // TODO: Implementar obtención de ubicación real
    // Por ahora retorna null, puedes implementar con FusedLocationProviderClient
    return null
}


private fun Context.openAppSettings() {
    Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", packageName, null)
    ).also(::startActivity)
}
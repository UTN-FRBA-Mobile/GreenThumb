package com.utn.greenthumb.ui.util

import android.net.Uri
import android.os.Environment
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun rememberTakePictureLauncher(
    onSuccess: (Uri) -> Unit,
    onPermissionRejected: () -> Unit,
    onError: () -> Unit
): () -> Unit {
    val context = LocalContext.current
    val fileProviderAuthority = "${context.packageName}.fileprovider"

    val imagesDir = File(context.cacheDir, "images")
    if (!imagesDir.exists()) {
        imagesDir.mkdirs()
    }

    val tempFile = File(imagesDir, "temp_image_${System.currentTimeMillis()}.jpg").apply {
        createNewFile()
        deleteOnExit()
    }

    val imageUri: Uri = FileProvider.getUriForFile(
        context,
        fileProviderAuthority,
        tempFile
    )

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            onSuccess(imageUri)
        } else {
            onError()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            cameraLauncher.launch(imageUri)
        } else {
            onPermissionRejected()
        }
    }

    return {
        when (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)) {
            android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                cameraLauncher.launch(imageUri)
            }
            else -> {
                permissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }
}
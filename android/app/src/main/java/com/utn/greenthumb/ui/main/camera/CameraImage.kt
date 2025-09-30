package com.utn.greenthumb.ui.main.camera

class CameraImage {


/*

    // Camera Launcher
    val takePhoto = rememberTakePictureLauncher(
        onSuccess = {  imageUri ->
            try {
                val base64Image = ImageUtils.uriToBase64(context, imageUri)
                val request = IdentificationRequest(
                    images = listOf(base64Image),
                    // TODO: Utilizar API de Geolocalización
                    longitude = 0.0,
                    latitude = 0.0,
                    similarImages = true
                )
                Log.d("LoginScreen", "Enviando imagen al ViewModel")
                plantViewModel.identifyPlant(request)
                Log.d("LoginScreen", "Imagen enviada al ViewModel")

                navController.currentBackStackEntry
                    ?.savedStateHandle
                    ?.set("imageUri", imageUri.toString())

                navController.navigate(NavRoutes.Result.route)

            } catch (e: Exception) {
                Log.e("LoginScreen", "Error al procesar imagen", e)
                Toast.makeText(context, "Error procesando imagen: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        },
        onError = {
            Toast.makeText(context, "Error al capturar foto", Toast.LENGTH_SHORT).show()
        },
        onPermissionRejected = {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    )*/
}
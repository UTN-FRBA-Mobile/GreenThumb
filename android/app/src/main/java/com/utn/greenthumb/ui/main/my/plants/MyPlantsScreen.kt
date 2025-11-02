package com.utn.greenthumb.ui.main.my.plants

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.utn.greenthumb.R
import com.utn.greenthumb.domain.model.PlantDTO
import com.utn.greenthumb.ui.main.BaseScreen
import com.utn.greenthumb.ui.main.GreenThumbTopAppBar
import com.utn.greenthumb.ui.theme.GreenBackground

import com.utn.greenthumb.viewmodel.MyPlantsViewModel
import kotlinx.coroutines.delay

@Composable
fun MyPlantsScreen(
    onHome: () -> Unit,
    onMyPlants: () -> Unit,
    onCamera: () -> Unit,
    onRemembers: () -> Unit,
    onProfile: () -> Unit,
    onNavigateBack: () -> Unit,
    onPlantSelected: (PlantDTO) -> Unit,
    viewModel: MyPlantsViewModel = hiltViewModel()
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val clientId = currentUser?.uid

    val plants by viewModel.plants.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isDeleting by viewModel.isDeleting.collectAsState()
    val deleteError by viewModel.deleteError.collectAsState()
    val deleteSuccess by viewModel.deleteSuccess.collectAsState()

    // Actualiza la pantalla con sus plantas cada vez que ingresa a la misma
    LaunchedEffect(Unit) {
        clientId?.let {
            Log.d("MyPlantsScreen", "Screen loaded/resumed - fetching plants for user: $it")
            viewModel.fetchMyPlants(it)
        }
    }

    // Actualiza la pantalla luego de eliminar exitosamente una planta del repositorio
    LaunchedEffect(deleteSuccess, deleteError) {
        if (deleteSuccess) {
            delay(500)
            clientId?.let {
                Log.d("MyPlantsScreen", "Plant deleted - refreshing list")
                viewModel.fetchMyPlants(it)
            }
            viewModel.resetDeleteState()
        }
    }

    BackHandler(onBack = onNavigateBack)

    BaseScreen(
        onHome = onHome,
        onMyPlants = onMyPlants,
        onCamera = onCamera,
        onRemembers = onRemembers,
        onProfile = onProfile
    ) {
        MyPlantsScreenContent(
            viewModel = viewModel,
            plants = plants,
            isLoading = isLoading,
            error = error,
            isDeleting = isDeleting,
            deleteError = deleteError,
            onNavigateBack = onNavigateBack,
            onPlantSelected = onPlantSelected,
            onPlantDeleted = { plant ->
                clientId?.let {
                    if (plant.id != null) {
                        Log.d("MyPlantsScreen", "Plant to delete: ${plant.id}")
                        viewModel.deletePlant(plant.id)
                    }
                }
            },
            onRetry = { clientId?.let { viewModel.fetchMyPlants(it) } },
            onDismissDeleteError = { viewModel.resetDeleteState() }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyPlantsScreenContent(
    viewModel: MyPlantsViewModel,
    plants: List<PlantDTO>,
    isLoading: Boolean,
    error: String?,
    isDeleting: Boolean,
    deleteError: String?,
    onNavigateBack: () -> Unit,
    onPlantSelected: (PlantDTO) -> Unit,
    onPlantDeleted: (PlantDTO) -> Unit,
    onRetry: () -> Unit,
    onDismissDeleteError: () -> Unit
) {
    Scaffold(
        topBar = {
            GreenThumbTopAppBar(
                title = stringResource(R.string.my_plants),
                onNavigateBack = onNavigateBack
            )
    }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                when {
                    isLoading -> {
                        LoadingState()
                    }

                    error != null -> {
                        ErrorState(
                            errorMessage = error,
                            onRetry = onRetry
                        )
                    }

                    plants.isEmpty() -> {
                        EmptyState()
                    }

                    else -> {
                        MyPlantsListContent(
                            viewModel = viewModel,
                            plants = plants,
                            onPlantDeleted = onPlantDeleted,
                            onPlantSelected = onPlantSelected
                        )
                    }
                }
                if (isDeleting) {
                    DeletingDialog()
                }

                deleteError?.let { errorMsg ->
                    DeleteErrorDialog(
                        errorMessage = errorMsg,
                        onDismiss = onDismissDeleteError
                    )
                }
            }
        }
    }
}


@Composable
private fun MyPlantsListContent(
    viewModel: MyPlantsViewModel,
    plants: List<PlantDTO>,
    onPlantDeleted: (PlantDTO) -> Unit,
    onPlantSelected: (PlantDTO) -> Unit
) {
    var plantToDelete by remember { mutableStateOf<PlantDTO?>(null) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        HeaderSection(plantsCount = plants.size)

        Spacer(modifier = Modifier.height(8.dp))

        // Lista Desplazable (LazyColumn)
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = plants,
                key = { it.id!! }
            ) { plant ->
                AnimatedPlantItem(
                    plant = plant,
                    onDeleteClick = { plantToDelete = plant },
                    onDetailsClick = { onPlantSelected(plant) },
                    onFavoriteClick = {
                        if (plant.id != null) {
                            Log.d("MyPlantsScreen", "Favorite clicked for plant: ${plant.name} to ${!(plant.favourite ?: false)}")
                            viewModel.toggleFavorite(plant.id, !(plant.favourite ?: false))
                        }
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Dialogo de confirmación de eliminación
    plantToDelete?.let { plant ->
        DeleteConfirmationDialog(
            plantName = plant.name,
            onConfirm = {
                onPlantDeleted(plant)
                plantToDelete = null
            },
            onDismiss = { plantToDelete = null }
        )
    }
}

@Composable
private fun HeaderSection(
    plantsCount: Int
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp)
        ){
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {

                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(16.dp)
                )
                // Contador de plantas
                Text(
                    text = getPlantsCountText(plantsCount),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                thickness = DividerDefaults.Thickness, color = MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}


@Composable
private fun AnimatedPlantItem(
    plant: PlantDTO,
    onDeleteClick: () -> Unit,
    onDetailsClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) +
                slideInVertically(
                    initialOffsetY = { 40 },
                    animationSpec = tween(300)
                ),
        exit = fadeOut(animationSpec = tween(200)) +
                slideOutHorizontally(
                    targetOffsetX = { -it },
                    animationSpec = tween(200)
                ) +
                scaleOut(targetScale = 0.8f, animationSpec = tween(200))
    ) {
        PlantItem(
            plant = plant,
            onDeleteClick = onDeleteClick,
            onDetailsClick = onDetailsClick,
            onFavoriteClick = onFavoriteClick
        )
    }
}


@Composable
private fun PlantItem(
    plant: PlantDTO,
    onDeleteClick: () -> Unit,
    onDetailsClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {

    Card(
        modifier = Modifier
                .fillMaxWidth()
                .clickable { onDetailsClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen de la planta
            PlantImage(imageUrl = plant.images?.firstOrNull()?.url, plantName = plant.name)

            Spacer(modifier = Modifier.width(12.dp))

            // Información de la planta
            PlantInfo(
                plantName = plant.name,
                commonNames = plant.commonNames,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Botones de acción
            ActionButtons(
                onDeleteClick = onDeleteClick,
                onFavoriteClick = onFavoriteClick,
                isFavorite = plant.favourite ?: false
            )
        }
    }
}


@Composable
private fun PlantImage(
    imageUrl: String?,
    plantName: String
) {
    Card(
        modifier = Modifier.size(72.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.greenthumb),
            error = painterResource(id = R.drawable.greenthumb),
            contentDescription = plantName,
            contentScale = ContentScale.Crop
        )
    }
}


@Composable
private fun PlantInfo(
    plantName: String,
    commonNames: List<String>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        // Nombre de la Planta
        Text(
            text = plantName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            color = MaterialTheme.colorScheme.onSurface
        )

        // Mostrar nombre común si existe
        if (commonNames.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocalFlorist,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = commonNames.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


@Composable
private fun FavoriteButton(
    isFavorite: Boolean,
    onClick: () -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    // Animación del color
    val containerColor by animateColorAsState(
        targetValue = if (isFavorite) {
            Color(0xFFFFE5E5)
        } else {
            MaterialTheme.colorScheme.surfaceVariant
        },
        animationSpec = tween(300),
        label = "containerColor"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isFavorite) {
            Color(0xFFE91E63)
        } else {
            MaterialTheme.colorScheme.onSurfaceVariant
        },
        animationSpec = tween(300),
        label = "iconColor"
    )

    FilledIconButton(
        onClick = onClick,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = containerColor,
            contentColor = iconColor
        ),
        modifier = modifier
            .size(40.dp)
            .scale(scale)
    ) {
        Icon(
            imageVector = if (isFavorite) {
                Icons.Default.Favorite // Corazón relleno
            } else {
                Icons.Default.FavoriteBorder // Corazón vacío
            },
            contentDescription = if (isFavorite) {
                stringResource(R.string.remove_from_favorites)
            } else {
                stringResource(R.string.add_to_favorites)
            },
            modifier = Modifier.size(20.dp),
            tint = iconColor
        )
    }
}


@Composable
private fun ActionButtons(
    onDeleteClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    isFavorite: Boolean
) {
    var favoritePressed by remember { mutableStateOf(false) }
    var deletePressed by remember { mutableStateOf(false) }

    val deleteScale by animateFloatAsState(
        targetValue = if (deletePressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "deleteScale"
    )

    val favoriteScale by animateFloatAsState(
        targetValue = if (favoritePressed) 1.2f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "favoriteScale"
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón de favorito
        FavoriteButton(
            isFavorite = isFavorite,
            onClick = {
                favoritePressed = true
                onFavoriteClick()
            },
            scale = favoriteScale
        )

        // Botón de eliminar
        FilledIconButton(
            onClick = {
                deletePressed = true
                onDeleteClick()
            },
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = Color(0xFFD32F2F),
                contentColor = Color.White
            ),
            modifier = Modifier
                .size(40.dp)
                .scale(deleteScale)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.delete_plant),
                modifier = Modifier.size(20.dp)
            )
        }
    }

    LaunchedEffect(deletePressed) {
        if (deletePressed) {
            delay(200)
            deletePressed = false
        }
    }

    LaunchedEffect(favoritePressed) {
        if (favoritePressed) {
            delay(300)
            favoritePressed = false
        }
    }
}


@Composable
private fun DeleteConfirmationDialog(
    plantName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = stringResource(R.string.delete_plant_intention),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.delete_plant_confirmation),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "\"$plantName\"",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = stringResource(R.string.delete_plant_last_warning),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.delete))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun DeletingDialog() {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Dialog(
        onDismissRequest = { },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(32.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .size(64.dp)
                        .graphicsLayer { rotationZ = rotation }
                )

                Text(
                    text = stringResource(R.string.deleting_plant),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun DeleteErrorDialog(
    errorMessage: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = "Error al eliminar",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = stringResource(R.string.error_saving_plant),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.understood))
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
private fun LoadingState(){
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.loading_plants),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
private fun ErrorState(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )

            Text(
                text = stringResource(R.string.error_loading_plants),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onRetry,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.retry))
            }
        }
    }
}


@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "🌱",
                style = MaterialTheme.typography.displayLarge,
                fontSize = 72.sp
            )

            Text(
                text = stringResource(R.string.no_plants_saved),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(R.string.take_picture_to_start),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Función para obtener el texto correspondiente al número de plantas
private fun getPlantsCountText(count: Int): String {
    return when (count) {
        0 -> "No hay plantas"
        1 -> "Total 1 planta"
        else -> "Total $count plantas"
    }
}
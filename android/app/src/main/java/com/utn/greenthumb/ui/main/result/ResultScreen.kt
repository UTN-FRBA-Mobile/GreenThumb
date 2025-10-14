package com.utn.greenthumb.ui.main.result

import android.content.res.Configuration
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.utn.greenthumb.R
import com.utn.greenthumb.domain.model.Image
import com.utn.greenthumb.domain.model.Plant
import com.utn.greenthumb.domain.model.Taxonomy
import com.utn.greenthumb.domain.model.Watering
import com.utn.greenthumb.state.UiState
import com.utn.greenthumb.ui.theme.GreenBackground
import com.utn.greenthumb.viewmodel.PlantViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ResultScreen(
    onBackPressed: () -> Unit,
    plantViewModel: PlantViewModel,
    onSuccessfulIdentification: () -> Unit,
) {
    val uiState by plantViewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    var selectedPlantExternalId by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            CoroutineScope(Dispatchers.Main).launch {
                plantViewModel.clearResults()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenBackground),
                title = { Text(stringResource(R.string.identification_results)) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPressed,
                        enabled = !isProcessing
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_navigation)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                // En caso que entre en este estado, sale de la pantalla de Resultados
                UiState.Idle -> {
                    LaunchedEffect(Unit) {
                        onBackPressed()
                    }
                }

                // Contenido mientras está cargando
                UiState.Loading -> {
                    LoadingContent()
                }

                // Contenido ante un resultado exitoso
                is UiState.Success -> {
                    if (state.data.isNotEmpty()) {
                        SuccessContent(
                            plants = state.data,
                            selectedPlantExternalId = selectedPlantExternalId,
                            onPlantSelected = { plant ->
                                selectedPlantExternalId = if (selectedPlantExternalId == plant.externalId) {
                                    null
                                } else {
                                    plant.externalId
                                }
                            },
                            onSavePlant = { plant ->
                                scope.launch {
                                    isProcessing = true
                                    try {
                                        // TODO: Implementar guardado en BD
                                        Log.d("ResultScreen", "Saving plant: $plant")
                                        withContext(Dispatchers.IO) {
                                            plantViewModel.savePlant(plant)
                                            // navController.navigate("my_plants")
                                        }
                                        onSuccessfulIdentification()
                                        // navController.navigate("my_plants")
                                    } catch(e: Exception) {
                                        Log.e("ResultScreen", "Error saving plant", e)
                                    } finally {
                                        isProcessing = false
                                    }
                                }
                            },
                            onBackPressed = onBackPressed,
                            isProcessing = isProcessing
                        )
                    } else {
                        EmptyResultsContent(
                            onBackPressed = onBackPressed,
                            onRetry = {
                                // TODO: Agregar método para reintentar la identificación de la planta
                                // plantViewModel.identifyPlant(lastRequest)
                            }
                        )
                    }
                }

                // Contenido ante un mensaje de error
                is UiState.Error -> {
                    ErrorContent(
                        message = state.message,
                        throwable = state.throwable,
                        onBackPressed = onBackPressed,
                        onRetry = {
                            // TODO: Implementar Retry
                        }
                    )
                }
            }
        }
    }
}


@Composable
private fun LoadingContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.animateContentSize()
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.identifying_plant),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.alpha(0.7f)
        )
    }
}

@Composable
private fun SuccessContent(
    plants: List<Plant>,
    selectedPlantExternalId: String?,
    onPlantSelected: (Plant) -> Unit,
    onSavePlant: (Plant) -> Unit,
    onBackPressed: () -> Unit,
    isProcessing: Boolean
) {
    var selectedGalleryImages by remember { mutableStateOf<List<String>?>(null) }
    var selectedGalleryIndex by remember { mutableIntStateOf(0) }

    val selectedPlant = remember(selectedPlantExternalId, plants) {
        plants.find { it.externalId == selectedPlantExternalId }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.select_plant) + ":",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(
            items = plants,
            key = { it.name }
        ) { plant ->
            PlantResultCard(
                plant = plant,
                isSelected = plant.externalId == selectedPlantExternalId,
                onClick = { onPlantSelected(plant) },
                onImageClick = { imageIndex, images ->
                    selectedGalleryImages = images
                    selectedGalleryIndex = imageIndex
                }
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = selectedPlantExternalId != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column (
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            selectedPlant?.let { onSavePlant(it) }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isProcessing
                    ) {
                        if (isProcessing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(
                            text = if (isProcessing) {
                                stringResource(R.string.saving)
                            } else {
                                stringResource(R.string.save_plant)
                            }
                        )
                    }
                }
            }

            OutlinedButton(
                onClick = onBackPressed,
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing
            ) {
                Text(stringResource(R.string.back_navigation))
            }
        }
    }

    if (selectedGalleryImages != null) {
        FullScreenImageGallery(
            images = selectedGalleryImages!!,
            initialIndex = selectedGalleryIndex,
            onDismiss = {
                selectedGalleryImages = null
                selectedGalleryIndex = 0
            }
        )
    }
}


@Composable
fun FullScreenImageGallery(
    images: List<String>,
    initialIndex: Int = 0,
    onDismiss: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(initialIndex) }
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }
    val scope = rememberCoroutineScope()

    // Resetear transformaciones al cambiar de imagen
    LaunchedEffect(currentIndex) {
        scale = 1f
        offsetX = 0f
        offsetY = 0f
    }

    BackHandler(onBack = onDismiss)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.95f))
    ) {
        val pagerState = rememberPagerState(
            initialPage = initialIndex,
            pageCount = { images.size }
        )

        LaunchedEffect(pagerState.currentPage) {
            currentIndex = pagerState.currentPage
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = scale <= 1f
        ) { page ->
            ImageViewerPage(
                imageUrl = images[page],
                scale = scale,
                offsetX = offsetX,
                offsetY = offsetY,
                onScaleChange = { newScale ->
                    scale = newScale
                },
                onOffsetChange = { x, y ->
                    offsetX = x
                    offsetY = y
                }
            )
        }

        // Botón para salir de la galería
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopStart),
            color = Color.Black.copy(alpha = 0.7f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.close),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Contador de imágenes
                Text(
                    text = "${currentIndex + 1}/${images.size}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.width(48.dp))
            }
        }

        if (images.size > 1) {
            // Botón izquierda - Imagen anterior
            if (currentIndex > 0) {
                IconButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(currentIndex - 1)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .padding(start = 16.dp)
                        .size(48.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = stringResource(R.string.previous_image),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Botón derecha - Imagen siguiente
            if (currentIndex < images.size - 1) {
                IconButton(
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(currentIndex + 1)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .size(48.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.5f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = stringResource(R.string.next_image),
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = scale > 1f,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color.Black.copy(alpha = 0.7f)
            ) {
                Text(
                    text = "Zoom: ${(scale * 100).toInt()}%",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp
                    )
                )
            }
        }

        if (images.size > 1 && scale <= 1f) {
            Text(
                text = stringResource(R.string.slide_instruction),
                color = Color.White.copy(alpha = 0.5f),
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
private fun ImageViewerPage(
    imageUrl: String,
    scale: Float,
    offsetX: Float,
    offsetY: Float,
    onScaleChange: (Float) -> Unit,
    onOffsetChange: (Float, Float) -> Unit
) {
    var currentScale by remember { mutableFloatStateOf(scale) }
    var currentOffsetX by remember { mutableFloatStateOf(offsetX) }
    var currentOffsetY by remember { mutableFloatStateOf(offsetY) }

    LaunchedEffect(scale) {
        currentScale = scale
    }
    LaunchedEffect(offsetX) {
        currentOffsetX = offsetX
    }
    LaunchedEffect(offsetY) {
        currentOffsetY = offsetY
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTransformGestures { _, pan, zoom, _ ->

                    if (zoom != 1f) {
                        val newScale = (currentScale * zoom).coerceIn(1f, 5f)
                        currentScale = newScale
                        onScaleChange(newScale)
                    }

                    if (currentScale > 1f) {
                        val maxX = (size.width * (currentScale - 1)) / 2
                        val maxY = (size.height * (currentScale - 1)) / 2
                        val newOffsetX = (currentOffsetX + pan.x).coerceIn(-maxX, maxX)
                        val newOffsetY = (currentOffsetY + pan.y).coerceIn(-maxY, maxY)

                        currentOffsetX = newOffsetX
                        currentOffsetY = newOffsetY
                        onOffsetChange(newOffsetX, newOffsetY)
                    } else {
                        currentOffsetX = 0f
                        currentOffsetY = 0f
                        onOffsetChange(0f, 0f)
                    }
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        val newScale = if (currentScale >= 1.5f) 1f else 2f
                        currentScale = newScale
                        currentOffsetX = 0f
                        currentOffsetY = 0f

                        onScaleChange(newScale)
                        onOffsetChange(0f, 0f)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.full_screen_image),
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    scaleX = currentScale,
                    scaleY = currentScale,
                    translationX = currentOffsetX,
                    translationY = currentOffsetY
                ),
            contentScale = ContentScale.Fit
        )
    }
}


@Composable
private fun EmptyResultsContent(
    onBackPressed: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.no_results_title),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.no_results_message),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onBackPressed,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.back_navigation))
            }

            Button(
                onClick = onRetry,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.retry))
            }
        }
    }
}


@Composable
private fun ErrorContent(
    message: String,
    throwable: Throwable?,
    onBackPressed: () -> Unit,
    onRetry: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(R.string.string_error_ocurred),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedButton(
                onClick = onBackPressed,
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.back_navigation))
            }
            Button(
                onClick = onRetry,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.retry))
            }
        }
    }
}


/**
 * Preview
 */
@Preview(
    name = "Loading State",
    showBackground = true
)
@Preview(
    name = "Loading - Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun LoadingContentPreview() {
    MaterialTheme {
        Surface {
            LoadingContent()
        }
    }
}


@Preview
@Composable
private fun SuccessContentPreview(
) {
    val plants = listOf<Plant>(
        Plant(
            id = null,
            externalId = "662e0f8d4202acfc",
            name = "Rhaphiolepis bibas",
            probability = 0.8843,
            images = listOf(Image(
                url = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/Loquat-0.jpg/250px-Loquat-0.jpg")),
            commonNames = listOf("níspero japonés", "nisperero del Japón", "níspero"),
            synonyms = listOf(                                    "Crataegus bibas",
                "Eriobotrya fragrans",
                "Eriobotrya fragrans var. furfuracea",
                "Eriobotrya japonica",
                "Eriobotrya japonica f. variegata",
                "Mespilus japonica",
                "Photinia japonica",
                "Pyrus bibas",
                "Pyrus williamtelliana",
                "Rhaphiolepis loquata",
                "Rhaphiolepis williamtelliana",
                "Rhaphiolepis williamtelliana var. furfuracea"),
            taxonomy = Taxonomy(
                taxonomyClass = "Magnoliopsida",
                genus = "Rhaphiolepis",
                order = "Rosales",
                family = "Rosaceae",
                phylum = "Tracheophyta",
                kingdom = "Plantae"
            ),
            moreInfoUrl = "https://es.wikipedia.org/wiki/Eriobotrya_japonica",
            description = "Eriobotrya japonica, comúnmente llamado níspero japonés,\u200B nisperero del Japón\u200B o simplemente níspero, es un árbol frutal perenne de la familia Rosaceae,\u200B originario del sudeste de China,\u200B donde se conoce como pípá, 枇杷.\u200B Fue introducido en Japón, donde se naturalizó y donde lleva cultivándose más de mil años. También se naturalizó en la India, la cuenca mediterránea, Canarias, Pakistán, Chile, Argentina , Ecuador,Costa Rica y muchas otras áreas. Se cree que la inmigración china llevó el níspero a Hawái.\nSe menciona a menudo en la antigua literatura china, por ejemplo en los poemas de Li Bai, y en la literatura portuguesa se conoce desde la era de los descubrimientos.\nEn noviembre se celebra el Festival del Níspero en San Juan del Obispo, Guatemala.\nEl fruto de esta especie ha ido sustituyendo al del níspero europeo (Mespilus germanica), de forma que, en la actualidad, al hablar de «níspero» se sobreentiende que se está haciendo referencia al japonés.",
            watering = Watering(
                min = 2,
                max = 2
            )
        ),
        Plant(
            id = null,
            externalId = "662e0f8d4202aabc",
            name = "Rhaphiolepis bibas",
            probability = 0.8843,
            images = listOf(Image(
                url = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/Loquat-0.jpg/250px-Loquat-0.jpg")),
            commonNames = listOf("níspero japonés", "nisperero del Japón", "níspero"),
            synonyms = listOf(                                    "Crataegus bibas",
                "Eriobotrya fragrans",
                "Eriobotrya fragrans var. furfuracea",
                "Eriobotrya japonica",
                "Eriobotrya japonica f. variegata",
                "Mespilus japonica",
                "Photinia japonica",
                "Pyrus bibas",
                "Pyrus williamtelliana",
                "Rhaphiolepis loquata",
                "Rhaphiolepis williamtelliana",
                "Rhaphiolepis williamtelliana var. furfuracea"),
            taxonomy = Taxonomy(
                taxonomyClass = "Magnoliopsida",
                genus = "Rhaphiolepis",
                order = "Rosales",
                family = "Rosaceae",
                phylum = "Tracheophyta",
                kingdom = "Plantae"
            ),
            moreInfoUrl = "https://es.wikipedia.org/wiki/Eriobotrya_japonica",
            description = "Eriobotrya japonica, comúnmente llamado níspero japonés,\u200B nisperero del Japón\u200B o simplemente níspero, es un árbol frutal perenne de la familia Rosaceae,\u200B originario del sudeste de China,\u200B donde se conoce como pípá, 枇杷.\u200B Fue introducido en Japón, donde se naturalizó y donde lleva cultivándose más de mil años. También se naturalizó en la India, la cuenca mediterránea, Canarias, Pakistán, Chile, Argentina , Ecuador,Costa Rica y muchas otras áreas. Se cree que la inmigración china llevó el níspero a Hawái.\nSe menciona a menudo en la antigua literatura china, por ejemplo en los poemas de Li Bai, y en la literatura portuguesa se conoce desde la era de los descubrimientos.\nEn noviembre se celebra el Festival del Níspero en San Juan del Obispo, Guatemala.\nEl fruto de esta especie ha ido sustituyendo al del níspero europeo (Mespilus germanica), de forma que, en la actualidad, al hablar de «níspero» se sobreentiende que se está haciendo referencia al japonés.",
            watering = Watering(
                min = 2,
                max = 2
            )
        )
    )
    SuccessContent(
        plants = plants,
        selectedPlantExternalId = null,
        onPlantSelected = {},
        onSavePlant = {},
        onBackPressed = {},
        isProcessing = false
    )
}


@Preview
@Composable
private fun SuccessContentSelectedPreview(
) {
    val plants = listOf<Plant>(
        Plant(
            id = null,
            externalId = "662e0f8d4202acfc",
            name = "Rhaphiolepis bibas",
            probability = 0.8843,
            images = listOf(Image(
                url = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/Loquat-0.jpg/250px-Loquat-0.jpg")),
            commonNames = listOf("níspero japonés", "nisperero del Japón", "níspero"),
            synonyms = listOf(                                    "Crataegus bibas",
                "Eriobotrya fragrans",
                "Eriobotrya fragrans var. furfuracea",
                "Eriobotrya japonica",
                "Eriobotrya japonica f. variegata",
                "Mespilus japonica",
                "Photinia japonica",
                "Pyrus bibas",
                "Pyrus williamtelliana",
                "Rhaphiolepis loquata",
                "Rhaphiolepis williamtelliana",
                "Rhaphiolepis williamtelliana var. furfuracea"),
            taxonomy = Taxonomy(
                taxonomyClass = "Magnoliopsida",
                genus = "Rhaphiolepis",
                order = "Rosales",
                family = "Rosaceae",
                phylum = "Tracheophyta",
                kingdom = "Plantae"
            ),
            moreInfoUrl = "https://es.wikipedia.org/wiki/Eriobotrya_japonica",
            description = "Eriobotrya japonica, comúnmente llamado níspero japonés,\u200B nisperero del Japón\u200B o simplemente níspero, es un árbol frutal perenne de la familia Rosaceae,\u200B originario del sudeste de China,\u200B donde se conoce como pípá, 枇杷.\u200B Fue introducido en Japón, donde se naturalizó y donde lleva cultivándose más de mil años. También se naturalizó en la India, la cuenca mediterránea, Canarias, Pakistán, Chile, Argentina , Ecuador,Costa Rica y muchas otras áreas. Se cree que la inmigración china llevó el níspero a Hawái.\nSe menciona a menudo en la antigua literatura china, por ejemplo en los poemas de Li Bai, y en la literatura portuguesa se conoce desde la era de los descubrimientos.\nEn noviembre se celebra el Festival del Níspero en San Juan del Obispo, Guatemala.\nEl fruto de esta especie ha ido sustituyendo al del níspero europeo (Mespilus germanica), de forma que, en la actualidad, al hablar de «níspero» se sobreentiende que se está haciendo referencia al japonés.",
            watering = Watering(
                min = 2,
                max = 2
            )
        ),
        Plant(
            id = null,
            externalId = "662e0f8d4202aabc",
            name = "Rhaphiolepis bibas",
            probability = 0.8843,
            images = listOf(Image(
                url = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/Loquat-0.jpg/250px-Loquat-0.jpg")),
            commonNames = listOf("níspero japonés", "nisperero del Japón", "níspero"),
            synonyms = listOf(                                    "Crataegus bibas",
                "Eriobotrya fragrans",
                "Eriobotrya fragrans var. furfuracea",
                "Eriobotrya japonica",
                "Eriobotrya japonica f. variegata",
                "Mespilus japonica",
                "Photinia japonica",
                "Pyrus bibas",
                "Pyrus williamtelliana",
                "Rhaphiolepis loquata",
                "Rhaphiolepis williamtelliana",
                "Rhaphiolepis williamtelliana var. furfuracea"),
            taxonomy = Taxonomy(
                taxonomyClass = "Magnoliopsida",
                genus = "Rhaphiolepis",
                order = "Rosales",
                family = "Rosaceae",
                phylum = "Tracheophyta",
                kingdom = "Plantae"
            ),
            moreInfoUrl = "https://es.wikipedia.org/wiki/Eriobotrya_japonica",
            description = "Eriobotrya japonica, comúnmente llamado níspero japonés,\u200B nisperero del Japón\u200B o simplemente níspero, es un árbol frutal perenne de la familia Rosaceae,\u200B originario del sudeste de China,\u200B donde se conoce como pípá, 枇杷.\u200B Fue introducido en Japón, donde se naturalizó y donde lleva cultivándose más de mil años. También se naturalizó en la India, la cuenca mediterránea, Canarias, Pakistán, Chile, Argentina , Ecuador,Costa Rica y muchas otras áreas. Se cree que la inmigración china llevó el níspero a Hawái.\nSe menciona a menudo en la antigua literatura china, por ejemplo en los poemas de Li Bai, y en la literatura portuguesa se conoce desde la era de los descubrimientos.\nEn noviembre se celebra el Festival del Níspero en San Juan del Obispo, Guatemala.\nEl fruto de esta especie ha ido sustituyendo al del níspero europeo (Mespilus germanica), de forma que, en la actualidad, al hablar de «níspero» se sobreentiende que se está haciendo referencia al japonés.",
            watering = Watering(
                min = 2,
                max = 2
            )
        ))
    SuccessContent(
        plants = plants,
        selectedPlantExternalId = "662e0f8d4202acfc",
        onPlantSelected = {},
        onSavePlant = {},
        onBackPressed = {},
        isProcessing = true
    )
}


@Preview
@Composable
private fun EmptyResultsContentPreview() {
    MaterialTheme {
        Surface {
            EmptyResultsContent(
                onRetry = {},
                onBackPressed = {}
            )
        }
    }
}


@Preview
@Composable
private fun ErrorContentPreview() {
    MaterialTheme {
        Surface {
            ErrorContent(
                message = "No se pudo conectar con el servidor. Verifica tu conexión a internet.",
                throwable = null,
                onBackPressed = {},
                onRetry = {}
            )
        }
    }
}
package com.utn.greenthumb.ui.main.result

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.alpha
import com.utn.greenthumb.domain.model.Plant
import com.utn.greenthumb.state.UiState
import com.utn.greenthumb.viewmodel.PlantViewModel
import com.utn.greenthumb.R
import com.utn.greenthumb.domain.model.SimilarImage
import com.utn.greenthumb.domain.model.Taxonomy
import com.utn.greenthumb.domain.model.Watering

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ResultScreen(
    imageUri: String?,
    navController: NavHostController,
    onBackPressed: () -> Unit,
    plantViewModel: PlantViewModel
) {
    val uiState by plantViewModel.uiState.collectAsState()
    var selectedPlantId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.identification_results)) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
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
                UiState.Idle -> {
                    IdleContent()
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
                            selectedPlantId = selectedPlantId,
                            onPlantSelected = { plant ->
                                selectedPlantId = plant.id
                            },
                            onSavePlant = { plant ->
                                // TODO: Implementar guardado en BD
                                // plantViewModel.savePlant(plant)
                                // navController.navigate("my_plants")
                            },
                            onBackPressed = onBackPressed
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
private fun IdleContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(24.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Info,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.waiting_for_action),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
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
    selectedPlantId: String?,
    onPlantSelected: (Plant) -> Unit,
    onSavePlant: (Plant) -> Unit,
    onBackPressed: () -> Unit
) {
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
            key = { it.id }
        ) { plant ->
            PlantResultCard(
                plant = plant,
                isSelected = plant.id == selectedPlantId,
                onClick = { onPlantSelected(plant) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(visible = selectedPlantId != null) {
                Column {
                    Button(
                        onClick = {
                            plants.find { it.id == selectedPlantId }?.let { onSavePlant(it) }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.save_plant)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            OutlinedButton(
                onClick = onBackPressed,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.back_navigation))
            }
        }
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
    name = "Idle State",
    showBackground = true
)
@Composable
private fun IdleContentPreview() {
    MaterialTheme {
        Surface {
            IdleContent()
        }
    }
}


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
        id = "662e0f8d4202acfc",
        name = "Rhaphiolepis bibas",
        probability = 0.8843,
        similarImages = listOf(SimilarImage(
            url = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/Loquat-0.jpg/250px-Loquat-0.jpg",
            similarity = 0.789)),
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
        url = "https://es.wikipedia.org/wiki/Eriobotrya_japonica",
        rank = "species",
        description = "Eriobotrya japonica, comúnmente llamado níspero japonés,\u200B nisperero del Japón\u200B o simplemente níspero, es un árbol frutal perenne de la familia Rosaceae,\u200B originario del sudeste de China,\u200B donde se conoce como pípá, 枇杷.\u200B Fue introducido en Japón, donde se naturalizó y donde lleva cultivándose más de mil años. También se naturalizó en la India, la cuenca mediterránea, Canarias, Pakistán, Chile, Argentina , Ecuador,Costa Rica y muchas otras áreas. Se cree que la inmigración china llevó el níspero a Hawái.\nSe menciona a menudo en la antigua literatura china, por ejemplo en los poemas de Li Bai, y en la literatura portuguesa se conoce desde la era de los descubrimientos.\nEn noviembre se celebra el Festival del Níspero en San Juan del Obispo, Guatemala.\nEl fruto de esta especie ha ido sustituyendo al del níspero europeo (Mespilus germanica), de forma que, en la actualidad, al hablar de «níspero» se sobreentiende que se está haciendo referencia al japonés.",
        watering = Watering(
            min = 2,
            max = 2
        )
    ),
        Plant(
        id = "662e0f8d4202aabc",
        name = "Rhaphiolepis bibas",
        probability = 0.8843,
        similarImages = listOf(SimilarImage(
            url = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/Loquat-0.jpg/250px-Loquat-0.jpg",
            similarity = 0.789)),
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
        url = "https://es.wikipedia.org/wiki/Eriobotrya_japonica",
        rank = "species",
        description = "Eriobotrya japonica, comúnmente llamado níspero japonés,\u200B nisperero del Japón\u200B o simplemente níspero, es un árbol frutal perenne de la familia Rosaceae,\u200B originario del sudeste de China,\u200B donde se conoce como pípá, 枇杷.\u200B Fue introducido en Japón, donde se naturalizó y donde lleva cultivándose más de mil años. También se naturalizó en la India, la cuenca mediterránea, Canarias, Pakistán, Chile, Argentina , Ecuador,Costa Rica y muchas otras áreas. Se cree que la inmigración china llevó el níspero a Hawái.\nSe menciona a menudo en la antigua literatura china, por ejemplo en los poemas de Li Bai, y en la literatura portuguesa se conoce desde la era de los descubrimientos.\nEn noviembre se celebra el Festival del Níspero en San Juan del Obispo, Guatemala.\nEl fruto de esta especie ha ido sustituyendo al del níspero europeo (Mespilus germanica), de forma que, en la actualidad, al hablar de «níspero» se sobreentiende que se está haciendo referencia al japonés.",
        watering = Watering(
            min = 2,
            max = 2
        )
    ))
    SuccessContent(
        plants = plants,
        selectedPlantId = null,
        onPlantSelected = {},
        onSavePlant = {},
        onBackPressed = {}
    )
}


@Preview
@Composable
private fun SuccessContentSelectedPreview(
) {
    val plants = listOf<Plant>(
        Plant(
            id = "662e0f8d4202acfc",
            name = "Rhaphiolepis bibas",
            probability = 0.8843,
            similarImages = listOf(SimilarImage(
                url = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/Loquat-0.jpg/250px-Loquat-0.jpg",
                similarity = 0.789)),
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
            url = "https://es.wikipedia.org/wiki/Eriobotrya_japonica",
            rank = "species",
            description = "Eriobotrya japonica, comúnmente llamado níspero japonés,\u200B nisperero del Japón\u200B o simplemente níspero, es un árbol frutal perenne de la familia Rosaceae,\u200B originario del sudeste de China,\u200B donde se conoce como pípá, 枇杷.\u200B Fue introducido en Japón, donde se naturalizó y donde lleva cultivándose más de mil años. También se naturalizó en la India, la cuenca mediterránea, Canarias, Pakistán, Chile, Argentina , Ecuador,Costa Rica y muchas otras áreas. Se cree que la inmigración china llevó el níspero a Hawái.\nSe menciona a menudo en la antigua literatura china, por ejemplo en los poemas de Li Bai, y en la literatura portuguesa se conoce desde la era de los descubrimientos.\nEn noviembre se celebra el Festival del Níspero en San Juan del Obispo, Guatemala.\nEl fruto de esta especie ha ido sustituyendo al del níspero europeo (Mespilus germanica), de forma que, en la actualidad, al hablar de «níspero» se sobreentiende que se está haciendo referencia al japonés.",
            watering = Watering(
                min = 2,
                max = 2
            )
        ),
        Plant(
            id = "662e0f8d4202aabc",
            name = "Rhaphiolepis bibas",
            probability = 0.8843,
            similarImages = listOf(SimilarImage(
                url = "https://upload.wikimedia.org/wikipedia/commons/thumb/1/17/Loquat-0.jpg/250px-Loquat-0.jpg",
                similarity = 0.789)),
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
            url = "https://es.wikipedia.org/wiki/Eriobotrya_japonica",
            rank = "species",
            description = "Eriobotrya japonica, comúnmente llamado níspero japonés,\u200B nisperero del Japón\u200B o simplemente níspero, es un árbol frutal perenne de la familia Rosaceae,\u200B originario del sudeste de China,\u200B donde se conoce como pípá, 枇杷.\u200B Fue introducido en Japón, donde se naturalizó y donde lleva cultivándose más de mil años. También se naturalizó en la India, la cuenca mediterránea, Canarias, Pakistán, Chile, Argentina , Ecuador,Costa Rica y muchas otras áreas. Se cree que la inmigración china llevó el níspero a Hawái.\nSe menciona a menudo en la antigua literatura china, por ejemplo en los poemas de Li Bai, y en la literatura portuguesa se conoce desde la era de los descubrimientos.\nEn noviembre se celebra el Festival del Níspero en San Juan del Obispo, Guatemala.\nEl fruto de esta especie ha ido sustituyendo al del níspero europeo (Mespilus germanica), de forma que, en la actualidad, al hablar de «níspero» se sobreentiende que se está haciendo referencia al japonés.",
            watering = Watering(
                min = 2,
                max = 2
            )
        ))
    SuccessContent(
        plants = plants,
        selectedPlantId = "662e0f8d4202acfc",
        onPlantSelected = {},
        onSavePlant = {},
        onBackPressed = {}
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
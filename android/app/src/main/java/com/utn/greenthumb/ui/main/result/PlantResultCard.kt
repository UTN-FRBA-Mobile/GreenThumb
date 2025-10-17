package com.utn.greenthumb.ui.main.result

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.utn.greenthumb.R
import com.utn.greenthumb.domain.model.PlantDTO
import com.utn.greenthumb.domain.model.ImageDTO
import com.utn.greenthumb.domain.model.TaxonomyDTO
import com.utn.greenthumb.domain.model.WateringDTO
import com.utn.greenthumb.ui.theme.PurpleCard
import kotlin.String


@Composable
fun PlantResultCard(
    plant: PlantDTO,
    isSelected: Boolean = false,
    onClick: () -> Unit = {},
    onImageClick: ((imageIndex: Int, images: List<String>) -> Unit)? = null
) {

    val elevation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 2.dp,
        label = "elevation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick,
            ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
            } else {
                PurpleCard
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header con nombre y probabilidad
                PlantHeader(
                    name = plant.name,
                    probability = plant.probability,
                    isSelected = isSelected
                )

            // Nombres comunes
            if (plant.commonNames.isNotEmpty()) {
                CommonNamesSection(
                    commonNames = plant.commonNames
                )
            }

            // Imágenes similares
            if (plant.images?.isNotEmpty() ?: false) {
                SimilarImagesSection(
                    images = plant.images,
                    onImageClick = onImageClick
                )
            }

            // Sinónimos
            if (plant.synonyms?.isNotEmpty() ?: false) {
                ExpandableSectionContainer(
                    title = stringResource(R.string.synonyms),
                    collapsedContent = {
                        Text(
                            text = plant.synonyms.joinToString(", "),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    expandedContent = {
                        Text(
                            text = plant.synonyms.joinToString(", "),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }

            // Descripción
            if (plant.description.isNotEmpty()) {
                ExpandableSectionContainer(
                    title = stringResource(R.string.plant_description),
                    collapsedContent = {
                        Text(
                            text = plant.description,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    expandedContent = {
                        Text(
                            text = plant.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                )
            }

            // Taxonomía
            if (plant.taxonomy != null) {
                TaxonomySection(
                    taxonomy = plant.taxonomy
                )
            }

            // URL
            if (plant.moreInfoUrl?.isNotEmpty() ?: false) {
                MoreInfoSection(
                    url = plant.moreInfoUrl
                )
            }
        }
    }
}


@Composable
private fun PlantHeader(
    name: String,
    probability: Double,
    isSelected: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { contentDescription = "Encabezado de la Planta" },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nombre científico de la planta
            Text(
                text = name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(weight = 1f, fill = false)
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Check que indica que la planta fue seleccionada
            AnimatedVisibility(
                visible = isSelected,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = stringResource(R.string.plant_selected),
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Probabilidad
        ProbabilityBadge(
            probability = probability,
        )
    }
}


@Composable
private fun ProbabilityBadge(
    probability: Double,
    modifier: Modifier = Modifier
) {
    val percentage = (probability * 100).toInt()
    val (backgroundColor, fontColor, icon) = when {
        percentage >= 80 -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            Icons.Default.CheckCircle
        )
        percentage >= 60 -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            Icons.Default.Warning
        )
        else -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            Icons.Default.Close
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = stringResource(R.string.probability_percentage, percentage),
                tint = fontColor,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.labelMedium,
                color = fontColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Composable
private fun CommonNamesSection(
    commonNames: List<String>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        SectionTitle(
            title = stringResource(R.string.common_names)
        )

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(commonNames) { name ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun SimilarImagesSection(
    images: List<ImageDTO>,
    modifier: Modifier = Modifier,
    onImageClick: ((imageIndex: Int, images: List<String>) -> Unit)? = null
) {
    Column(modifier = modifier) {
        SectionTitle(title = stringResource(R.string.similar_images))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(
                items = images,
                key = { _, image -> image.url }
            ) { index, image ->
                Surface(
                    modifier = Modifier
                        .size(80.dp)
                        .clickable {
                            onImageClick?.invoke(index, images.map { it.url })
                        },
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(image.url)
                            .crossfade(true)
                            .memoryCachePolicy(CachePolicy.ENABLED)
                            .diskCachePolicy(CachePolicy.ENABLED)
                            .build(),
                        contentDescription = stringResource(R.string.similar_image),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ExpandableSectionContainer(
    title: String,
    collapsedContent: @Composable () -> Unit,
    expandedContent: @Composable () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        isExpanded = !isExpanded
                    }
                )
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$title:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) {
                    Icons.Default.KeyboardArrowUp
                } else {
                    Icons.Default.KeyboardArrowDown
                },
                contentDescription = if (isExpanded) {
                    stringResource(R.string.collapse_section)
                } else {
                    stringResource(R.string.expand_section)
                },
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(
                animationSpec = tween(300, easing = EaseInOut)
            ) + fadeIn(animationSpec = tween(300)),
            exit = shrinkVertically(
                animationSpec = tween(300, easing = EaseInOut)
            ) + fadeOut(animationSpec = tween(300))
        ) {
            expandedContent()
        }

        if (!isExpanded) {
            collapsedContent()
        }

    }
}


@Composable
private fun TaxonomySection(
    taxonomy: TaxonomyDTO,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        isExpanded = !isExpanded
                    }
                )
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.taxonomy) + ":",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) {
                    stringResource(R.string.collapse_section)
                } else {
                    stringResource(R.string.expand_section)
                },
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically(
                animationSpec = tween(300, easing = EaseInOut)
            ) + fadeIn(animationSpec = tween(300)),
            exit = shrinkVertically(
                animationSpec = tween(300, easing = EaseInOut)
            ) + fadeOut(animationSpec = tween(300))
        ) {
            Column(
                modifier = Modifier.padding(start = 28.dp, top = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (taxonomy.taxonomyClass != null) {
                    TaxonomyItem(
                        label = stringResource(R.string.taxonomy_class),
                        value = taxonomy.taxonomyClass
                    )
                }

                if (taxonomy.genus != null) {
                    TaxonomyItem(
                        label = stringResource(R.string.taxonomy_genus),
                        value = taxonomy.genus
                    )
                }

                if (taxonomy.order != null) {
                    TaxonomyItem(
                        label = stringResource(R.string.taxonomy_order),
                        value = taxonomy.order
                    )
                }

                if (taxonomy.family != null) {
                    TaxonomyItem(
                        label = stringResource(R.string.taxonomy_family),
                        value = taxonomy.family
                    )
                }

                if (taxonomy.phylum != null) {
                    TaxonomyItem(
                        label = stringResource(R.string.taxonomy_phylum),
                        value = taxonomy.phylum
                    )
                }

                if (taxonomy.kingdom != null) {
                    TaxonomyItem(
                        label = stringResource(R.string.taxonomy_kingdom),
                        value = taxonomy.kingdom
                    )
                }
            }
        }

        // Preview cuando está colapsado
        if (!isExpanded) {
            Text(
                text = "${stringResource(R.string.taxonomy_class)}: ${taxonomy.taxonomyClass} • ${stringResource(R.string.taxonomy_genus)}: ${taxonomy.genus}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 28.dp, top = 4.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Composable
private fun TaxonomyItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.width(80.dp)
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
    }
}


@SuppressLint("QueryPermissionsNeeded")
@Composable
private fun MoreInfoSection(
    url: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(modifier = modifier) {
        SectionTitle(
            title = stringResource(R.string.more_info),
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable (
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {
                        try {
                            if (url.isBlank()) {
                                errorMessage = "URL no válida"
                                showErrorDialog = true
                                return@clickable
                            }

                            try {
                                uriHandler.openUri(url)
                            } catch (e: Exception) {
                                val intent = Intent(Intent.ACTION_VIEW, url.toUri()).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                }
                                if (intent.resolveActivity(context.packageManager) != null) {
                                    context.startActivity(intent)
                                } else {
                                    errorMessage =
                                        "No hay aplicaciones disponibles para abrir enlaces web. Por favor, instala un navegador."
                                    showErrorDialog = true
                                }

                            }

                            Log.d("MoreInfoSection", "Successfully opened URL: $url")

                        } catch (e: Exception) {
                            Log.e("PlantResultCard", "Error opening URL: $url", e)
                            errorMessage = when (e) {
                                is ActivityNotFoundException -> "No se encontró una aplicación para abrir el enlace"
                                is SecurityException -> "El enlace fue bloqueado por razones de seguridad"
                                is IllegalArgumentException -> "El enlace no es válido"
                                else -> "Ocurrió un error inesperado al abrir el enlace"
                            }
                            showErrorDialog = true
                        }
                    }
                ),
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = url,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Abrir enlace externo",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Diálogo de error
        if (showErrorDialog) {
            AlertDialog(
                onDismissRequest = { showErrorDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = {
                    Text(stringResource(R.string.error_open_url))
                },
                text = {
                    Text(errorMessage)
                },
                confirmButton = {
                    TextButton(
                        onClick = { showErrorDialog = false }
                    ) {
                        Text(stringResource(R.string.understood))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showErrorDialog = false
                            // Copiar URL al clipboard como alternativa
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Plant URL", url)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(
                                context,
                                "URL copiada al portapapeles",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    ) {
                        Text(stringResource(R.string.copy_url))
                    }
                }
            )
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$title:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


/**
 * Previews
 */
@Preview (
    name = "Plant Result Card - No Selected")
@Composable
fun PlantResultCardPreview() {
    val plant = PlantDTO(
        id = null,
        externalId = "662e0f8d4202acfc",
        name = "Rhaphiolepis bibas",
        probability = 0.8843,
        images = listOf(ImageDTO(
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
        taxonomy = TaxonomyDTO(
            taxonomyClass = "Magnoliopsida",
            genus = "Rhaphiolepis",
            order = "Rosales",
            family = "Rosaceae",
            phylum = "Tracheophyta",
            kingdom = "Plantae"
        ),
        moreInfoUrl = "https://es.wikipedia.org/wiki/Eriobotrya_japonica",
        description = "Eriobotrya japonica, comúnmente llamado níspero japonés,\u200B nisperero del Japón\u200B o simplemente níspero, es un árbol frutal perenne de la familia Rosaceae,\u200B originario del sudeste de China,\u200B donde se conoce como pípá, 枇杷.\u200B Fue introducido en Japón, donde se naturalizó y donde lleva cultivándose más de mil años. También se naturalizó en la India, la cuenca mediterránea, Canarias, Pakistán, Chile, Argentina , Ecuador,Costa Rica y muchas otras áreas. Se cree que la inmigración china llevó el níspero a Hawái.\nSe menciona a menudo en la antigua literatura china, por ejemplo en los poemas de Li Bai, y en la literatura portuguesa se conoce desde la era de los descubrimientos.\nEn noviembre se celebra el Festival del Níspero en San Juan del Obispo, Guatemala.\nEl fruto de esta especie ha ido sustituyendo al del níspero europeo (Mespilus germanica), de forma que, en la actualidad, al hablar de «níspero» se sobreentiende que se está haciendo referencia al japonés.",
        watering = WateringDTO(
            min = 2,
            max = 2
        ),
        bestWatering = "Watering this plant properly is crucial for its health. It prefers a moderate amount of water, so it's best to keep the soil consistently moist but not waterlogged. Overwatering can lead to root rot, while underwatering can cause the leaves to wilt and drop. During the growing season, water the plant more frequently, but reduce the amount in the winter when the plant's growth slows down. Always check the top inch of soil; if it feels dry, it's time to water.",
        propagationMethods = listOf("cuttings", "seeds"),
        culturalSignificance = "In various cultures, this plant is appreciated for its ornamental value. It is often used in landscaping for its attractive flowers and evergreen foliage. In some regions, it is also associated with good luck and prosperity. The plant's resilience and beauty make it a popular choice for public gardens and private yards alike.",
        bestLightCondition = "This plant thrives in full sun to partial shade. It needs at least six hours of direct sunlight each day to grow well and produce flowers. In hotter climates, some afternoon shade can help protect it from the intense midday sun. If grown indoors, place it near a south or west-facing window where it can receive plenty of light. Insufficient light can lead to poor growth and fewer blooms.",
        commonUses = "Common uses for this plant include ornamental landscaping and hedging. Its dense growth habit makes it an excellent choice for creating privacy screens or windbreaks. The plant's flowers attract pollinators like bees and butterflies, making it beneficial for the local ecosystem. Additionally, it can be grown in containers, making it versatile for use in patios and small garden spaces.",
        toxicity = "This plant is generally considered non-toxic to both humans and animals. There are no known harmful effects if ingested, making it a safe choice for gardens and homes with pets and children. However, it's always a good idea to discourage pets and kids from chewing on any plant material to avoid any potential digestive upset."
    )
    PlantResultCard(
        plant = plant,
        isSelected = false,
        onClick = {}
    )
}


@Preview (
    name = "Plant Result Card - Selected")
@Composable
fun PlantResultCardSelectedPreview() {
    val plant = PlantDTO(
        id = null,
        externalId = "662e0f8d4202acfc",
        name = "Rhaphiolepis bibas",
        probability = 0.8843,
        images = listOf(ImageDTO(
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
        taxonomy = TaxonomyDTO(
            taxonomyClass = "Magnoliopsida",
            genus = "Rhaphiolepis",
            order = "Rosales",
            family = "Rosaceae",
            phylum = "Tracheophyta",
            kingdom = "Plantae"
        ),
        moreInfoUrl = "https://es.wikipedia.org/wiki/Eriobotrya_japonica",
        description = "Eriobotrya japonica, comúnmente llamado níspero japonés,\u200B nisperero del Japón\u200B o simplemente níspero, es un árbol frutal perenne de la familia Rosaceae,\u200B originario del sudeste de China,\u200B donde se conoce como pípá, 枇杷.\u200B Fue introducido en Japón, donde se naturalizó y donde lleva cultivándose más de mil años. También se naturalizó en la India, la cuenca mediterránea, Canarias, Pakistán, Chile, Argentina , Ecuador,Costa Rica y muchas otras áreas. Se cree que la inmigración china llevó el níspero a Hawái.\nSe menciona a menudo en la antigua literatura china, por ejemplo en los poemas de Li Bai, y en la literatura portuguesa se conoce desde la era de los descubrimientos.\nEn noviembre se celebra el Festival del Níspero en San Juan del Obispo, Guatemala.\nEl fruto de esta especie ha ido sustituyendo al del níspero europeo (Mespilus germanica), de forma que, en la actualidad, al hablar de «níspero» se sobreentiende que se está haciendo referencia al japonés.",
        watering = WateringDTO(
            min = 2,
            max = 2
        ),
        bestWatering = "Watering this plant properly is crucial for its health. It prefers a moderate amount of water, so it's best to keep the soil consistently moist but not waterlogged. Overwatering can lead to root rot, while underwatering can cause the leaves to wilt and drop. During the growing season, water the plant more frequently, but reduce the amount in the winter when the plant's growth slows down. Always check the top inch of soil; if it feels dry, it's time to water.",
        propagationMethods = listOf("cuttings", "seeds"),
        culturalSignificance = "In various cultures, this plant is appreciated for its ornamental value. It is often used in landscaping for its attractive flowers and evergreen foliage. In some regions, it is also associated with good luck and prosperity. The plant's resilience and beauty make it a popular choice for public gardens and private yards alike.",
        bestLightCondition = "This plant thrives in full sun to partial shade. It needs at least six hours of direct sunlight each day to grow well and produce flowers. In hotter climates, some afternoon shade can help protect it from the intense midday sun. If grown indoors, place it near a south or west-facing window where it can receive plenty of light. Insufficient light can lead to poor growth and fewer blooms.",
        commonUses = "Common uses for this plant include ornamental landscaping and hedging. Its dense growth habit makes it an excellent choice for creating privacy screens or windbreaks. The plant's flowers attract pollinators like bees and butterflies, making it beneficial for the local ecosystem. Additionally, it can be grown in containers, making it versatile for use in patios and small garden spaces.",
        toxicity = "This plant is generally considered non-toxic to both humans and animals. There are no known harmful effects if ingested, making it a safe choice for gardens and homes with pets and children. However, it's always a good idea to discourage pets and kids from chewing on any plant material to avoid any potential digestive upset."
    )
    PlantResultCard(
        plant = plant,
        isSelected = true,
        onClick = {}
    )
}


@Preview (
    name = "Plant Result Card - High Probability")
@Composable
fun PlantResultCardHighProbabilityPreview() {
    val plant = PlantDTO(
        id = null,
        externalId = "662e0f8d4202acfc",
        name = "Rhaphiolepis bibas",
        probability = 0.99,
        images = listOf(ImageDTO(
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
        taxonomy = TaxonomyDTO(
            taxonomyClass = "Magnoliopsida",
            genus = "Rhaphiolepis",
            order = "Rosales",
            family = "Rosaceae",
            phylum = "Tracheophyta",
            kingdom = "Plantae"
        ),
        moreInfoUrl = "https://es.wikipedia.org/wiki/Eriobotrya_japonica",
        description = "Eriobotrya japonica, comúnmente llamado níspero japonés,\u200B nisperero del Japón\u200B o simplemente níspero, es un árbol frutal perenne de la familia Rosaceae,\u200B originario del sudeste de China,\u200B donde se conoce como pípá, 枇杷.\u200B Fue introducido en Japón, donde se naturalizó y donde lleva cultivándose más de mil años. También se naturalizó en la India, la cuenca mediterránea, Canarias, Pakistán, Chile, Argentina , Ecuador,Costa Rica y muchas otras áreas. Se cree que la inmigración china llevó el níspero a Hawái.\nSe menciona a menudo en la antigua literatura china, por ejemplo en los poemas de Li Bai, y en la literatura portuguesa se conoce desde la era de los descubrimientos.\nEn noviembre se celebra el Festival del Níspero en San Juan del Obispo, Guatemala.\nEl fruto de esta especie ha ido sustituyendo al del níspero europeo (Mespilus germanica), de forma que, en la actualidad, al hablar de «níspero» se sobreentiende que se está haciendo referencia al japonés.",
        watering = WateringDTO(
            min = 2,
            max = 2
        ),
        bestWatering = "Watering this plant properly is crucial for its health. It prefers a moderate amount of water, so it's best to keep the soil consistently moist but not waterlogged. Overwatering can lead to root rot, while underwatering can cause the leaves to wilt and drop. During the growing season, water the plant more frequently, but reduce the amount in the winter when the plant's growth slows down. Always check the top inch of soil; if it feels dry, it's time to water.",
        propagationMethods = listOf("cuttings", "seeds"),
        culturalSignificance = "In various cultures, this plant is appreciated for its ornamental value. It is often used in landscaping for its attractive flowers and evergreen foliage. In some regions, it is also associated with good luck and prosperity. The plant's resilience and beauty make it a popular choice for public gardens and private yards alike.",
        bestLightCondition = "This plant thrives in full sun to partial shade. It needs at least six hours of direct sunlight each day to grow well and produce flowers. In hotter climates, some afternoon shade can help protect it from the intense midday sun. If grown indoors, place it near a south or west-facing window where it can receive plenty of light. Insufficient light can lead to poor growth and fewer blooms.",
        commonUses = "Common uses for this plant include ornamental landscaping and hedging. Its dense growth habit makes it an excellent choice for creating privacy screens or windbreaks. The plant's flowers attract pollinators like bees and butterflies, making it beneficial for the local ecosystem. Additionally, it can be grown in containers, making it versatile for use in patios and small garden spaces.",
        toxicity = "This plant is generally considered non-toxic to both humans and animals. There are no known harmful effects if ingested, making it a safe choice for gardens and homes with pets and children. However, it's always a good idea to discourage pets and kids from chewing on any plant material to avoid any potential digestive upset."
    )
    PlantResultCard(
        plant = plant,
        isSelected = false,
        onClick = {}
    )
}


@Preview (
    name = "Plant Result Card - Medium Probability")
@Composable
fun PlantResultCardMediumProbabilityPreview() {
    val plant = PlantDTO(
        id = null,
        externalId = "662e0f8d4202acfc",
        name = "Rhaphiolepis bibas",
        probability = 0.70,
        images = listOf(ImageDTO(
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
        taxonomy = TaxonomyDTO(
            taxonomyClass = "Magnoliopsida",
            genus = "Rhaphiolepis",
            order = "Rosales",
            family = "Rosaceae",
            phylum = "Tracheophyta",
            kingdom = "Plantae"
        ),
        moreInfoUrl = "https://es.wikipedia.org/wiki/Eriobotrya_japonica",
        description = "Eriobotrya japonica, comúnmente llamado níspero japonés,\u200B nisperero del Japón\u200B o simplemente níspero, es un árbol frutal perenne de la familia Rosaceae,\u200B originario del sudeste de China,\u200B donde se conoce como pípá, 枇杷.\u200B Fue introducido en Japón, donde se naturalizó y donde lleva cultivándose más de mil años. También se naturalizó en la India, la cuenca mediterránea, Canarias, Pakistán, Chile, Argentina , Ecuador,Costa Rica y muchas otras áreas. Se cree que la inmigración china llevó el níspero a Hawái.\nSe menciona a menudo en la antigua literatura china, por ejemplo en los poemas de Li Bai, y en la literatura portuguesa se conoce desde la era de los descubrimientos.\nEn noviembre se celebra el Festival del Níspero en San Juan del Obispo, Guatemala.\nEl fruto de esta especie ha ido sustituyendo al del níspero europeo (Mespilus germanica), de forma que, en la actualidad, al hablar de «níspero» se sobreentiende que se está haciendo referencia al japonés.",
        watering = WateringDTO(
            min = 2,
            max = 2
        ),
        bestWatering = "Watering this plant properly is crucial for its health. It prefers a moderate amount of water, so it's best to keep the soil consistently moist but not waterlogged. Overwatering can lead to root rot, while underwatering can cause the leaves to wilt and drop. During the growing season, water the plant more frequently, but reduce the amount in the winter when the plant's growth slows down. Always check the top inch of soil; if it feels dry, it's time to water.",
        propagationMethods = listOf("cuttings", "seeds"),
        culturalSignificance = "In various cultures, this plant is appreciated for its ornamental value. It is often used in landscaping for its attractive flowers and evergreen foliage. In some regions, it is also associated with good luck and prosperity. The plant's resilience and beauty make it a popular choice for public gardens and private yards alike.",
        bestLightCondition = "This plant thrives in full sun to partial shade. It needs at least six hours of direct sunlight each day to grow well and produce flowers. In hotter climates, some afternoon shade can help protect it from the intense midday sun. If grown indoors, place it near a south or west-facing window where it can receive plenty of light. Insufficient light can lead to poor growth and fewer blooms.",
        commonUses = "Common uses for this plant include ornamental landscaping and hedging. Its dense growth habit makes it an excellent choice for creating privacy screens or windbreaks. The plant's flowers attract pollinators like bees and butterflies, making it beneficial for the local ecosystem. Additionally, it can be grown in containers, making it versatile for use in patios and small garden spaces.",
        toxicity = "This plant is generally considered non-toxic to both humans and animals. There are no known harmful effects if ingested, making it a safe choice for gardens and homes with pets and children. However, it's always a good idea to discourage pets and kids from chewing on any plant material to avoid any potential digestive upset."
    )
    PlantResultCard(
        plant = plant,
        isSelected = false,
        onClick = {}
    )
}


@Preview (
    name = "Plant Result Card - Low Probability")
@Composable
fun PlantResultCardLowProbabilityPreview() {
    val plant = PlantDTO(
        id = null,
        externalId = "662e0f8d4202acfc",
        name = "Rhaphiolepis bibas",
        probability = 0.25,
        images = listOf(ImageDTO(
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
        taxonomy = TaxonomyDTO(
            taxonomyClass = "Magnoliopsida",
            genus = "Rhaphiolepis",
            order = "Rosales",
            family = "Rosaceae",
            phylum = "Tracheophyta",
            kingdom = "Plantae"
        ),
        moreInfoUrl = "https://es.wikipedia.org/wiki/Eriobotrya_japonica",
        description = "Eriobotrya japonica, comúnmente llamado níspero japonés,\u200B nisperero del Japón\u200B o simplemente níspero, es un árbol frutal perenne de la familia Rosaceae,\u200B originario del sudeste de China,\u200B donde se conoce como pípá, 枇杷.\u200B Fue introducido en Japón, donde se naturalizó y donde lleva cultivándose más de mil años. También se naturalizó en la India, la cuenca mediterránea, Canarias, Pakistán, Chile, Argentina , Ecuador,Costa Rica y muchas otras áreas. Se cree que la inmigración china llevó el níspero a Hawái.\nSe menciona a menudo en la antigua literatura china, por ejemplo en los poemas de Li Bai, y en la literatura portuguesa se conoce desde la era de los descubrimientos.\nEn noviembre se celebra el Festival del Níspero en San Juan del Obispo, Guatemala.\nEl fruto de esta especie ha ido sustituyendo al del níspero europeo (Mespilus germanica), de forma que, en la actualidad, al hablar de «níspero» se sobreentiende que se está haciendo referencia al japonés.",
        watering = WateringDTO(
            min = 2,
            max = 2
        ),
        bestWatering = "Watering this plant properly is crucial for its health. It prefers a moderate amount of water, so it's best to keep the soil consistently moist but not waterlogged. Overwatering can lead to root rot, while underwatering can cause the leaves to wilt and drop. During the growing season, water the plant more frequently, but reduce the amount in the winter when the plant's growth slows down. Always check the top inch of soil; if it feels dry, it's time to water.",
        propagationMethods = listOf("cuttings", "seeds"),
        culturalSignificance = "In various cultures, this plant is appreciated for its ornamental value. It is often used in landscaping for its attractive flowers and evergreen foliage. In some regions, it is also associated with good luck and prosperity. The plant's resilience and beauty make it a popular choice for public gardens and private yards alike.",
        bestLightCondition = "This plant thrives in full sun to partial shade. It needs at least six hours of direct sunlight each day to grow well and produce flowers. In hotter climates, some afternoon shade can help protect it from the intense midday sun. If grown indoors, place it near a south or west-facing window where it can receive plenty of light. Insufficient light can lead to poor growth and fewer blooms.",
        commonUses = "Common uses for this plant include ornamental landscaping and hedging. Its dense growth habit makes it an excellent choice for creating privacy screens or windbreaks. The plant's flowers attract pollinators like bees and butterflies, making it beneficial for the local ecosystem. Additionally, it can be grown in containers, making it versatile for use in patios and small garden spaces.",
        toxicity = "This plant is generally considered non-toxic to both humans and animals. There are no known harmful effects if ingested, making it a safe choice for gardens and homes with pets and children. However, it's always a good idea to discourage pets and kids from chewing on any plant material to avoid any potential digestive upset."
    )
    PlantResultCard(
        plant = plant,
        isSelected = false,
        onClick = {}
    )
}


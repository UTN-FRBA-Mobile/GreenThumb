package com.utn.greenthumb.ui.main.result

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.utn.greenthumb.domain.model.Plant
import com.utn.greenthumb.domain.model.SimilarImage
import com.utn.greenthumb.domain.model.Taxonomy
import com.utn.greenthumb.domain.model.Watering

@Composable
fun PlantResultCard(plant: Plant) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Nombre de la Planta
            Text(
                text = plant.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Nombres comunes
            if (plant.commonNames.isNotEmpty()) {
                Text(
                    text = "Nombres comunes:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = plant.commonNames.joinToString(),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Imágenes similares
            if (plant.similarImages.isNotEmpty()) {
                Text(
                    text = "Imágenes similares:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow {
                    items(plant.similarImages) { image ->
                        Image(
                            painter = rememberAsyncImagePainter(image.url),
                            contentDescription = "Imagen similar",
                            modifier = Modifier
                                .size(80.dp)
                                .padding(end = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Probabilidad
            Text(
                text = "Probabilidad:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${(plant.probability * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Sinónimos
            if (plant.synonyms.isNotEmpty()) {
                Text(
                    text = "Sinónimos:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = plant.synonyms.joinToString(),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Descripción
            if (plant.description.isNotEmpty()) {
                Text(
                    text = "Descripción:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = plant.description,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Taxonomía
            Text(
                text = "Taxonomía:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Clase: ${plant.taxonomy.taxonomyClass}\n" +
                        "Género: ${plant.taxonomy.genus}\n" +
                        "Orden: ${plant.taxonomy.order}\n" +
                        "Familia: ${plant.taxonomy.family}\n" +
                        "Filo: ${plant.taxonomy.phylum}\n" +
                        "Reino: ${plant.taxonomy.kingdom}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            // URL
            Text(
                text = "Más información:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = plant.url,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}


@Preview (showBackground = true)
@Composable
fun ResultCardPreview() {
    val plant = Plant(
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
    )
    PlantResultCard(plant = plant)
}

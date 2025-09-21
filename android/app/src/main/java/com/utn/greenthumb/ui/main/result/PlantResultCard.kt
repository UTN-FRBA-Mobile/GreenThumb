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
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.utn.greenthumb.domain.model.Plant

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
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Probabilidad
            Text(
                text = "Probabilidad: ${(plant.probability * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Nombres comunes
            if (plant.commonNames.isNotEmpty()) {
                Text(
                    text = "Nombres comunes: ${plant.commonNames.joinToString()}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Sinónimos
            if (plant.synonyms.isNotEmpty()) {
                Text(
                    text = "Sinónimos: ${plant.synonyms.joinToString()}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Descripción
            if (plant.description.isNotEmpty()) {
                Text(
                    text = "Descripción: ${plant.description}",
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Imágenes similares
            if (plant.similarImages.isNotEmpty()) {
                Text(
                    text = "Imágenes similares:",
                    style = MaterialTheme.typography.bodySmall,
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

            // Taxonomía
            Text(
                text = "Taxonomía: ${plant.taxonomy.kingdom}, ${plant.taxonomy.phylum}, " +
                        "${plant.taxonomy.taxonomyClass}, ${plant.taxonomy.order}, " +
                        "${plant.taxonomy.family}, ${plant.taxonomy.genus}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Rank
            Text(
                text = "Rank: ${plant.rank}",
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            // URL de referencia
            Text(
                text = "Más info: ${plant.url}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

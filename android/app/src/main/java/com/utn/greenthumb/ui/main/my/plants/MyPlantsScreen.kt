package com.utn.greenthumb.ui.main.my.plants

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.utn.greenthumb.R
import com.utn.greenthumb.domain.model.PlantDTO
import com.utn.greenthumb.ui.main.BaseScreen
import com.utn.greenthumb.ui.theme.GreenBackground

import com.utn.greenthumb.viewmodel.MyPlantsViewModel

@Composable
fun MyPlantsScreen(
    onHome: () -> Unit,
    onMyPlants: () -> Unit,
    onCamera: () -> Unit,
    onRemembers: () -> Unit,
    onProfile: () -> Unit,
    onPlantSelected: (PlantDTO) -> Unit,
    viewModel: MyPlantsViewModel = hiltViewModel()
) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val clientId = currentUser?.uid

    val plants by viewModel.plants.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(clientId) {
        clientId?.let {
            viewModel.fetchMyPlants(it)
        }
    }

    BaseScreen(
        onHome = onHome,
        onMyPlants = onMyPlants,
        onCamera = onCamera,
        onRemembers = onRemembers,
        onProfile = onProfile
    ) {
        val plants by viewModel.plants.collectAsState()
        MyPlantsScreenContent(
            plants = plants,
            isLoading = isLoading,
            error = error,
            onPlantSelected = onPlantSelected,
            onRetry = { clientId?.let { viewModel.fetchMyPlants(it) } }
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyPlantsScreenContent(
    plants: List<PlantDTO>,
    isLoading: Boolean,
    error: String?,
    onPlantSelected: (PlantDTO) -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(title = {
            Text("GreenThumb 🌿")
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = GreenBackground
            )
        )
    }
    ) { padding ->
        // Título de la sección
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
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
                        plants = plants,
                        onPlantSelected = onPlantSelected
                    )
                }
            }
        }
    }
}


@Composable
private fun MyPlantsListContent(
    plants: List<PlantDTO>,
    onPlantSelected: (PlantDTO) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Título de la sección
        Text(
            text = "Mis plantas",
            style = MaterialTheme.typography.headlineMedium,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        // Contador de plantas
        Text(
            text = getPlantsCountText(plants.size),
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        )

        // Lista Desplazable (LazyColumn)
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            itemsIndexed(
                plants,
                key = { index, plant ->
                    "${plant.id ?: plant.externalId}_$index"
                }
            ) { _, planta ->
                PlantItem(
                    planta = planta,
                    onClick = { onPlantSelected(planta) }
                )
            }
        }
    }
}

@Composable
private fun PlantItem(
    planta: PlantDTO,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
             val imageUrl = planta.images?.firstOrNull()?.url

            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.greenthumb),
                error = painterResource(id = R.drawable.greenthumb),
                contentDescription = planta.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Nombre de la planta
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = planta.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Mostrar nombre común si existe
                if (planta.commonNames.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = planta.commonNames.joinToString(", "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Ver detalles",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp)
            )
        }
    }
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
                text = "Cargando plantas...",
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
                text = "Error al cargar plantas",
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
                Text("Reintentar")
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
                text = "No tienes plantas guardadas",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Toma una foto de una planta para comenzar",
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
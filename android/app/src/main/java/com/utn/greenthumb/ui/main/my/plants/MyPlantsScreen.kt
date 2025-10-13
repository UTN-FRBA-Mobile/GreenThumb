package com.utn.greenthumb.ui.main.my.plants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.utn.greenthumb.domain.model.Plant
import com.utn.greenthumb.ui.main.BaseScreen

import com.utn.greenthumb.viewmodel.MyPlantsViewModel

@Composable
fun MyPlantsScreen(
    onHome: () -> Unit,
    onMyPlants: () -> Unit,
    onCamera: () -> Unit,
    onRemembers: () -> Unit,
    onProfile: () -> Unit,
    viewModel: MyPlantsViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.fetchMyPlants("user-123")
    }

    BaseScreen(
        onHome = onHome,
        onMyPlants = onMyPlants,
        onCamera = onCamera,
        onRemembers = onRemembers,
        onProfile = onProfile
    ) {
        val plants by viewModel.plants.collectAsState()
        MyPlantsScreenContent(plants)
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPlantsScreenContent(
    plants: List<Plant>
) {
    Scaffold(topBar = {
        TopAppBar(title = { Text("GreenThumb 🌿") })
    }
    ) { padding ->
        // Título de la sección
        Column(
            modifier = Modifier
                .fillMaxSize()
                // APLICA el padding del Scaffold aquí para evitar superposición
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp) // Padding adicional opcional
        ) {
            // Título de la sección
            Text(
                text = "Mis plantas",
                style = MaterialTheme.typography.headlineMedium,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Contador de elementos
            Text(
                text = "Total ${plants.size} elementos",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                // Agrega el modifier para alinear a la derecha como en tu imagen original
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )

            // Lista Desplazable (LazyColumn)
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(plants) { planta ->
                    PlantItem(planta = planta)
                }
            }
        }
    }
}


@Composable
fun PlantItem(planta: Plant) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder de Imagen (El bloque gris)
            Box(
                modifier = Modifier
                    .size(64.dp, 64.dp)
                    .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Nombre de la planta
            Text(
                text = planta.name,
                style = MaterialTheme.typography.titleMedium // O titleLarge
            )
        }
    }
}

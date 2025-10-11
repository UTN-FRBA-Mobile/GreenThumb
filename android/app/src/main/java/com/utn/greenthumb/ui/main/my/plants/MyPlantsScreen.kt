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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.utn.greenthumb.domain.model.User
import com.utn.greenthumb.ui.main.BaseScreen
import com.utn.greenthumb.ui.theme.GreenBackground

import com.utn.greenthumb.viewmodel.AuthViewModel

//val myPlants = mutableListOf("Red", "Green", "Blue")
data class Planta(
    val id: Int,
    val nombre: String,
    // Aquí iría el recurso o URL de la imagen (ej: val imagenResId: Int)
    val colorPlaceholder: Color // Para el ejemplo de la imagen gris
)
// Datos de ejemplo para la lista
val samplePlants =
    mutableListOf(
        Planta(1, "Planta #1", Color.LightGray),
        Planta(2, "Planta #2", Color.LightGray),
        Planta(3, "Planta #3", Color.LightGray),
        Planta(4, "Planta #41", Color.LightGray),
        // Puedes agregar muchas más, LazyColumn lo maneja eficientemente
    )

@Composable
fun MyPlantsScreen(
    onHome: () -> Unit,
    onMyPlants: () -> Unit,
    onCamera: () -> Unit,
    onRemembers: () -> Unit,
    onProfile: () -> Unit
) {
    BaseScreen(
        onHome = onHome,
        onMyPlants = onMyPlants,
        onCamera = onCamera,
        onRemembers = onRemembers,
        onProfile = onProfile
    ) {
        MyPlantsScreenContent(
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPlantsScreenContent(

) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("GreenThumb 🌿") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenBackground)
        )
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
                text = "Total ${samplePlants.size} elementos",
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
                items(samplePlants) { planta ->
                    PlantItem(planta = planta)
                }
            }
        }
    }
}


@Composable
fun PlantItem(planta: Planta) {
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
                    .background(planta.colorPlaceholder, shape = RoundedCornerShape(4.dp))
                    .clip(RoundedCornerShape(4.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Nombre de la planta
            Text(
                text = planta.nombre,
                style = MaterialTheme.typography.titleMedium // O titleLarge
            )
        }
    }
}



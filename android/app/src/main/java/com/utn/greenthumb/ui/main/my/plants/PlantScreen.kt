package com.utn.greenthumb.ui.main.my.plants

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.utn.greenthumb.R
import com.utn.greenthumb.domain.model.PlantDTO
import com.utn.greenthumb.ui.main.result.FullScreenImageGallery
import com.utn.greenthumb.ui.theme.GreenBackground
import com.utn.greenthumb.viewmodel.PlantViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantScreen(
    onBackPressed: () -> Unit,
    plantSelected: PlantDTO
) {

    var selectedGalleryImages by remember { mutableStateOf<List<String>?>(null) }
    var selectedGalleryIndex by remember { mutableIntStateOf(0) }

    BackHandler(onBack = onBackPressed)

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenBackground),
                title = { Text(plantSelected.name) },
                navigationIcon = {
                    IconButton(
                        onClick = onBackPressed
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
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            PlantCard(
                plant = plantSelected,
                onImageClick = { imageIndex, images ->
                    selectedGalleryImages = images
                    selectedGalleryIndex = imageIndex
                }
            )
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
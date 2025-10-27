package com.utn.greenthumb.ui.main.remember

import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.utn.greenthumb.R
import com.utn.greenthumb.domain.model.PlantCatalogDTO
import com.utn.greenthumb.domain.model.watering.WateringType
import com.utn.greenthumb.ui.main.BaseScreen
import com.utn.greenthumb.ui.theme.GreenBackground
import com.utn.greenthumb.ui.theme.PurpleCard
import com.utn.greenthumb.viewmodel.WateringConfigViewModel
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RememberScreen(
    onHome: () -> Unit,
    onMyPlants: () -> Unit,
    onCamera: () -> Unit,
    onRemembers: () -> Unit,
    onProfile: () -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    wateringConfigViewModel: WateringConfigViewModel,
) {
    val configurations = wateringConfigViewModel.configurations.collectAsState()
    val modalState = wateringConfigViewModel.modalState.collectAsState()

    LaunchedEffect(Unit) {
        wateringConfigViewModel.getConfigs()
    }

    BaseScreen(
        onHome = onHome,
        onMyPlants = onMyPlants,
        onCamera = onCamera,
        onRemembers = onRemembers,
        onProfile = onProfile
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenBackground),
                    title = {
                        Text(text = stringResource(R.string.remembers))
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back_navigation)
                            )
                        }
                    })
            },
            modifier = modifier
        ) { padding ->
            if (configurations.value.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                    }
                }
            } else {
                Box(
                    modifier = Modifier.padding(padding)
                ) {
                    CreateWateringBottomSheet(
                        showSheet = modalState.value.visible,
                        plants = modalState.value.plantNames,
                        onDismiss = { wateringConfigViewModel.closeModal() },
                        onConfirm = { s1: String, s2: String -> wateringConfigViewModel.closeModal() }
                    )

                    if (configurations.value.rememberConfigurations.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text("No hay configuraciones")
                            Spacer(modifier = Modifier.padding(16.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .padding(10.dp),
                                onClick = wateringConfigViewModel::openModal
                            ) {
                                Text("Crear nueva configuración")
                            }
                        }
                    } else {

                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateWateringBottomSheet(
    showSheet: Boolean,
    plants: List<PlantCatalogDTO>,
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit,
) {
    if (showSheet) {
        val selectedPlant by remember { mutableStateOf(null) }
        val tabs = WateringType.entries.toTypedArray()
        var selectedTabIndex by remember { mutableIntStateOf(0) }

        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        LaunchedEffect(Unit) {
            sheetState.expand()
        }

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
        ) {
            var plantName by remember { mutableStateOf("") }
            var time by remember { mutableStateOf("") }
            var numberInput by remember { mutableStateOf("") }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text("Nuevo Recordatorio", style = MaterialTheme.typography.titleLarge)
                Spacer(Modifier.height(4.dp))

                WateringTypeDropdown(
                    selectedPlant = selectedPlant,
                    onTypeSelected = {},
                    plants = plants,
                )
                GreenThumbTimePicker()

                SecondaryTabRow(
                    containerColor = PurpleCard,
                    selectedTabIndex = selectedTabIndex
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title.name.lowercase()) }
                        )
                    }
                }

                when (selectedTabIndex) {
                    0 -> WeekdaySelector(
                        selectedDays = mutableListOf(),
                        onSelectionChange = {

                        }
                    )

                    1 -> TextField(
                        value = numberInput,
                        onValueChange = { newValue ->
                            // Filter out non-numeric characters
                            numberInput = newValue.filter { it.isDigit() }
                        },
                        label = { Text("Enter a number") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                }

                Spacer(Modifier.height(16.dp))
                Button(
                    modifier = Modifier.align(Alignment.End),
                    onClick = { onConfirm(plantName, time) }
                ) {
                    Text("Guardar")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WateringTypeDropdown(
    selectedPlant: PlantCatalogDTO?,
    plants: List<PlantCatalogDTO>,
    onTypeSelected: (PlantCatalogDTO) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedPlant?.name ?: "",
            onValueChange = {},
            label = { Text("Elige tu planta") },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            plants.forEach { plant ->
                DropdownMenuItem(
                    text = { Text(plant.name) },
                    onClick = {
                        onTypeSelected(plant)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreenThumbTimePicker(
    label: String = "Hora de recordatorio",
    initialHour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
    initialMinute: Int = Calendar.getInstance().get(Calendar.MINUTE),
    is24Hour: Boolean = true
) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedTime by remember {
        mutableStateOf(String.format("%02d:%02d", initialHour, initialMinute))
    }

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = is24Hour
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(4.dp)
                )
                .clickable { showDialog = true }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = selectedTime)
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedTime = "%02d:%02d".format(timePickerState.hour, timePickerState.minute)
                    showDialog = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekdaySelector(
    selectedDays: List<String>,
    onSelectionChange: (List<String>) -> Unit
) {
    val days = listOf("L", "M", "X", "J", "V", "S", "D")

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        days.forEach { day ->
            val isSelected = selectedDays.contains(day)
            FilterChip(
                selected = isSelected,
                onClick = {
                    val newSelection = if (isSelected) {
                        selectedDays - day
                    } else {
                        selectedDays + day
                    }
                    onSelectionChange(newSelection)
                },
                label = { Text(day) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    }
}



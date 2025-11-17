package com.utn.greenthumb.ui.main.remember

import android.content.Context
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.utn.greenthumb.R
import com.utn.greenthumb.domain.model.PlantCatalogDTO
import com.utn.greenthumb.domain.model.watering.DayOfWeek
import com.utn.greenthumb.domain.model.watering.WateringConfigurationDTO
import com.utn.greenthumb.domain.model.watering.WateringDatesDTO
import com.utn.greenthumb.domain.model.watering.WateringScheduleDTO
import com.utn.greenthumb.domain.model.watering.WateringType
import com.utn.greenthumb.ui.main.BaseScreen
import com.utn.greenthumb.ui.theme.Purple80
import com.utn.greenthumb.viewmodel.RememberModalForm
import com.utn.greenthumb.viewmodel.WateringConfigViewModel
import java.util.Calendar
import java.util.Calendar.HOUR_OF_DAY
import java.util.Calendar.MINUTE

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
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    title = {
                        Text(text = stringResource(R.string.home_remembers_title))
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back_navigation)
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = wateringConfigViewModel::openModal) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Agregar",
                                modifier = Modifier.size(42.dp)
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
                        plants = modalState.value.plantNames ?: listOf(),
                        onDismiss = { wateringConfigViewModel.closeModal() },
                        onConfirm = { form ->
                            if (modalState.value.editFlow) {
                                wateringConfigViewModel.update(form)
                            } else {
                                wateringConfigViewModel.create(form)
                            }

                            wateringConfigViewModel.closeModal()
                        },
                        initialState = modalState.value.selectedConfig
                    )

                    if (configurations.value.rememberConfigurations.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(stringResource(R.string.no_configuration))
                            Spacer(modifier = Modifier.padding(16.dp))
                            Button(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .padding(10.dp),
                                onClick = wateringConfigViewModel::openModal
                            ) {
                                Text(stringResource(R.string.create_configuration))
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(20.dp),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            itemsIndexed(
                                items = configurations.value.rememberConfigurations,
                                key = { _, reminder -> reminder.id ?: "" }
                            ) { _, reminder ->
                                ConfigurationCard(
                                    reminder,
                                    viewModel = wateringConfigViewModel
                                )
                            }

                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConfigurationCard(
    reminder: WateringConfigurationDTO,
    viewModel: WateringConfigViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = {
                                viewModel.openModalForEdit(reminder)
                            }
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = reminder.plantName ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                }

                FilledIconButton(
                    onClick = {
                        viewModel.delete(reminder)
                    },
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = Color(0xFFD32F2F),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_plant),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            if (reminder.details is WateringScheduleDTO) {
                Row {
                    DayOfWeek.entries.map {
                        DayChip(
                            isSelected = reminder.details.daysOfWeek.contains(
                                it
                            ),
                            day = it,
                            onClick = {}
                        )
                    }
                }
            } else if (reminder.details is WateringDatesDTO) {
                Text(stringResource(R.string.every_x_days, reminder.details.datesInterval))
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
    onConfirm: (RememberModalForm) -> Unit,
    initialState: RememberModalForm
) {
    if (showSheet) {
        var form by remember { mutableStateOf(initialState) }

        val tabs = WateringType.entries.toTypedArray()
        val selectedTabIndex = remember { mutableIntStateOf(tabs.indexOf(initialState.type)) }

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

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Text(
                    text = stringResource(R.string.new_remember),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(Modifier.height(4.dp))

                WateringPlantDropdown(
                    selectedPlant = form.selectedPlant,
                    onPlantSelected = { it ->
                        form = form.copy(selectedPlant = it)
                    },
                    plants = plants,
                )

                val initialHour = if (form.time.isEmpty()) Calendar.getInstance().get(HOUR_OF_DAY) else form.time.split(":")[0].toInt()
                val initialMinute = if (form.time.isEmpty()) Calendar.getInstance().get(MINUTE) else  form.time.split(":")[1].toInt()
                GreenThumbTimePicker(
                    label = stringResource(R.string.remember_time),
                    initialHour = initialHour,
                    initialMinute = initialMinute,
                    onChange = {
                        form = form.copy(time = it)
                    }
                )

                SecondaryTabRow(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    selectedTabIndex = selectedTabIndex.intValue
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex.intValue == index,
                            onClick = { selectedTabIndex.intValue = index },
                            text = { Text(title.getString(LocalContext.current)) }
                        )
                    }
                }

                when (selectedTabIndex.intValue) {
                    0 -> {
                        WeekdaySelector(
                            selectedDays = form.selectedDays,
                            onSelectionChange = {
                                form = form.copy(
                                    selectedDays = it,
                                    type = WateringType.SCHEDULES
                                )
                            }
                        )
                    }

                    1 -> TextField(
                        value = form.numberInput?.toString() ?: "",
                        onValueChange = { newValue ->
                            val numberInput = newValue.filter { it.isDigit() }
                            form =
                                form.copy(
                                    numberInput = if (numberInput.isEmpty()) null else numberInput.toInt(),
                                    type = WateringType.DATES_FREQUENCY
                                )
                        },
                        label = { Text(stringResource(R.string.enter_value)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                }

                Spacer(Modifier.height(16.dp))
                Button(
                    enabled = form.isValid(),
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                        onConfirm(form)
                    }
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WateringPlantDropdown(
    selectedPlant: PlantCatalogDTO?,
    plants: List<PlantCatalogDTO>,
    onPlantSelected: (PlantCatalogDTO) -> Unit
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
                        expanded = false
                        onPlantSelected(plant)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreenThumbTimePicker(
    label: String,
    initialHour: Int = Calendar.getInstance().get(HOUR_OF_DAY),
    initialMinute: Int = Calendar.getInstance().get(MINUTE),
    is24Hour: Boolean = true,
    onChange: (String) -> Unit,
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
                    onChange(selectedTime)
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
    selectedDays: List<DayOfWeek>,
    onSelectionChange: (List<DayOfWeek>) -> Unit
) {
    val days = DayOfWeek.entries.toTypedArray()

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(horizontal = 8.dp)
    ) {
        days.forEach { day ->
            val isSelected = selectedDays.contains(day)
            DayChip(
                isSelected = isSelected,
                day = day,
                onClick = {
                    val newSelection = if (isSelected) {
                        selectedDays - day
                    } else {
                        selectedDays + day
                    }
                    onSelectionChange(newSelection)
                })
        }
    }
}

@Composable
private fun DayChip(
    isSelected: Boolean,
    day: DayOfWeek,
    onClick: () -> Unit,
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(day.getString(LocalContext.current)) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Purple80,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

fun DayOfWeek.getString(context: Context): String {
    return when (this) {
        DayOfWeek.monday -> context.getString(R.string.monday)
        DayOfWeek.tuesday -> context.getString(R.string.tuesday)
        DayOfWeek.wednesday -> context.getString(R.string.wednesday)
        DayOfWeek.thursday -> context.getString(R.string.thursday)
        DayOfWeek.friday -> context.getString(R.string.friday)
        DayOfWeek.saturday -> context.getString(R.string.saturday)
        DayOfWeek.sunday -> context.getString(R.string.sunday)
    }
}

fun WateringType.getString(context: Context): String {
    return when (this) {
        WateringType.SCHEDULES -> context.getString(R.string.schedules)
        WateringType.DATES_FREQUENCY -> context.getString(R.string.dates_frequency)
    }
}


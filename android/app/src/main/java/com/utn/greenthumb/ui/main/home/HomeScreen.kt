package com.utn.greenthumb.ui.main.home

import com.utn.greenthumb.R
import com.utn.greenthumb.viewmodel.HomeViewModel
import com.utn.greenthumb.viewmodel.HomeViewModel.FavouritePlantsUIState
import com.utn.greenthumb.viewmodel.HomeViewModel.WateringScheduleUIState
import com.utn.greenthumb.viewmodel.HomeViewModel.FavouritePlant
import com.utn.greenthumb.viewmodel.HomeViewModel.WateringReminder
import com.utn.greenthumb.domain.model.UserMessage
import com.utn.greenthumb.ui.theme.GreenThumbTheme
import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.utn.greenthumb.domain.model.User
import com.utn.greenthumb.ui.main.BaseScreen
import com.utn.greenthumb.utils.NotificationHelper
import com.utn.greenthumb.viewmodel.AuthViewModel
import com.utn.greenthumb.viewmodel.NotificationViewModel
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import kotlin.math.abs
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.clickable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.compose.material.icons.filled.Clear // <-- Añade este import
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.text.style.TextAlign
import com.utn.greenthumb.domain.model.Severity
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.sp
import com.utn.greenthumb.domain.model.PlantDTO
import com.utn.greenthumb.ui.main.GreenThumbTopAppBar
import kotlin.String



@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    notificationViewModel: NotificationViewModel,
    viewModel: HomeViewModel = hiltViewModel(),
    currentUser: User?,
    onHome: () -> Unit,
    onMyPlants: () -> Unit,
    onCamera: () -> Unit,
    onRemembers: () -> Unit,
    onProfile: () -> Unit,
    onPlantSelected: (PlantDTO) -> Unit
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("Home", "User Granted")
            notificationViewModel.refreshToken()
        } else {
            Log.w("HomeScreen", "Notification permission denied")
        }
    }

    val uiHomeState by viewModel.uiHomeState.collectAsState()

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (NotificationHelper.hasNotificationPermission(context)) {
                    notificationViewModel.refreshToken()
                } else {
                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            } else {
                notificationViewModel.refreshToken()
            }

            if (currentUser.uid.isNotEmpty()) {
                viewModel.fetchData(currentUser.uid)
            }
        }
    }

    BaseScreen(
        onHome = onHome,
        onMyPlants = onMyPlants,
        onCamera = onCamera,
        onRemembers = onRemembers,
        onProfile = onProfile
    ) { paddingValues ->
        if (uiHomeState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "🌿 " + "Bienvenido, ${currentUser?.displayName ?: authViewModel.getUserName()}",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Estamos cargando tus datos ...",
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }
        } else {
            HomeScreenContent(
                userName = currentUser?.displayName ?: authViewModel.getUserName(),
                onPlantSelected = onPlantSelected,
                wateringScheduleUIState = uiHomeState.wateringScheduleUIState,
                favouritePlantsUIState = uiHomeState.favouritePlantsUIState
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    userName: String?,
    onPlantSelected: (PlantDTO) -> Unit,
    wateringScheduleUIState: WateringScheduleUIState,
    favouritePlantsUIState: FavouritePlantsUIState
) {
    val scrollState = rememberScrollState()

    Scaffold(topBar = {
        GreenThumbTopAppBar(
            title = "GreenThumb 🌿",
            onNavigateBack = {  },
            visible = false,
            enabled = false
        )
    }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(top = 2.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (wateringScheduleUIState.isLoading || favouritePlantsUIState.isLoading) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "🌿 " + "Bienvenido, ${userName ?: "Usuario no identificado"}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Estamos recolectando tus datos ...",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.Start
                ) {
                    //SearchBar(Modifier.padding(horizontal = 16.dp))

                    // Sección de Plantas Favoritas
                    HomeSection(
                        title = stringResource(R.string.favourite_plants_section_title)
                    ) {
                        FavouritePlantSection(
                            favouritePlantsUIState = favouritePlantsUIState,
                            onPlantSelected = onPlantSelected
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Sección de Recordatorios de Riego
//                    HomeSection(
//                        title = stringResource(R.string.watering_schedule_section_title)
//                    ) {
//                        WateringScheduleSection(
//                            wateringScheduleUIState = wateringScheduleUIState,
//                        )
//                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

    }
}


@Composable
fun HomeSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .paddingFromBaseline(top = 40.dp, bottom = 16.dp)
        )
        content()
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { newText -> text = newText },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },

        trailingIcon = {
            // Muestra el icono de la cruz SÓLO si el texto no está vacío.
            if (text.isNotEmpty()) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Clear text",
                    modifier = Modifier.clickable { text = "" }
                )
            }
        },

        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        placeholder = {
            Text(stringResource(R.string.placeholder_search_favourite))
        },
        // una sola línea para la barra de búsqueda.
        singleLine = true,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    )
}


@Composable
fun FavouritePlantItem(
    favouritePlant: FavouritePlant,
    onSelectFavouritePlant: (PlantDTO) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(favouritePlant.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(favouritePlant.imagePlaceholder),
            error = painterResource(id = favouritePlant.imagePlaceholder),
            contentDescription = favouritePlant.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .border(4.dp, Color(0xFF2A542C), CircleShape)
                .padding(all = 4.dp)
                .clickable(onClick = {
                    Log.d("HomeScreen","Favourite plant clicked: ${favouritePlant.id} | ${favouritePlant.name}")
                    onSelectFavouritePlant(favouritePlant.plant)
                })
        )

        val name = if (favouritePlant.name.length > 10) {
            favouritePlant.name.substring(0, 10) + "..."
        } else {
            favouritePlant.name
        }

        Text(
            text = name,
            modifier = Modifier.paddingFromBaseline(top = 24.dp, bottom = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}


@Composable
fun FavouritePlantSection(
    favouritePlantsUIState: FavouritePlantsUIState,
    onPlantSelected: (PlantDTO) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!favouritePlantsUIState.isValid) {
        // Estado invalido de los datos
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(horizontal = 16.dp)
                .background(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                favouritePlantsUIState.userMessages.forEach { userMessage ->
                    Text(
                        text = "🍁 " + userMessage.message,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    } else {
        if (favouritePlantsUIState.favourites.isNotEmpty()) {
            // Caso de exito
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = modifier
            ) {
                itemsIndexed(
                    favouritePlantsUIState.favourites,
                    key = { _, plant -> plant.id }
                ) { _, plant ->
                    FavouritePlantItem(
                        favouritePlant = plant,
                        onSelectFavouritePlant = onPlantSelected
                        //onSelectFavouritePlant = favouritePlantsUIState.onSelectFavouritePlant
                    )
                }
            }
        } else {
            // No hay datos para mostrar
            Box(
                modifier = modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .padding(horizontal = 16.dp)
                    .background(
                        //color = colorResource(id = R.color.home_empty_data_background),
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🌿 " + stringResource(R.string.empty_favourite_plants_message),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WateringReminderCard(
    reminder: WateringReminder,
    onCheckWateringReminder: (WateringReminder) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }

    val cardColor = if (reminder.overdue)
        MaterialTheme.colorScheme.errorContainer
    else
        MaterialTheme.colorScheme.primaryFixed

    Surface(
        shape = MaterialTheme.shapes.medium,
        color = cardColor,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .clickable { isExpanded = !isExpanded }
    ) {
        // Usamos ConstraintLayout para un control preciso de la alineación
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp) // Un único padding interno para el layout
        ) {
            // Crea las referencias (IDs) para cada elemento del layout
            val (plantImage, textInfo, checkIcon, expandableContent) = createRefs()

            // Imagen de planta
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(reminder.plantImageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(reminder.plantImagePlaceholder),
                error = painterResource(id = reminder.plantImagePlaceholder),
                contentDescription = "", //reminder.plantName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                    // Anclas del ConstraintLayout
                    .constrainAs(plantImage) {
                        top.linkTo(parent.top) // Alineado arriba
                        start.linkTo(parent.start) // Alineado a la izquierda
                    }
            )

            // Columna de texto
            Column(
                modifier = Modifier
                    .constrainAs(textInfo) {
                        // Anclado arriba, a la derecha de la imagen y a la izquierda del check
                        top.linkTo(parent.top)
                        start.linkTo(plantImage.end, margin = 12.dp)
                        end.linkTo(checkIcon.start, margin = 12.dp)
                        // Esto hace que ocupe el ancho disponible entre los dos iconos
                        width = Dimension.fillToConstraints
                    }
            ) {
                Text(
                    text = reminder.plantName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = SimpleDateFormat("EEEE, d 'de' MMMM").format(reminder.date),
                    //text = SimpleDateFormat("EEEE, d 'de' MMMM").format(reminder.date),
                    style = MaterialTheme.typography.titleSmall,
                )
            }

            // Icono de check
            val checkImage = if (reminder.onCheck) {
                R.drawable.clock
            } else {
                R.drawable.check_green
            }

            Image(
                painter = painterResource(checkImage),
                contentDescription = "Check",
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onCheckWateringReminder(reminder) }
                    .constrainAs(checkIcon) {
                        top.linkTo(parent.top) // Alineado arriba
                        end.linkTo(parent.end) // Alineado a la derecha
                    }
            )

            // Contenido expandible
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn(animationSpec = tween(durationMillis = 150)) + expandVertically(animationSpec = tween(durationMillis = 300)),
                exit = fadeOut(animationSpec = tween(durationMillis = 150)) + shrinkVertically(animationSpec = tween(durationMillis = 300)),
                modifier = Modifier
                    .constrainAs(expandableContent) {
                        // Anclado DEBAJO de la columna de texto principal
                        top.linkTo(textInfo.bottom, margin = 8.dp)
                        // Ancla el inicio al inicio de la columna de texto
                        start.linkTo(textInfo.start)
                        // Ancla el final al final del icono de check
                        end.linkTo(checkIcon.end)
                        // Haz que el ancho llene el espacio definido por las anclas
                        width = Dimension.fillToConstraints
                    }
            ) {
                Column {
                    val days =
                        abs(reminder.daysLeft).toString() + " " +
                            if (abs(reminder.daysLeft) > 1) {
                                stringResource(R.string.remaining_days)
                            } else {
                                stringResource(R.string.remaining_day)
                            }

                    val notice =
                        if (reminder.daysLeft == 0) {
                            "Hoy es el dia de regar tu planta"
                        }
                        else if (reminder.daysLeft < 0) {
                            "Tu riego esta retrasado por $days"
                        }
                        else {
                            "$days para el próximo riego"
                        }

                    Text(
                        text = notice,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xEE1E1D1D),
                    )
                    Text(
                        text = stringResource(R.string.watering_suggestion),
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xEE1E1D1D),
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}



@Composable
fun WateringScheduleSection (
    wateringScheduleUIState: WateringScheduleUIState,
    modifier: Modifier = Modifier
) {
    // Estado invalido de los datos
    if (!wateringScheduleUIState.isValid) {
        Box (
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .heightIn(min = 150.dp)
                .background(
                    //color = colorResource(id = R.color.home_error_data_background),
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🍂",
                    style = MaterialTheme.typography.displayLarge,
                    fontSize = 72.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
                wateringScheduleUIState.userMessages.forEach { userMessage ->
                    Text(
                        text = "🍁 " + userMessage.message,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    } else {
        // Estado exitoso
        if (wateringScheduleUIState.schedule.isNotEmpty()) {
            /*LazyColumn(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = modifier
            ) {
                itemsIndexed(
                    items = wateringScheduleUIState.schedule,
                    key = { _, reminder -> reminder.id }
                ) { _, reminder ->
                    WateringReminderCard(
                        reminder,
                        onCheckWateringReminder = wateringScheduleUIState.onCheckWateringReminder
                    )
                }*/
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                wateringScheduleUIState.schedule.forEach { reminder ->
                    WateringReminderCard(
                        reminder = reminder,
                        onCheckWateringReminder = wateringScheduleUIState.onCheckWateringReminder
                    )
                }
            }
        } else {
            // No hay datos para mostrar
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape= RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            )  {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🪴",
                        style = MaterialTheme.typography.displayLarge,
                        fontSize = 72.sp
                    )
                    Text(
                        text = "🌿 " + stringResource(R.string.empty_watering_schedule_message),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}


private val favouritePlantsUIInvalidState = FavouritePlantsUIState(
    isLoading = false,
    isValid = false,
    userMessages = listOf(
        UserMessage(
            "No se han podido recuperar tus plantas favoritas. Por favor, refresca la pantalla",
            Severity.ERROR
        )
    ),
    favourites = listOf()
)

private val favouritePlantsUIEmptyState = FavouritePlantsUIState(
    isLoading = false,
    isValid = true,
    userMessages = listOf(),
    favourites = listOf()
)

private val favouritePlantsUILoadingState = FavouritePlantsUIState(
    isLoading = true,
    isValid = false,
    userMessages = listOf(),
    favourites = listOf()
)


private val plantDTO = PlantDTO(
    id = "",
    externalId = "",
    name = "",
    probability = 0.0,
    images = listOf(),
    commonNames = listOf(),
    taxonomy = null,
    moreInfoUrl = "",
    description = "",
    synonyms = null,
    watering = null,
    bestWatering = "",
    propagationMethods = listOf(),
    culturalSignificance = "",
    bestLightCondition = "",
    commonUses = "",
    toxicity = "",
    favourite = true
)

private val favouritePlantsUIState = FavouritePlantsUIState(
    isLoading = false,
    isValid = true,
    userMessages = listOf(),
    favourites = listOf(
        FavouritePlant(id = "1", name = "Acer palmatum Thunb",
            imageUrl = "https://www.massogarden.com/images/plantas/Acer_palmatum_Thunb.jpg",
            imagePlaceholder = R.drawable.anthurium_andraeanum,
            plant = plantDTO),
        FavouritePlant(id = "2", name = "Anturio",
            imageUrl = "https://www.massogarden.com/images/anturio.jpg",
            imagePlaceholder = R.drawable.anturio,
            plant = plantDTO),
        FavouritePlant(id = "3", name = "Acacia de Constantinopla",
            imageUrl = "https://www.massogarden.com/images/plantas/Acacia_de_Constantinopla.jpg",
            imagePlaceholder = R.drawable.clivia,
            plant = plantDTO),
        FavouritePlant(id = "4", name = "Impatiens",
            imageUrl = "https://www.massogarden.com/images/plantas/Impatiens.jpg",
            imagePlaceholder = R.drawable.kalanchoe,
            plant = plantDTO),
        FavouritePlant(id = "5", name = "Alegrías de la casa",
            imageUrl = "https://www.massogarden.com/images/plantas/Impatiens.jpg",
            imagePlaceholder = R.drawable.clerodendrum_thomsoniae,
            plant = plantDTO),
        FavouritePlant(id = "6", name = "Cactus",
            imageUrl = "https://www.massogarden.com/images/plantas/Cactus.jpg",
            imagePlaceholder = R.drawable.cephalotus_follicularis,
            plant = plantDTO),
        FavouritePlant(id = "7", name = "Cheflera",
            imageUrl = "https://www.massogarden.com/images/plantas/cheflera.jpg",
            imagePlaceholder = R.drawable.hoya_carnosa,
            plant = plantDTO),
    ),
)

private val wateringScheduleUIInvalidState = WateringScheduleUIState(
    isLoading = false,
    isValid = false,
    userMessages = listOf(
        UserMessage(
            "No se han podido recuperar tus horarios de riego. Por favor, refresca la pantalla",
            Severity.ERROR
        )
    ),
    schedule = listOf(),
    onCheckWateringReminder = {}
)

private val wateringScheduleUIEmptyState = WateringScheduleUIState(
    isLoading = false,
    isValid = true,
    userMessages = listOf(),
    schedule = listOf(),
    onCheckWateringReminder = {}
)

private val wateringScheduleUILoadingState = WateringScheduleUIState(
    isLoading = true,
    isValid = false,
    userMessages = listOf(),
    schedule = listOf(),
    onCheckWateringReminder = {}
)


private val wateringScheduleUIState = WateringScheduleUIState(
    isLoading = false,
    isValid = true,
    userMessages = listOf(),
    onCheckWateringReminder = {},
    schedule = listOf(
        WateringReminder(id = "2", plantId = "4", plantName = "Impatients",
            plantImageUrl = "https://www.massogarden.com/images/plantas/Impatiens.jpg",
            plantImagePlaceholder = R.drawable.kalanchoe,
            date = getDate(-2), daysLeft = -2, overdue = true, onCheck = false
        ),
        WateringReminder(id = "3", plantId = "2", plantName = "Anturio",
            plantImageUrl = "https://www.massogarden.com/images/anturio.jpg",
            plantImagePlaceholder = R.drawable.anturio,
            date = getDate(0), daysLeft = 0, overdue = false, onCheck = false
        ),
        WateringReminder(id = "1", plantId = "6", plantName = "Cactus",
            plantImageUrl = "https://www.massogarden.com/images/plantas/Cactus.jpg",
            plantImagePlaceholder = R.drawable.cephalotus_follicularis,
            date = getDate(4), daysLeft = 4, overdue = false, onCheck = false
        ),
        WateringReminder(id = "4", plantId = "7", plantName = "Cheflera",
            plantImageUrl = "https://www.massogarden.com/images/plantas/cheflera.jpg",
            plantImagePlaceholder = R.drawable.hoya_carnosa,
            date = getDate(6), daysLeft = 6, overdue = false, onCheck = false
        ),
    ),
)

private fun getDate(days: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, days)
    return calendar.time
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    GreenThumbTheme {
        SearchBar(Modifier.padding(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun FavouritePlantItemPreview() {
    GreenThumbTheme {
        FavouritePlantItem(
            favouritePlant = favouritePlantsUIState.favourites.first(),
            onSelectFavouritePlant = { },
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavouritePlantItemDarkPreview() {
    GreenThumbTheme (darkTheme = true) {
        FavouritePlantItem(
            favouritePlant = favouritePlantsUIState.favourites.first(),
            onSelectFavouritePlant = { },
            modifier = Modifier.padding(8.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun FavouritePlantSectionPreview() {
    GreenThumbTheme {
        FavouritePlantSection(
            favouritePlantsUIState = favouritePlantsUIState,
            onPlantSelected = { },
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavouritePlantSectionDarkPreview() {
    GreenThumbTheme(darkTheme = true) {
        FavouritePlantSection(
            favouritePlantsUIState = favouritePlantsUIState,
            onPlantSelected = { },
            modifier = Modifier.padding(8.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun WateringReminderCardOverduePreview() {
    GreenThumbTheme {
        val plant = wateringScheduleUIState.schedule.first { it.overdue }

        WateringReminderCard(
            reminder = plant,
            onCheckWateringReminder = { },
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WateringReminderCardOverdueDarkPreview() {
    GreenThumbTheme(darkTheme = true) {
        val plant = wateringScheduleUIState.schedule.first { it.overdue }

        WateringReminderCard(
            reminder = plant,
            onCheckWateringReminder = { },
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WateringReminderCardPreview() {
    GreenThumbTheme {
        val plant = wateringScheduleUIState.schedule.first { !it.overdue }

        WateringReminderCard(
            reminder = plant,
            onCheckWateringReminder = { },
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WateringReminderCardDarkPreview() {
    GreenThumbTheme(darkTheme = true) {
        val plant = wateringScheduleUIState.schedule.first { !it.overdue }

        WateringReminderCard(
            reminder = plant,
            onCheckWateringReminder = { },
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WateringScheduleSectionPreview() {
    GreenThumbTheme {
        WateringScheduleSection(
            wateringScheduleUIState = wateringScheduleUIState,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WateringScheduleSectionDarkPreview() {
    GreenThumbTheme(darkTheme = true) {
        WateringScheduleSection(
            wateringScheduleUIState = wateringScheduleUIState,
            modifier = Modifier.padding(8.dp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    GreenThumbTheme {
        BaseScreen(
            onHome = { },
            onMyPlants = { },
            onCamera = { },
            onRemembers = { },
            onProfile = { }
        ) {
            HomeScreenContent(
                userName = "Matias",
                onPlantSelected = { },
                wateringScheduleUIState = wateringScheduleUIState,
                favouritePlantsUIState = favouritePlantsUIState
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenDarkPreview() {
    GreenThumbTheme(darkTheme = true) {
        BaseScreen(
            onHome = { },
            onMyPlants = { },
            onCamera = { },
            onRemembers = { },
            onProfile = { }
        ) {
            HomeScreenContent(
                userName = "Matias",
                onPlantSelected = { },
                wateringScheduleUIState = wateringScheduleUIState,
                favouritePlantsUIState = favouritePlantsUIState
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenInvalidPreview() {
    GreenThumbTheme {
        BaseScreen(
            onHome = { },
            onMyPlants = { },
            onCamera = { },
            onRemembers = { },
            onProfile = { }
        ) {
            HomeScreenContent(
                userName = "Matias",
                onPlantSelected = { },
                wateringScheduleUIState = wateringScheduleUIInvalidState,
                favouritePlantsUIState = favouritePlantsUIInvalidState
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenInvalidDarkPreview() {
    GreenThumbTheme(darkTheme = true) {
        BaseScreen(
            onHome = { },
            onMyPlants = { },
            onCamera = { },
            onRemembers = { },
            onProfile = { }
        ) {
            HomeScreenContent(
                userName = "Matias",
                onPlantSelected = { },
                wateringScheduleUIState = wateringScheduleUIInvalidState,
                favouritePlantsUIState = favouritePlantsUIInvalidState
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenEmptyPreview() {
    GreenThumbTheme {
        BaseScreen(
            onHome = { },
            onMyPlants = { },
            onCamera = { },
            onRemembers = { },
            onProfile = { }
        ) {
            HomeScreenContent(
                userName = "Matias",
                onPlantSelected = { },
                wateringScheduleUIState = wateringScheduleUIEmptyState,
                favouritePlantsUIState = favouritePlantsUIEmptyState
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenEmptyDarkPreview() {
    GreenThumbTheme(darkTheme = true) {
        BaseScreen(
            onHome = { },
            onMyPlants = { },
            onCamera = { },
            onRemembers = { },
            onProfile = { }
        ) {
            HomeScreenContent(
                userName = "Matias",
                onPlantSelected = { },
                wateringScheduleUIState = wateringScheduleUIEmptyState,
                favouritePlantsUIState = favouritePlantsUIEmptyState
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenLoadingPreview() {
    GreenThumbTheme {
        BaseScreen(
            onHome = { },
            onMyPlants = { },
            onCamera = { },
            onRemembers = { },
            onProfile = { }
        ) {
            HomeScreenContent(
                userName = "Matias",
                onPlantSelected = { },
                wateringScheduleUIState = wateringScheduleUILoadingState,
                favouritePlantsUIState = favouritePlantsUILoadingState
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenLoadingDarkPreview() {
    GreenThumbTheme(darkTheme = true) {
        BaseScreen(
            onHome = { },
            onMyPlants = { },
            onCamera = { },
            onRemembers = { },
            onProfile = { }
        ) {
            HomeScreenContent(
                userName = "Matias",
                onPlantSelected = { },
                wateringScheduleUIState = wateringScheduleUILoadingState,
                favouritePlantsUIState = favouritePlantsUILoadingState
            )
        }
    }
}
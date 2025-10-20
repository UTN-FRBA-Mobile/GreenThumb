package com.utn.greenthumb.ui.main.home
import com.utn.greenthumb.R
import com.utn.greenthumb.viewmodel.HomeViewModel
import com.utn.greenthumb.viewmodel.HomeViewModel.FavouritePlantsUIState
import com.utn.greenthumb.viewmodel.HomeViewModel.WateringScheduleUIState
import com.utn.greenthumb.viewmodel.HomeViewModel.Plant
import com.utn.greenthumb.viewmodel.HomeViewModel.WateringReminder
import com.utn.greenthumb.ui.theme.GreenThumbTheme

import android.Manifest
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.utn.greenthumb.domain.model.User
import com.utn.greenthumb.ui.main.BaseScreen
import com.utn.greenthumb.ui.theme.GreenBackground
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.text.SimpleDateFormat
//import com.utn.greenthumb.domain.model.WateringReminderDTO
import java.util.Calendar
import java.util.Date
import kotlin.math.abs
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight


@Composable
fun SearchBar(
    modifier: Modifier = Modifier
) {
    TextField(
        value = "",
        onValueChange = {},
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        placeholder = {
            Text(stringResource(R.string.placeholder_search_favourite))
        },
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
    )
}

@Composable
fun FavouritePlantItem(
    plant: Plant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(plant.imageUrl)
                .crossfade(true)
                .build(),
            placeholder = painterResource(R.drawable.greenthumb),
            error = painterResource(id = R.drawable.greenthumb),
            contentDescription = plant.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .border(4.dp, Color(0xFF2A542C), CircleShape)
                .padding(all = 4.dp)
        )

        val name = if (plant.name.length > 10) {
            plant.name.substring(0, 10) + "..."
        } else {
            plant.name
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
    plants: FavouritePlantsUIState,
    modifier: Modifier = Modifier
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier
    ) {
        itemsIndexed(
            plants.favourites,
            key = { index, plant -> plant.id }
        ) { _, plant ->
            FavouritePlantItem(
                plant = plant,
                onClick = { }
            )
        }
    }
}

@Composable
fun WateringReminderCard (
    reminder: WateringReminder,
    modifier: Modifier = Modifier
) {
    val cardColor = if (reminder.overdue)
        colorResource(R.color.card_red_background)
    else
        colorResource(R.color.card_green_background)

    Surface(
        shape = MaterialTheme.shapes.medium,
        //color = MaterialTheme.colorScheme.surfaceVariant,
        color = cardColor,
        modifier = modifier
            .fillMaxWidth()
            //.padding(start = 10.dp, end = 10.dp, top = 2.dp, bottom = 2.dp)
            .padding(horizontal = 10.dp, vertical = 2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(reminder.plantImageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.greenthumb),
                error = painterResource(id = R.drawable.greenthumb),
                contentDescription = "", //reminder.plantName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .padding(start = 8.dp)
            )
            Column(
                modifier = Modifier
                    //.fillMaxSize()
                    // APLICA el padding del Scaffold aquí para evitar superposición
                    //.padding(padding)
                    .weight(fill = true, weight = 1f)
                    .padding(horizontal = 16.dp, vertical = 4.dp) // Padding adicional opcional
            ) {
                Text(
                    text = reminder.plantName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    //modifier = Modifier.padding(horizontal = 2.dp)
                )
                Text(
                    text = SimpleDateFormat("EEEE, d 'de' MMMM").format(reminder.date),
                    style = MaterialTheme.typography.titleSmall,
                    //modifier = Modifier.padding(horizontal = 2.dp)
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.watering_can_white_red),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                )

                val days = abs(reminder.daysLeft).toString() + " " +
                        if (abs(reminder.daysLeft) > 1 || reminder.daysLeft == 0) {
                            stringResource(R.string.remaining_days)
                        } else {
                            stringResource(R.string.remaining_day)
                        }

                Text (
                    text = days,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(horizontal = 2.dp)
                )
            }
        }
    }
}

@Composable
fun WateringScheduleSection (
    wateringSchedule: WateringScheduleUIState,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier //.height(200.dp)      // TODO : debe utilizar todo el espacio disponible
    ) {
        itemsIndexed(
            items = wateringSchedule.schedule,
            key = { index, reminder -> reminder.id }
        ) { _, reminder ->
            WateringReminderCard(reminder)
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
            //text = stringResource(title),
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .paddingFromBaseline(top = 40.dp, bottom = 16.dp)
        )
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    userName: String?,
    wateringSchedule: WateringScheduleUIState,
    favouritePlants: FavouritePlantsUIState
) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("GreenThumb 🌿") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenBackground)
        )
    }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(top = 8.dp)
            //.border(1.dp, MaterialTheme.colorScheme.onBackground),
            //contentAlignment = Alignment.Center
        ) {
            if (wateringSchedule.isLoading || favouritePlants.isLoading) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Bienvenido, ${userName ?: "Usuario no identificado"}",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            else {
                Column(horizontalAlignment = Alignment.Start) {
                    //SearchBar(Modifier.padding(horizontal = 16.dp))

                    HomeSection(
                        title = stringResource(R.string.favourite_plants_section_title)
                    ) {
                        FavouritePlantSection(favouritePlants)
                    }

                    HomeSection(
                        title = stringResource(R.string.watering_schedule_section_title),
                        modifier = Modifier.weight(1f)
                    ) {
                        WateringScheduleSection(
                            wateringSchedule = wateringSchedule,
                            modifier = Modifier.fillMaxHeight()
                        )
                    }
                }
            }
        }

    }
}


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
    onProfile: () -> Unit
) {
    val context = LocalContext.current

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.d("Home", "Granted ${isGranted}")
            notificationViewModel.refreshToken()
        } else {
            Log.w("HomeScreen", "Notification permission denied")
        }
    }

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
        }
    }

    val clientId = currentUser?.uid ?: ""
    val uiWateringScheduleState = viewModel.uiWateringScheduleState.collectAsState()
    val uiFavouritePlantState = viewModel.uiFavouritePlantState.collectAsState()

    viewModel.fetchWateringSchedule(clientId)
    viewModel.fetchFavouritePlants(clientId)

    BaseScreen(
        onHome = onHome,
        onMyPlants = onMyPlants,
        onCamera = onCamera,
        onRemembers = onRemembers,
        onProfile = onProfile
    ) {
        HomeScreenContent(
            userName = currentUser?.displayName ?: authViewModel.getUserName(),
            wateringSchedule = uiWateringScheduleState.value,
            favouritePlants = uiFavouritePlantState.value
        )
    }
}





private val favouritePlantsUIState = FavouritePlantsUIState(
    isLoading = false,
    isValid = true,
    userMessages = listOf(),
    favourites = listOf(
        Plant(id = "1", name = "Acer palmatum Thunb", imageUrl = "https://www.massogarden.com/images/plantas/Acer_palmatum_Thunb.jpg"),
        Plant(id = "2", name = "Anturio", imageUrl = "https://www.massogarden.com/images/anturio.jpg"),
        Plant(id = "3", name = "Acacia de Constantinopla", imageUrl = "https://www.massogarden.com/images/plantas/Acacia_de_Constantinopla.jpg"),
        Plant(id = "4", name = "Impatiens", imageUrl = "https://www.massogarden.com/images/plantas/Impatiens.jpg"),
        Plant(id = "5", name = "Alegrías de la casa", imageUrl = "https://www.massogarden.com/images/plantas/Impatiens.jpg"),
        Plant(id = "6", name = "Cactus", imageUrl = "https://www.massogarden.com/images/plantas/Cactus.jpg"),
        Plant(id = "7", name = "Cheflera", imageUrl = "https://www.massogarden.com/images/plantas/cheflera.jpg"),
    ),
)

private val wateringScheduleUIState = WateringScheduleUIState(
    isLoading = false,
    isValid = false,
    userMessages = listOf(),
    schedule = listOf(
        WateringReminder(id = "2", plantId = "4", plantName = "Impatients",
            plantImageUrl = "https://www.massogarden.com/images/plantas/Impatiens.jpg",
            date = getDate(-2), daysLeft = -2, overdue = true, checked = false
        ),
        WateringReminder(id = "3", plantId = "2", plantName = "Anturio",
            plantImageUrl = "https://www.massogarden.com/images/anturio.jpg",
            date = getDate(0), daysLeft = 0, overdue = false, checked = false
        ),
        WateringReminder(id = "1", plantId = "6", plantName = "Cactus",
            plantImageUrl = "https://www.massogarden.com/images/plantas/Cactus.jpg",
            date = getDate(4), daysLeft = 4, overdue = false, checked = false
        ),
        WateringReminder(id = "4", plantId = "7", plantName = "Cheflera",
            plantImageUrl = "https://www.massogarden.com/images/plantas/cheflera.jpg",
            date = getDate(6), daysLeft = 6, overdue = false, checked = false
        ),
    ),
)

private fun getDate(days: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DATE, days);
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
            plant = favouritePlantsUIState.favourites.first(),
            onClick = { },
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun FavouritePlantSectionPreview() {
    GreenThumbTheme {
        FavouritePlantSection(
            plants = favouritePlantsUIState,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WateringReminderCardPreview() {
    GreenThumbTheme {
        WateringReminderCard(
            reminder = wateringScheduleUIState.schedule.first(),
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WateringScheduleSectionPreview() {
    GreenThumbTheme {
        WateringScheduleSection(
            wateringSchedule = wateringScheduleUIState,
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
                wateringSchedule = wateringScheduleUIState,
                favouritePlants = favouritePlantsUIState
            )
        }
    }
}


/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    userName: String?
) {
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("GreenThumb 🌿") },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = GreenBackground)
        )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Bienvenido, ${userName ?: "Usuario no identificado"}",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }
}


@Preview
@Composable
fun HomeScreenPreview(
    authViewModel: AuthViewModel = AuthViewModel(
        authRepository = TODO(),
        authManager = TODO()
    ),
    currentUser: User? = null,
    onHome: () -> Unit = {},
    onProfile: () -> Unit = {},
    onCamera: () -> Unit = {}
) {

}

 */
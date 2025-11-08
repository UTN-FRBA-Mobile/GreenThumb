package com.utn.greenthumb.ui.main

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.utn.greenthumb.R
import com.utn.greenthumb.ui.theme.DarkGreen
import com.utn.greenthumb.ui.theme.Green
import com.utn.greenthumb.ui.theme.GreenBackground
import com.utn.greenthumb.ui.theme.GreenThumbTheme

@Composable
fun BaseScreen(
    onHome: () -> Unit,
    onMyPlants: () -> Unit,
    onCamera: () -> Unit,
    onRemembers: () -> Unit,
    onProfile: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    val activity = LocalContext.current as Activity
    LaunchedEffect(Unit) {
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    Scaffold(
        bottomBar = {
            BottomBar(
                onHome = onHome,
                onMyPlants = onMyPlants,
                onCamera = onCamera,
                onRemembers = onRemembers,
                onProfile = onProfile
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)) {
            content(innerPadding)
        }
    }
}


@Composable
fun BottomBar(
    onHome: () -> Unit,
    onMyPlants: () -> Unit,
    onCamera: () -> Unit,
    onRemembers: () -> Unit,
    onProfile: () -> Unit
) {
    val insets = WindowInsets.navigationBars.asPaddingValues()
    val bottomPadding = insets.calculateBottomPadding()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp + bottomPadding)
            .background(DarkGreen)
            .windowInsetsPadding(WindowInsets.navigationBars),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomButtonScale(
            icon = painterResource(R.drawable.home),
            text = stringResource(R.string.home),
            onClick = onHome
        )
        BottomButtonScale(
            icon = painterResource(R.drawable.leafs),
            text = stringResource(R.string.my_plants),
            onClick = onMyPlants
        )
        CameraButton(
            icon = painterResource(R.drawable.photo_camera),
            onClick = onCamera
        )
        BottomButtonScale(
            icon = painterResource(R.drawable.reminder),
            text = stringResource(R.string.remembers),
            onClick = onRemembers
        )
        BottomButtonScale(
            icon = painterResource(R.drawable.profile),
            text = stringResource(R.string.profile),
            onClick = onProfile,
        )
    }
}


@Composable
fun RowScope.BottomButtonScale(
    icon: Painter,
    text: String? = "",
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> isPressed = true
                is PressInteraction.Release -> isPressed = false
                is PressInteraction.Cancel -> isPressed = false
            }
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.85f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = icon,
            contentDescription = text,
            tint = GreenBackground,
            modifier = Modifier.size(36.dp)
        )

        if (!text.isNullOrEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = text,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = GreenBackground
            )
        }
    }
}


@Composable
fun RowScope.CameraButton(
    icon: Painter,
    onClick: () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> isPressed = true
                is PressInteraction.Release -> isPressed = false
                is PressInteraction.Cancel -> isPressed = false
            }
        }
    }

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val backgroundColor by animateColorAsState(
        targetValue = if (isPressed) Green.copy(alpha = 0.7f) else Green,
        animationSpec = tween(durationMillis = 100),
        label = "camera_background"
    )

    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Botón circular flotante
        Box(
            modifier = Modifier
                .size(72.dp)
                .offset(y = (-32).dp)
                .scale(scale)
                .background(backgroundColor, shape = CircleShape)
                .border(
                    width = 3.dp,
                    color = DarkGreen,
                    shape = CircleShape
                )
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = stringResource(R.string.camera_screen_title),
                tint = GreenBackground,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}


/**
 * PREVIEWS
 */

@Preview(
    showBackground = true,
    name = "Base Screen - Modo Claro"
)
@Composable
fun BaseScreenLightPreview() {
    GreenThumbTheme {
        BaseScreen(
            onHome = { },
            onMyPlants = { },
            onCamera = { },
            onRemembers = { },
            onProfile = { }
        ) { }
    }
}

@Preview(
    showBackground = true,
    name = "Base Screen - Modo Oscuro",
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun BaseScreenDarkPreview() {
    GreenThumbTheme {
        BaseScreen(
            onHome = { },
            onMyPlants = { },
            onCamera = { },
            onRemembers = { },
            onProfile = { }
        ) { }
    }
}
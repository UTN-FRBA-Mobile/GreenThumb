package com.utn.greenthumb.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.utn.greenthumb.R
import com.utn.greenthumb.ui.navigation.NavRoutes
import com.utn.greenthumb.ui.theme.DarkGreen
import com.utn.greenthumb.ui.theme.Green
import com.utn.greenthumb.ui.theme.GreenBackground

@Composable
fun BaseScreen(
    onHome: () -> Unit,
    onProfile: () -> Unit,
    onCamera: () -> Unit,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomBar(
                onHome = onHome,
                onProfile = onProfile,
                onCamera = onCamera
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
    onProfile: () -> Unit,
    onCamera: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(DarkGreen),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomButton(
            icon = painterResource(R.drawable.home),
            text = stringResource(R.string.home),
            onClick = onHome
        )
        BottomButton(
            icon = painterResource(R.drawable.leafs),
            text = stringResource(R.string.my_plants),
            onClick = { }
        )
        BottomButton(
            icon = painterResource(R.drawable.photo_camera),
            onClick = onCamera,
            modifier = Modifier
                .size(72.dp)
                .offset(y = (-32).dp)
                .background(Green, shape = CircleShape)
        )
        BottomButton(
            icon = painterResource(R.drawable.reminder),
            text = stringResource(R.string.remembers),
            onClick = { }
        )
        BottomButton(
            icon = painterResource(R.drawable.profile),
            text = stringResource(R.string.profile),
            onClick = onProfile,
        )
    }
}

@Composable
fun RowScope.BottomButton(
    icon: Painter,
    modifier: Modifier = Modifier,
    text: String? = "",
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
            ) { onClick() },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = icon,
                contentDescription = stringResource(R.string.my_plants),
                tint = GreenBackground,
                modifier = Modifier.size(36.dp)
            )
        }

        if (text != null) {
            Text(
                text = text,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                color = GreenBackground
            )
        }

    }
}


@Preview
@Composable
fun BaseScreenPreview() {
    BaseScreen(
        onHome = { },
        onProfile = { },
        onCamera = { }
    ) {

    }
}
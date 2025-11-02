package com.utn.greenthumb.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.utn.greenthumb.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GreenThumbTopAppBar(
    title: String,
    onNavigateBack: () -> Unit,
    visible: Boolean = true,
    enabled: Boolean = true
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            navigationIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        title = {
            Text(text = title)
        },
        navigationIcon = {
            if (visible) {
                IconButton(
                    onClick = onNavigateBack,
                    enabled = enabled
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_navigation)
                    )
                }
            }
        }
    )
}
package com.bignerdranch.android.watertrackerapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun WaterTrackerAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF0A84FF),
            onPrimary = Color.White,
            background = Color(0xFFF2F2F7),
            onBackground = Color.Black
        ),
        typography = Typography(),
        content = content
    )
}

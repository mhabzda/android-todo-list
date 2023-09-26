package com.mhabzda.todolist.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val marginDefault = 16.dp

private val LightColors = lightColorScheme(
    primary = Color(0xFF052CA3),
    onPrimary = Color.White,
    secondary = Color(0xFFD9911A),
    onSecondary = Color.White,
)
private val DarkColors = darkColorScheme(
    primary = Color(0xFF051DA3),
    onPrimary = Color.White,
    secondary = Color(0xFF8C5B0A),
    onSecondary = Color.White,
)

private val Shapes = Shapes()
private val Typography = Typography()

@Composable
fun TodoListTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (!useDarkTheme) {
        LightColors
    } else {
        DarkColors
    }

    MaterialTheme(
        colorScheme = colors,
        shapes = Shapes,
        typography = Typography,
        content = content,
    )
}
// app/src/main/java/com/example/baosapp/ui/theme/Theme.kt
package com.example.baosapp.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary   = LightBlue,
    secondary = MediumBlue,
    background= DarkBlue,
    surface   = AccentCream,
    onPrimary = DarkBlue,
    onSecondary = AccentCream,
    onBackground= AccentCream,
    onSurface = DarkBlue

)

@Composable
fun BañosAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = AppTypography,     // si ya tienes tu Typography definido
        content = content
    )
}

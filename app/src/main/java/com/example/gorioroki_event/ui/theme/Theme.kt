package com.example.gorioroki_event.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun GoRioRoki_EventTheme(content: @Composable () -> Unit) {
    // Menggunakan skema warna default (Light Theme)
    val colorScheme = lightColorScheme()

    // Menggunakan tipografi default
    val typography = Typography()

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}

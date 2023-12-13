package com.pangbai.weblog.global

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb

object themeManager {
    @Composable
    @JvmStatic
 fun   getColorSurface():Int{
   return  MaterialTheme.colorScheme.surface.toArgb()
 }
}
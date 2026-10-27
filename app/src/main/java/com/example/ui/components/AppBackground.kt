package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import com.example.data.settings.AppSettings
import com.example.data.settings.BackgroundType
import com.example.ui.theme.BackgroundPresets

/** Latar belakang seluruh aplikasi: gradien bawaan atau foto pilihan dari galeri. */
@Composable
fun AppBackground(
  settings: AppSettings,
  darkTheme: Boolean,
  modifier: Modifier = Modifier
) {
  val preset = BackgroundPresets.getOrElse(settings.presetIndex) { BackgroundPresets.first() }
  val bitmap by rememberLocalBitmap(
    path = if (settings.backgroundType == BackgroundType.IMAGE) settings.backgroundImagePath else "",
    maxSize = 1600
  )

  Box(modifier = modifier.fillMaxSize()) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(preset.brush(darkTheme))
    )
    val image = bitmap
    if (image != null) {
      Image(
        bitmap = image,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )
      // Lapisan gelap agar teks tetap terbaca di atas foto.
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = settings.dim))
      )
    }
  }
}

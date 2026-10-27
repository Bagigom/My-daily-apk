package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/*
 * Efek "kaca" (glass): warna surface dibuat sedikit transparan supaya latar belakang
 * (gradien atau foto dari galeri) terlihat samar di balik kartu. Warna surfaceContainer*
 * sengaja dibuat solid agar dialog, bottom sheet, dan menu tetap mudah dibaca.
 */
private val DarkColorScheme = darkColorScheme(
  primary = PrimaryDark,
  onPrimary = OnPrimaryDark,
  primaryContainer = PrimaryContainerDark,
  onPrimaryContainer = OnPrimaryContainerDark,
  secondary = SecondaryDark,
  onSecondary = OnSecondaryDark,
  secondaryContainer = SecondaryContainerDark,
  onSecondaryContainer = OnSecondaryContainerDark,
  tertiary = TertiaryDark,
  onTertiary = OnTertiaryDark,
  tertiaryContainer = TertiaryContainerDark,
  onTertiaryContainer = OnTertiaryContainerDark,
  error = ErrorDark,
  onError = OnErrorDark,
  errorContainer = ErrorContainerDark,
  onErrorContainer = OnErrorContainerDark,
  background = BackgroundDark,
  onBackground = OnBackgroundDark,
  surface = Color(0xFF16142E).copy(alpha = 0.80f),
  onSurface = OnSurfaceDark,
  surfaceVariant = Color(0xFF2A2750).copy(alpha = 0.70f),
  onSurfaceVariant = OnSurfaceVariantDark,
  outline = OutlineDark,
  surfaceContainerLowest = Color(0xFF0E0D22),
  surfaceContainerLow = Color(0xFF16142E),
  surfaceContainer = Color(0xFF1C1A38),
  surfaceContainerHigh = Color(0xFF232044),
  surfaceContainerHighest = Color(0xFF2B2850),
)

private val LightColorScheme = lightColorScheme(
  primary = PrimaryLight,
  onPrimary = OnPrimaryLight,
  primaryContainer = PrimaryContainerLight,
  onPrimaryContainer = OnPrimaryContainerLight,
  secondary = SecondaryLight,
  onSecondary = OnSecondaryLight,
  secondaryContainer = SecondaryContainerLight,
  onSecondaryContainer = OnSecondaryContainerLight,
  tertiary = TertiaryLight,
  onTertiary = OnTertiaryLight,
  tertiaryContainer = TertiaryContainerLight,
  onTertiaryContainer = OnTertiaryContainerLight,
  error = ErrorLight,
  onError = OnErrorLight,
  errorContainer = ErrorContainerLight,
  onErrorContainer = OnErrorContainerLight,
  background = BackgroundLight,
  onBackground = OnBackgroundLight,
  surface = Color.White.copy(alpha = 0.86f),
  onSurface = OnSurfaceLight,
  surfaceVariant = Color(0xFFEDEBFB).copy(alpha = 0.78f),
  onSurfaceVariant = OnSurfaceVariantLight,
  outline = OutlineLight,
  surfaceContainerLowest = Color.White,
  surfaceContainerLow = Color(0xFFF8F7FF),
  surfaceContainer = Color(0xFFF1EFFC),
  surfaceContainerHigh = Color(0xFFEBE9F8),
  surfaceContainerHighest = Color(0xFFE4E2F4),
)

private val AppShapes = Shapes(
  extraSmall = RoundedCornerShape(8.dp),
  small = RoundedCornerShape(12.dp),
  medium = RoundedCornerShape(18.dp),
  large = RoundedCornerShape(24.dp),
  extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
    typography = Typography,
    shapes = AppShapes,
    content = content,
  )
}

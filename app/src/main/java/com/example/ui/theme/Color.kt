package com.example.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ---------- Light ----------
val PrimaryLight = Color(0xFF5B4BDB)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFE6E2FF)
val OnPrimaryContainerLight = Color(0xFF241A7A)

val SecondaryLight = Color(0xFF0E9F8E)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFD3F7F0)
val OnSecondaryContainerLight = Color(0xFF0B4F47)

val TertiaryLight = Color(0xFFE8710A)
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFFFE8CF)
val OnTertiaryContainerLight = Color(0xFF6B3200)

val ErrorLight = Color(0xFFDC2626)
val OnErrorLight = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFFEE2E2)
val OnErrorContainerLight = Color(0xFF991B1B)

val BackgroundLight = Color(0xFFF5F4FF)
val OnBackgroundLight = Color(0xFF14122B)
val OnSurfaceLight = Color(0xFF14122B)
val OnSurfaceVariantLight = Color(0xFF4B4968)
val OutlineLight = Color(0xFFC9C6E3)

// ---------- Dark ----------
val PrimaryDark = Color(0xFFA79BFF)
val OnPrimaryDark = Color(0xFF1B1360)
val PrimaryContainerDark = Color(0xFF3A2FB0)
val OnPrimaryContainerDark = Color(0xFFE6E2FF)

val SecondaryDark = Color(0xFF3FE0C8)
val OnSecondaryDark = Color(0xFF00382F)
val SecondaryContainerDark = Color(0xFF0B5A4F)
val OnSecondaryContainerDark = Color(0xFFD3F7F0)

val TertiaryDark = Color(0xFFFFB366)
val OnTertiaryDark = Color(0xFF4A2200)
val TertiaryContainerDark = Color(0xFF8A4300)
val OnTertiaryContainerDark = Color(0xFFFFE8CF)

val ErrorDark = Color(0xFFF87171)
val OnErrorDark = Color(0xFF7F1D1D)
val ErrorContainerDark = Color(0xFF991B1B)
val OnErrorContainerDark = Color(0xFFFEE2E2)

val BackgroundDark = Color(0xFF0B0A1A)
val OnBackgroundDark = Color(0xFFF1EFFF)
val OnSurfaceDark = Color(0xFFF1EFFF)
val OnSurfaceVariantDark = Color(0xFFB4B0D6)
val OutlineDark = Color(0xFF47446B)

// ---------- Aksen ----------
val SuccessGreen = Color(0xFF10B981)
val SuccessGreenContainer = Color(0xFFD1FAE5)
val WarningAmber = Color(0xFFF59E0B)
val IncomeGreen = Color(0xFF059669)
val ExpenseRed = Color(0xFFE11D48)
val TimerAccent = Color(0xFF6366F1)

// ---------- Gradien kartu utama (hero) ----------
object Gradients {
  private fun diagonal(vararg colors: Color) =
    Brush.linearGradient(colors = colors.toList(), start = Offset.Zero, end = Offset.Infinite)

  val Violet = diagonal(Color(0xFF5B4BDB), Color(0xFF8B5CF6), Color(0xFFD946EF))
  val Ocean = diagonal(Color(0xFF0F4C81), Color(0xFF2563EB), Color(0xFF06B6D4))
  val Sunset = diagonal(Color(0xFFF97316), Color(0xFFEC4899), Color(0xFF8B5CF6))
  val Emerald = diagonal(Color(0xFF047857), Color(0xFF10B981), Color(0xFF84CC16))
}

// ---------- Pilihan latar belakang bawaan ----------
class BackgroundPreset(
  val name: String,
  private val light: List<Color>,
  private val dark: List<Color>
) {
  fun colors(darkTheme: Boolean): List<Color> = if (darkTheme) dark else light

  fun brush(darkTheme: Boolean): Brush =
    Brush.linearGradient(colors = colors(darkTheme), start = Offset.Zero, end = Offset.Infinite)
}

val BackgroundPresets = listOf(
  BackgroundPreset(
    "Aurora",
    listOf(Color(0xFFE0E7FF), Color(0xFFF3E8FF), Color(0xFFFCE7F3)),
    listOf(Color(0xFF0B0A1A), Color(0xFF1B1245), Color(0xFF2A0F3D))
  ),
  BackgroundPreset(
    "Samudra",
    listOf(Color(0xFFCFFAFE), Color(0xFFDBEAFE), Color(0xFFE0E7FF)),
    listOf(Color(0xFF04121F), Color(0xFF0B2A4A), Color(0xFF0E3B5C))
  ),
  BackgroundPreset(
    "Senja",
    listOf(Color(0xFFFFEDD5), Color(0xFFFFE4E6), Color(0xFFF3E8FF)),
    listOf(Color(0xFF1A0B0B), Color(0xFF3B1226), Color(0xFF2B1650))
  ),
  BackgroundPreset(
    "Hutan",
    listOf(Color(0xFFDCFCE7), Color(0xFFD1FAE5), Color(0xFFCFFAFE)),
    listOf(Color(0xFF04140D), Color(0xFF0B2E22), Color(0xFF0C3B3B))
  ),
  BackgroundPreset(
    "Mawar",
    listOf(Color(0xFFFFE4E6), Color(0xFFFCE7F3), Color(0xFFFAE8FF)),
    listOf(Color(0xFF1C0A14), Color(0xFF3A0F2C), Color(0xFF2A1048))
  ),
  BackgroundPreset(
    "Mentari",
    listOf(Color(0xFFFEF9C3), Color(0xFFFFEDD5), Color(0xFFFFE4E6)),
    listOf(Color(0xFF1A1405), Color(0xFF3A260B), Color(0xFF3B1A12))
  ),
  BackgroundPreset(
    "Kabut",
    listOf(Color(0xFFF1F5F9), Color(0xFFE2E8F0), Color(0xFFF8FAFC)),
    listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF111827))
  ),
  BackgroundPreset(
    "Tengah Malam",
    listOf(Color(0xFFC7D2FE), Color(0xFFA5B4FC), Color(0xFFDDD6FE)),
    listOf(Color(0xFF020617), Color(0xFF0F0A2E), Color(0xFF020617))
  )
)

package com.example.data.settings

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class ThemeMode { SYSTEM, LIGHT, DARK }

enum class BackgroundType { PRESET, IMAGE }

data class AppSettings(
  val themeMode: ThemeMode = ThemeMode.SYSTEM,
  val backgroundType: BackgroundType = BackgroundType.PRESET,
  val presetIndex: Int = 0,
  val backgroundImagePath: String = "",
  val dim: Float = 0.25f
)

/** Menyimpan preferensi tampilan di perangkat. Tidak memakai jaringan sama sekali. */
class SettingsRepository(context: Context) {
  private val prefs = context.applicationContext
    .getSharedPreferences("dailyfocus_settings", Context.MODE_PRIVATE)

  private val _settings = MutableStateFlow(load())
  val settings: StateFlow<AppSettings> = _settings.asStateFlow()

  private fun load(): AppSettings {
    val themeMode = runCatching {
      ThemeMode.valueOf(prefs.getString("theme_mode", ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name)
    }.getOrDefault(ThemeMode.SYSTEM)
    val bgType = runCatching {
      BackgroundType.valueOf(prefs.getString("bg_type", BackgroundType.PRESET.name) ?: BackgroundType.PRESET.name)
    }.getOrDefault(BackgroundType.PRESET)
    return AppSettings(
      themeMode = themeMode,
      backgroundType = bgType,
      presetIndex = prefs.getInt("preset_index", 0),
      backgroundImagePath = prefs.getString("bg_image_path", "") ?: "",
      dim = prefs.getFloat("dim", 0.25f)
    )
  }

  private fun update(transform: (AppSettings) -> AppSettings) {
    val next = transform(_settings.value)
    _settings.value = next
    prefs.edit()
      .putString("theme_mode", next.themeMode.name)
      .putString("bg_type", next.backgroundType.name)
      .putInt("preset_index", next.presetIndex)
      .putString("bg_image_path", next.backgroundImagePath)
      .putFloat("dim", next.dim)
      .apply()
  }

  fun setThemeMode(mode: ThemeMode) = update { it.copy(themeMode = mode) }

  fun selectPreset(index: Int) = update {
    it.copy(backgroundType = BackgroundType.PRESET, presetIndex = index)
  }

  fun setBackgroundImage(path: String) = update {
    it.copy(backgroundType = BackgroundType.IMAGE, backgroundImagePath = path)
  }

  fun clearBackgroundImage() = update {
    it.copy(backgroundType = BackgroundType.PRESET, backgroundImagePath = "")
  }

  fun setDim(value: Float) = update { it.copy(dim = value.coerceIn(0f, 0.8f)) }
}

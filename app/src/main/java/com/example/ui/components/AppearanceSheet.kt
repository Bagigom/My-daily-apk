package com.example.ui.components

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.settings.AppSettings
import com.example.data.settings.BackgroundType
import com.example.data.settings.ThemeMode
import com.example.ui.theme.BackgroundPresets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceSheet(
  settings: AppSettings,
  darkTheme: Boolean,
  onDismiss: () -> Unit,
  onThemeModeChange: (ThemeMode) -> Unit,
  onPresetSelected: (Int) -> Unit,
  onImagePicked: (Uri) -> Unit,
  onClearImage: () -> Unit,
  onDimChange: (Float) -> Unit
) {
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  val openGallery = rememberGalleryPicker(onPicked = onImagePicked)
  val hasImage = settings.backgroundType == BackgroundType.IMAGE && settings.backgroundImagePath.isNotBlank()
  val previewBitmap by rememberLocalBitmap(
    path = if (hasImage) settings.backgroundImagePath else "",
    maxSize = 800
  )

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .navigationBarsPadding()
        .padding(horizontal = 20.dp)
        .padding(bottom = 24.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Text(
        text = "Tampilan & Latar Belakang",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.ExtraBold
      )

      // ---- Mode tema ----
      SectionLabel("Mode Tema")
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        ThemeChip("Ikuti Sistem", settings.themeMode == ThemeMode.SYSTEM) {
          onThemeModeChange(ThemeMode.SYSTEM)
        }
        ThemeChip("Terang", settings.themeMode == ThemeMode.LIGHT) {
          onThemeModeChange(ThemeMode.LIGHT)
        }
        ThemeChip("Gelap", settings.themeMode == ThemeMode.DARK) {
          onThemeModeChange(ThemeMode.DARK)
        }
      }

      // ---- Gradien bawaan ----
      SectionLabel("Warna Latar")
      Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        BackgroundPresets.forEachIndexed { index, preset ->
          val selected = settings.backgroundType == BackgroundType.PRESET && settings.presetIndex == index
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable { onPresetSelected(index) }
          ) {
            Box(
              contentAlignment = Alignment.Center,
              modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(preset.brush(darkTheme))
                .border(
                  border = if (selected) BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
                  else BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                  shape = RoundedCornerShape(18.dp)
                )
            ) {
              if (selected) {
                Icon(
                  Icons.Default.Check,
                  contentDescription = "Dipilih",
                  tint = MaterialTheme.colorScheme.primary
                )
              }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = preset.name,
              fontSize = 11.sp,
              fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }

      // ---- Foto dari galeri ----
      SectionLabel("Foto dari Galeri")
      Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
          .fillMaxWidth()
          .aspectRatio(16f / 9f)
          .clip(RoundedCornerShape(20.dp))
          .background(MaterialTheme.colorScheme.surfaceContainerHigh)
          .clickable { openGallery() }
      ) {
        val bmp = previewBitmap
        if (hasImage && bmp != null) {
          Image(
            bitmap = bmp,
            contentDescription = "Pratinjau latar belakang",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
          )
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(Color.Black.copy(alpha = settings.dim))
          )
        } else {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              Icons.Default.PhotoLibrary,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Ketuk untuk memilih foto",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
          onClick = openGallery,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier.weight(1f)
        ) {
          Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Pilih dari Galeri")
        }
        if (hasImage) {
          OutlinedButton(
            onClick = onClearImage,
            shape = RoundedCornerShape(14.dp)
          ) {
            Icon(Icons.Default.DeleteOutline, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Hapus")
          }
        }
      }

      if (hasImage) {
        SectionLabel("Gelapkan Foto (${(settings.dim * 100).toInt()}%)")
        Slider(
          value = settings.dim,
          onValueChange = onDimChange,
          valueRange = 0f..0.8f
        )
      }

      Text(
        text = "Foto disalin dan disimpan hanya di perangkat ini. Tidak ada yang diunggah ke internet.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }
  }
}

@Composable
private fun SectionLabel(text: String) {
  Text(
    text = text,
    style = MaterialTheme.typography.labelMedium,
    fontWeight = FontWeight.Bold,
    color = MaterialTheme.colorScheme.primary
  )
}

@Composable
private fun ThemeChip(label: String, selected: Boolean, onClick: () -> Unit) {
  FilterChip(
    selected = selected,
    onClick = onClick,
    label = { Text(label, fontSize = 12.sp) }
  )
}

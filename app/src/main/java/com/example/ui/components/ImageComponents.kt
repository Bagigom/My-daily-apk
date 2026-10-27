package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.example.util.ImageStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Membuka galeri (Android Photo Picker). Tidak butuh izin penyimpanan.
 * Mengembalikan fungsi yang dipanggil saat tombol ditekan.
 */
@Composable
fun rememberGalleryPicker(onPicked: (Uri) -> Unit): () -> Unit {
  val launcher = rememberLauncherForActivityResult(
    ActivityResultContracts.PickVisualMedia()
  ) { uri ->
    if (uri != null) onPicked(uri)
  }
  return {
    launcher.launch(
      PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
    )
  }
}

/** Memuat gambar dari file internal secara asinkron (tidak memblokir UI). */
@Composable
fun rememberLocalBitmap(path: String, maxSize: Int): State<ImageBitmap?> {
  return produceState<ImageBitmap?>(initialValue = null, path, maxSize) {
    value = if (path.isBlank()) {
      null
    } else {
      withContext(Dispatchers.IO) {
        ImageStore.decodeFile(path, maxSize)?.asImageBitmap()
      }
    }
  }
}

/** Pratinjau gambar yang baru dipilih dari galeri (sebelum disimpan). */
@Composable
fun rememberUriBitmap(uri: Uri?, maxSize: Int): State<ImageBitmap?> {
  val context = LocalContext.current
  return produceState<ImageBitmap?>(initialValue = null, uri, maxSize) {
    value = if (uri == null) {
      null
    } else {
      withContext(Dispatchers.IO) {
        ImageStore.decodeUri(context, uri, maxSize)?.asImageBitmap()
      }
    }
  }
}

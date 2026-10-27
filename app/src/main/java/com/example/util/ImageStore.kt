package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import kotlin.math.max

/**
 * Mengimpor gambar yang dipilih dari galeri ke penyimpanan internal aplikasi,
 * sehingga tetap tersedia walau foto aslinya dipindah/dihapus, dan tanpa izin
 * akses penyimpanan maupun internet.
 *
 * Semua fungsi di sini berat (I/O + decode), panggil dari Dispatchers.IO.
 */
object ImageStore {

  private fun imagesDir(context: Context): File =
    File(context.applicationContext.filesDir, "images").apply { mkdirs() }

  /** Menyalin [uri] menjadi JPEG yang sudah diperkecil. Mengembalikan path file, atau null jika gagal. */
  fun importImage(context: Context, uri: Uri, prefix: String, maxSize: Int): String? {
    return try {
      val resolver = context.contentResolver

      val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
      resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
      if (bounds.outWidth <= 0 || bounds.outHeight <= 0) return null

      val opts = BitmapFactory.Options().apply {
        inSampleSize = calculateSampleSize(bounds.outWidth, bounds.outHeight, maxSize)
      }
      val decoded = resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
        ?: return null

      val rotation = resolver.openInputStream(uri)?.use { readRotation(it) } ?: 0
      val longest = max(decoded.width, decoded.height)
      val scale = if (longest > maxSize) maxSize.toFloat() / longest else 1f

      val matrix = Matrix().apply {
        if (rotation != 0) postRotate(rotation.toFloat())
        if (scale < 1f) postScale(scale, scale)
      }
      val finalBitmap = if (rotation != 0 || scale < 1f) {
        Bitmap.createBitmap(decoded, 0, 0, decoded.width, decoded.height, matrix, true)
      } else decoded

      val file = File(imagesDir(context), "${prefix}_${System.currentTimeMillis()}.jpg")
      FileOutputStream(file).use { out ->
        finalBitmap.compress(Bitmap.CompressFormat.JPEG, 88, out)
      }
      if (finalBitmap !== decoded) decoded.recycle()
      file.absolutePath
    } catch (e: Exception) {
      null
    }
  }

  /** Membaca bitmap dari path internal dengan ukuran terbatas supaya hemat memori. */
  fun decodeFile(path: String, reqMax: Int): Bitmap? {
    return try {
      val file = File(path)
      if (!file.exists()) return null
      val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
      BitmapFactory.decodeFile(path, bounds)
      val opts = BitmapFactory.Options().apply {
        inSampleSize = calculateSampleSize(bounds.outWidth, bounds.outHeight, reqMax)
      }
      BitmapFactory.decodeFile(path, opts)
    } catch (e: Exception) {
      null
    }
  }

  /** Untuk pratinjau gambar yang baru dipilih (belum disalin). */
  fun decodeUri(context: Context, uri: Uri, reqMax: Int): Bitmap? {
    return try {
      val resolver = context.contentResolver
      val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
      resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
      val opts = BitmapFactory.Options().apply {
        inSampleSize = calculateSampleSize(bounds.outWidth, bounds.outHeight, reqMax)
      }
      resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
    } catch (e: Exception) {
      null
    }
  }

  fun delete(path: String) {
    if (path.isBlank()) return
    runCatching { File(path).delete() }
  }

  private fun calculateSampleSize(width: Int, height: Int, reqMax: Int): Int {
    var sample = 1
    val longest = max(width, height)
    while (longest / (sample * 2) >= reqMax) sample *= 2
    return sample
  }

  private fun readRotation(stream: java.io.InputStream): Int {
    return try {
      when (ExifInterface(stream).getAttributeInt(
        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
      )) {
        ExifInterface.ORIENTATION_ROTATE_90 -> 90
        ExifInterface.ORIENTATION_ROTATE_180 -> 180
        ExifInterface.ORIENTATION_ROTATE_270 -> 270
        else -> 0
      }
    } catch (e: Exception) {
      0
    }
  }
}

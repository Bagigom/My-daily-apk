package com.example.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateTimeUtils {
  private val indonesianLocale = Locale("id", "ID")
  private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
  private val displayDateFormat = SimpleDateFormat("EEEE, d MMMM yyyy", indonesianLocale)
  private val shortDateFormat = SimpleDateFormat("d MMM yyyy", indonesianLocale)
  private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
  private val monthPrefixFormat = SimpleDateFormat("yyyy-MM", Locale.getDefault())
  private val monthDisplayFormat = SimpleDateFormat("MMMM yyyy", indonesianLocale)

  fun getTodayDate(): String {
    return dateFormat.format(Date())
  }

  fun getCurrentMonthPrefix(): String {
    return monthPrefixFormat.format(Date())
  }

  fun getCurrentMonthDisplay(): String {
    return monthDisplayFormat.format(Date())
  }

  fun formatDisplayDate(dateStr: String): String {
    return try {
      val parsed = dateFormat.parse(dateStr) ?: return dateStr
      displayDateFormat.format(parsed)
    } catch (e: Exception) {
      dateStr
    }
  }

  fun formatShortDate(dateStr: String): String {
    return try {
      val parsed = dateFormat.parse(dateStr) ?: return dateStr
      shortDateFormat.format(parsed)
    } catch (e: Exception) {
      dateStr
    }
  }

  fun formatTime(timeMillis: Long): String {
    return timeFormat.format(Date(timeMillis))
  }

  fun formatDuration(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) {
      String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
      String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }
  }

  fun formatDurationHuman(totalSeconds: Long): String {
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    return when {
      hours > 0 && minutes > 0 -> "${hours}j ${minutes}m"
      hours > 0 -> "${hours} jam"
      minutes > 0 -> "${minutes} menit"
      else -> "${totalSeconds} detik"
    }
  }

  fun formatRupiah(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(indonesianLocale)
    format.maximumFractionDigits = 0
    return format.format(amount).replace("Rp", "Rp ")
  }

  fun getLast7Days(): List<Pair<String, String>> {
    val list = mutableListOf<Pair<String, String>>()
    val cal = Calendar.getInstance()
    val dayNameFormat = SimpleDateFormat("EEE", indonesianLocale)

    for (i in 6 downTo 0) {
      val dayCal = Calendar.getInstance()
      dayCal.add(Calendar.DAY_OF_YEAR, -i)
      val dateStr = dateFormat.format(dayCal.time)
      val dayName = dayNameFormat.format(dayCal.time)
      list.add(Pair(dateStr, dayName))
    }
    return list
  }
}

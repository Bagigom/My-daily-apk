package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "timer_sessions")
data class TimerSessionEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val activityTitle: String,
  val category: String = "Fokus",
  val startTimeMillis: Long,
  val endTimeMillis: Long,
  val durationSeconds: Long,
  val date: String, // "yyyy-MM-dd"
  val weekOfYear: Int = 0,
  val year: Int = 0,
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

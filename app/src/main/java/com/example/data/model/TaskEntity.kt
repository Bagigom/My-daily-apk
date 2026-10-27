package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val description: String = "",
  val category: String = "Pribadi",
  val priority: String = "Sedang", // "Tinggi", "Sedang", "Rendah"
  val date: String, // "yyyy-MM-dd"
  val isCompleted: Boolean = false,
  val completedAt: Long? = null,
  val createdAt: Long = System.currentTimeMillis()
)

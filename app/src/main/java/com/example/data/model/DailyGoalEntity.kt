package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_goals")
data class DailyGoalEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val iconEmoji: String = "🎯",
  val targetValue: Int, // e.g. 2000 ml, 30 mins, 5 times
  val currentValue: Int = 0,
  val unit: String = "kali", // "ml", "menit", "halaman", "kali", "km"
  val stepIncrement: Int = 1, // e.g. +250 ml, +1, +5
  val streak: Int = 0,
  val lastUpdatedDate: String = "", // "yyyy-MM-dd"
  val isCompleted: Boolean = false,
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "wishlist_goals")
data class WishlistEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val targetAmount: Double,
  val savedAmount: Double = 0.0,
  val category: String = "Elektronik",
  val targetDate: String = "", // "yyyy-MM-dd"
  val notes: String = "",
  val isAchieved: Boolean = false,
  val imagePath: String = "", // path file foto di penyimpanan internal (dipilih dari galeri)
  val createdAt: Long = System.currentTimeMillis()
)

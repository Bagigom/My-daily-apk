package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val title: String,
  val amount: Double,
  val type: String, // "EXPENSE" or "INCOME"
  val category: String = "Makanan",
  val date: String, // "yyyy-MM-dd"
  val notes: String = "",
  val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "finance_targets")
data class FinanceTargetEntity(
  @PrimaryKey val id: Long = 1,
  val monthlyBudgetLimit: Double = 3000000.0,
  val monthlySavingsTarget: Double = 1500000.0,
  val targetMonth: String = "" // "yyyy-MM"
)

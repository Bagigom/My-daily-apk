package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.DailyGoalEntity
import com.example.data.model.FinanceTargetEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TimerSessionEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.WishlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyFocusDao {

  // --- Tasks ---
  @Query("SELECT * FROM tasks ORDER BY isCompleted ASC, createdAt DESC")
  fun getAllTasks(): Flow<List<TaskEntity>>

  @Query("SELECT * FROM tasks WHERE date = :date ORDER BY isCompleted ASC, createdAt DESC")
  fun getTasksByDate(date: String): Flow<List<TaskEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTask(task: TaskEntity): Long

  @Update
  suspend fun updateTask(task: TaskEntity)

  @Delete
  suspend fun deleteTask(task: TaskEntity)

  @Query("DELETE FROM tasks WHERE id = :id")
  suspend fun deleteTaskById(id: Long)

  // --- Timer Sessions ---
  @Query("SELECT * FROM timer_sessions ORDER BY startTimeMillis DESC")
  fun getAllTimerSessions(): Flow<List<TimerSessionEntity>>

  @Query("SELECT * FROM timer_sessions WHERE date = :date ORDER BY startTimeMillis DESC")
  fun getTimerSessionsByDate(date: String): Flow<List<TimerSessionEntity>>

  @Query("SELECT * FROM timer_sessions WHERE date >= :startDate AND date <= :endDate ORDER BY startTimeMillis DESC")
  fun getTimerSessionsBetween(startDate: String, endDate: String): Flow<List<TimerSessionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTimerSession(session: TimerSessionEntity): Long

  @Delete
  suspend fun deleteTimerSession(session: TimerSessionEntity)

  // --- Transactions ---
  @Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC")
  fun getAllTransactions(): Flow<List<TransactionEntity>>

  @Query("SELECT * FROM transactions WHERE date = :date ORDER BY createdAt DESC")
  fun getTransactionsByDate(date: String): Flow<List<TransactionEntity>>

  @Query("SELECT * FROM transactions WHERE date LIKE :monthPrefix || '%' ORDER BY date DESC, createdAt DESC")
  fun getTransactionsByMonth(monthPrefix: String): Flow<List<TransactionEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTransaction(transaction: TransactionEntity): Long

  @Update
  suspend fun updateTransaction(transaction: TransactionEntity)

  @Delete
  suspend fun deleteTransaction(transaction: TransactionEntity)

  @Query("DELETE FROM transactions WHERE id = :id")
  suspend fun deleteTransactionById(id: Long)

  // --- Finance Target ---
  @Query("SELECT * FROM finance_targets WHERE id = 1 LIMIT 1")
  fun getFinanceTarget(): Flow<FinanceTargetEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrUpdateFinanceTarget(target: FinanceTargetEntity)

  // --- Daily Goals ---
  @Query("SELECT * FROM daily_goals ORDER BY isCompleted ASC, id ASC")
  fun getAllDailyGoals(): Flow<List<DailyGoalEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertDailyGoal(goal: DailyGoalEntity): Long

  @Update
  suspend fun updateDailyGoal(goal: DailyGoalEntity)

  @Delete
  suspend fun deleteDailyGoal(goal: DailyGoalEntity)

  @Query("DELETE FROM daily_goals WHERE id = :id")
  suspend fun deleteDailyGoalById(id: Long)

  // --- Wishlist ---
  @Query("SELECT * FROM wishlist_goals ORDER BY isAchieved ASC, createdAt DESC")
  fun getAllWishlists(): Flow<List<WishlistEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertWishlist(wishlist: WishlistEntity): Long

  @Update
  suspend fun updateWishlist(wishlist: WishlistEntity)

  @Delete
  suspend fun deleteWishlist(wishlist: WishlistEntity)

  @Query("DELETE FROM wishlist_goals WHERE id = :id")
  suspend fun deleteWishlistById(id: Long)

  @Query("UPDATE wishlist_goals SET imagePath = :path WHERE id = :id")
  suspend fun updateWishlistImage(id: Long, path: String)
}

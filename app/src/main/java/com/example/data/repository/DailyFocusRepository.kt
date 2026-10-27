package com.example.data.repository

import com.example.data.local.DailyFocusDao
import com.example.data.model.DailyGoalEntity
import com.example.data.model.FinanceTargetEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TimerSessionEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.WishlistEntity
import com.example.util.DateTimeUtils
import kotlinx.coroutines.flow.Flow

class DailyFocusRepository(private val dao: DailyFocusDao) {

  // Tasks
  val allTasks: Flow<List<TaskEntity>> = dao.getAllTasks()

  fun getTasksByDate(date: String): Flow<List<TaskEntity>> = dao.getTasksByDate(date)

  suspend fun insertTask(task: TaskEntity): Long = dao.insertTask(task)

  suspend fun updateTask(task: TaskEntity) = dao.updateTask(task)

  suspend fun deleteTask(task: TaskEntity) = dao.deleteTask(task)

  suspend fun deleteTaskById(id: Long) = dao.deleteTaskById(id)

  suspend fun toggleTaskCompletion(task: TaskEntity) {
    val newCompleted = !task.isCompleted
    val completedAt = if (newCompleted) System.currentTimeMillis() else null
    dao.updateTask(task.copy(isCompleted = newCompleted, completedAt = completedAt))
  }

  // Timer Sessions
  val allTimerSessions: Flow<List<TimerSessionEntity>> = dao.getAllTimerSessions()

  fun getTimerSessionsByDate(date: String): Flow<List<TimerSessionEntity>> =
    dao.getTimerSessionsByDate(date)

  fun getTimerSessionsBetween(startDate: String, endDate: String): Flow<List<TimerSessionEntity>> =
    dao.getTimerSessionsBetween(startDate, endDate)

  suspend fun insertTimerSession(session: TimerSessionEntity): Long =
    dao.insertTimerSession(session)

  suspend fun deleteTimerSession(session: TimerSessionEntity) =
    dao.deleteTimerSession(session)

  // Transactions & Finance
  val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()

  fun getTransactionsByMonth(monthPrefix: String): Flow<List<TransactionEntity>> =
    dao.getTransactionsByMonth(monthPrefix)

  suspend fun insertTransaction(transaction: TransactionEntity): Long =
    dao.insertTransaction(transaction)

  suspend fun updateTransaction(transaction: TransactionEntity) =
    dao.updateTransaction(transaction)

  suspend fun deleteTransaction(transaction: TransactionEntity) =
    dao.deleteTransaction(transaction)

  suspend fun deleteTransactionById(id: Long) = dao.deleteTransactionById(id)

  val financeTarget: Flow<FinanceTargetEntity?> = dao.getFinanceTarget()

  suspend fun saveFinanceTarget(budgetLimit: Double, savingsTarget: Double) {
    dao.insertOrUpdateFinanceTarget(
      FinanceTargetEntity(
        id = 1,
        monthlyBudgetLimit = budgetLimit,
        monthlySavingsTarget = savingsTarget,
        targetMonth = DateTimeUtils.getCurrentMonthPrefix()
      )
    )
  }

  // Daily Goals (Habits)
  val allDailyGoals: Flow<List<DailyGoalEntity>> = dao.getAllDailyGoals()

  suspend fun insertDailyGoal(goal: DailyGoalEntity): Long = dao.insertDailyGoal(goal)

  suspend fun updateDailyGoal(goal: DailyGoalEntity) = dao.updateDailyGoal(goal)

  suspend fun deleteDailyGoal(goal: DailyGoalEntity) = dao.deleteDailyGoal(goal)

  suspend fun deleteDailyGoalById(id: Long) = dao.deleteDailyGoalById(id)

  suspend fun incrementDailyGoalProgress(goal: DailyGoalEntity, step: Int) {
    val today = DateTimeUtils.getTodayDate()
    val baseValue = if (goal.lastUpdatedDate != today) 0 else goal.currentValue
    val newValue = (baseValue + step).coerceAtLeast(0)
    val isDone = newValue >= goal.targetValue
    val newStreak = if (isDone && !goal.isCompleted) goal.streak + 1 else goal.streak
    dao.updateDailyGoal(
      goal.copy(
        currentValue = newValue,
        isCompleted = isDone,
        lastUpdatedDate = today,
        streak = newStreak
      )
    )
  }

  suspend fun resetDailyGoal(goal: DailyGoalEntity) {
    val today = DateTimeUtils.getTodayDate()
    dao.updateDailyGoal(
      goal.copy(
        currentValue = 0,
        isCompleted = false,
        lastUpdatedDate = today
      )
    )
  }

  // Wishlist (Target Keinginan)
  val allWishlists: Flow<List<WishlistEntity>> = dao.getAllWishlists()

  suspend fun insertWishlist(wishlist: WishlistEntity): Long = dao.insertWishlist(wishlist)

  suspend fun updateWishlist(wishlist: WishlistEntity) = dao.updateWishlist(wishlist)

  suspend fun deleteWishlist(wishlist: WishlistEntity) = dao.deleteWishlist(wishlist)

  suspend fun deleteWishlistById(id: Long) = dao.deleteWishlistById(id)

  suspend fun updateWishlistImage(id: Long, path: String) = dao.updateWishlistImage(id, path)

  suspend fun addWishlistSavings(wishlist: WishlistEntity, addedAmount: Double) {
    val newSaved = (wishlist.savedAmount + addedAmount).coerceAtLeast(0.0)
    val isAchieved = newSaved >= wishlist.targetAmount
    dao.updateWishlist(
      wishlist.copy(
        savedAmount = newSaved,
        isAchieved = isAchieved
      )
    )
  }
}

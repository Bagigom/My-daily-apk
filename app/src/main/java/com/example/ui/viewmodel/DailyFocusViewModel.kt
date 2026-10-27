package com.example.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DailyFocusDatabase
import com.example.data.model.DailyGoalEntity
import com.example.data.model.FinanceTargetEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TimerSessionEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.WishlistEntity
import com.example.data.repository.DailyFocusRepository
import com.example.data.settings.AppSettings
import com.example.data.settings.SettingsRepository
import com.example.data.settings.ThemeMode
import com.example.util.ImageStore
import kotlinx.coroutines.Dispatchers
import com.example.util.DateTimeUtils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab(val title: String) {
  TASKS("Tugas"),
  TIMER("Timer"),
  FINANCE("Keuangan"),
  TARGETS("Target")
}

enum class TaskFilter {
  ALL, TODAY, PENDING, COMPLETED
}

enum class TimerMode {
  STOPWATCH, POMODORO
}

enum class TimerPeriod {
  DAILY, WEEKLY
}

enum class FinanceFilter {
  ALL, THIS_MONTH, INCOME_ONLY, EXPENSE_ONLY
}

enum class TargetTab {
  DAILY_HABITS, WISHLIST
}

data class WeeklyDayStat(
  val date: String,
  val dayLabel: String,
  val totalSeconds: Long,
  val formattedDuration: String
)

class DailyFocusViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: DailyFocusRepository
  private val settingsRepository = SettingsRepository(application)

  // --- Tampilan & latar belakang ---
  val settings: StateFlow<AppSettings> = settingsRepository.settings

  fun setThemeMode(mode: ThemeMode) = settingsRepository.setThemeMode(mode)

  fun selectBackgroundPreset(index: Int) = settingsRepository.selectPreset(index)

  fun setBackgroundDim(value: Float) = settingsRepository.setDim(value)

  fun setBackgroundImage(uri: Uri) {
    viewModelScope.launch(Dispatchers.IO) {
      val newPath = ImageStore.importImage(getApplication(), uri, "bg", 1600) ?: return@launch
      val oldPath = settingsRepository.settings.value.backgroundImagePath
      settingsRepository.setBackgroundImage(newPath)
      if (oldPath != newPath) ImageStore.delete(oldPath)
    }
  }

  fun clearBackgroundImage() {
    viewModelScope.launch(Dispatchers.IO) {
      val oldPath = settingsRepository.settings.value.backgroundImagePath
      settingsRepository.clearBackgroundImage()
      ImageStore.delete(oldPath)
    }
  }

  init {
    val database = DailyFocusDatabase.getDatabase(application, viewModelScope)
    repository = DailyFocusRepository(database.dailyFocusDao())
  }

  // --- Main Tab Navigation ---
  private val _currentTab = MutableStateFlow(MainTab.TASKS)
  val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

  fun setTab(tab: MainTab) {
    _currentTab.value = tab
  }

  // --- Task Feature ---
  private val _taskFilter = MutableStateFlow(TaskFilter.ALL)
  val taskFilter: StateFlow<TaskFilter> = _taskFilter.asStateFlow()

  private val _taskCategoryFilter = MutableStateFlow<String?>(null)
  val taskCategoryFilter: StateFlow<String?> = _taskCategoryFilter.asStateFlow()

  val tasks: StateFlow<List<TaskEntity>> = combine(
    repository.allTasks,
    _taskFilter,
    _taskCategoryFilter
  ) { list, filter, category ->
    val today = DateTimeUtils.getTodayDate()
    list.filter { task ->
      val matchesCategory = category == null || task.category == category
      val matchesStatus = when (filter) {
        TaskFilter.ALL -> true
        TaskFilter.TODAY -> task.date == today
        TaskFilter.PENDING -> !task.isCompleted
        TaskFilter.COMPLETED -> task.isCompleted
      }
      matchesCategory && matchesStatus
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun setTaskFilter(filter: TaskFilter) {
    _taskFilter.value = filter
  }

  fun setTaskCategoryFilter(category: String?) {
    _taskCategoryFilter.value = category
  }

  fun toggleTaskCompletion(task: TaskEntity) {
    viewModelScope.launch {
      repository.toggleTaskCompletion(task)
    }
  }

  fun addTask(
    title: String,
    description: String,
    category: String,
    priority: String,
    date: String
  ) {
    viewModelScope.launch {
      repository.insertTask(
        TaskEntity(
          title = title.trim(),
          description = description.trim(),
          category = category,
          priority = priority,
          date = date,
          isCompleted = false
        )
      )
    }
  }

  fun deleteTask(task: TaskEntity) {
    viewModelScope.launch {
      repository.deleteTask(task)
    }
  }

  fun updateTask(task: TaskEntity) {
    viewModelScope.launch {
      repository.updateTask(task)
    }
  }

  // --- Live Timer Engine ---
  private val _timerMode = MutableStateFlow(TimerMode.STOPWATCH)
  val timerMode: StateFlow<TimerMode> = _timerMode.asStateFlow()

  private val _isTimerRunning = MutableStateFlow(false)
  val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

  private val _isTimerPaused = MutableStateFlow(false)
  val isTimerPaused: StateFlow<Boolean> = _isTimerPaused.asStateFlow()

  private val _elapsedSeconds = MutableStateFlow(0L)
  val elapsedSeconds: StateFlow<Long> = _elapsedSeconds.asStateFlow()

  private val _pomodoroTargetMinutes = MutableStateFlow(25)
  val pomodoroTargetMinutes: StateFlow<Int> = _pomodoroTargetMinutes.asStateFlow()

  private val _timerTitle = MutableStateFlow("")
  val timerTitle: StateFlow<String> = _timerTitle.asStateFlow()

  private val _timerCategory = MutableStateFlow("Fokus")
  val timerCategory: StateFlow<String> = _timerCategory.asStateFlow()

  private var timerJob: Job? = null
  private var sessionStartTimeMillis: Long = 0L

  fun setTimerMode(mode: TimerMode) {
    if (!_isTimerRunning.value) {
      _timerMode.value = mode
      if (mode == TimerMode.POMODORO) {
        _elapsedSeconds.value = _pomodoroTargetMinutes.value * 60L
      } else {
        _elapsedSeconds.value = 0L
      }
    }
  }

  fun setPomodoroMinutes(minutes: Int) {
    if (!_isTimerRunning.value) {
      _pomodoroTargetMinutes.value = minutes
      if (_timerMode.value == TimerMode.POMODORO) {
        _elapsedSeconds.value = minutes * 60L
      }
    }
  }

  fun setTimerActivity(title: String, category: String) {
    _timerTitle.value = title
    _timerCategory.value = category
  }

  fun startTimer() {
    if (_isTimerRunning.value) return
    if (!_isTimerPaused.value) {
      sessionStartTimeMillis = System.currentTimeMillis()
      if (_timerMode.value == TimerMode.POMODORO && _elapsedSeconds.value == 0L) {
        _elapsedSeconds.value = _pomodoroTargetMinutes.value * 60L
      }
    }
    _isTimerRunning.value = true
    _isTimerPaused.value = false

    timerJob?.cancel()
    timerJob = viewModelScope.launch {
      while (_isTimerRunning.value) {
        delay(1000)
        if (_timerMode.value == TimerMode.STOPWATCH) {
          _elapsedSeconds.value += 1
        } else {
          if (_elapsedSeconds.value > 0) {
            _elapsedSeconds.value -= 1
            if (_elapsedSeconds.value == 0L) {
              finishAndSaveTimer()
              break
            }
          }
        }
      }
    }
  }

  fun pauseTimer() {
    if (_isTimerRunning.value) {
      _isTimerRunning.value = false
      _isTimerPaused.value = true
      timerJob?.cancel()
    }
  }

  fun resetTimer() {
    _isTimerRunning.value = false
    _isTimerPaused.value = false
    timerJob?.cancel()
    if (_timerMode.value == TimerMode.POMODORO) {
      _elapsedSeconds.value = _pomodoroTargetMinutes.value * 60L
    } else {
      _elapsedSeconds.value = 0L
    }
  }

  fun finishAndSaveTimer() {
    val duration: Long = if (_timerMode.value == TimerMode.STOPWATCH) {
      _elapsedSeconds.value
    } else {
      (_pomodoroTargetMinutes.value * 60L) - _elapsedSeconds.value
    }

    val endTime = System.currentTimeMillis()
    val startTime = if (sessionStartTimeMillis > 0) sessionStartTimeMillis else endTime - (duration * 1000)

    if (duration >= 5) { // Save sessions that lasted at least 5 seconds
      val title = _timerTitle.value.ifBlank { "Sesi Fokus Mandiri" }
      val category = _timerCategory.value.ifBlank { "Fokus" }
      val today = DateTimeUtils.getTodayDate()

      viewModelScope.launch {
        repository.insertTimerSession(
          TimerSessionEntity(
            activityTitle = title,
            category = category,
            startTimeMillis = startTime,
            endTimeMillis = endTime,
            durationSeconds = duration,
            date = today
          )
        )
      }
    }

    resetTimer()
  }

  // --- Timer History & Weekly Stats ---
  private val _timerPeriod = MutableStateFlow(TimerPeriod.DAILY)
  val timerPeriod: StateFlow<TimerPeriod> = _timerPeriod.asStateFlow()

  fun setTimerPeriod(period: TimerPeriod) {
    _timerPeriod.value = period
  }

  val allTimerSessions: StateFlow<List<TimerSessionEntity>> =
    repository.allTimerSessions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val weeklyStats: StateFlow<List<WeeklyDayStat>> = combine(allTimerSessions) { sessionsList ->
    val sessions = sessionsList[0]
    val last7Days = DateTimeUtils.getLast7Days()
    last7Days.map { (date, label) ->
      val daySessions = sessions.filter { it.date == date }
      val totalSecs = daySessions.sumOf { it.durationSeconds }
      WeeklyDayStat(
        date = date,
        dayLabel = label,
        totalSeconds = totalSecs,
        formattedDuration = DateTimeUtils.formatDurationHuman(totalSecs)
      )
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun deleteTimerSession(session: TimerSessionEntity) {
    viewModelScope.launch {
      repository.deleteTimerSession(session)
    }
  }

  // --- Finance Feature ---
  private val _financeFilter = MutableStateFlow(FinanceFilter.ALL)
  val financeFilter: StateFlow<FinanceFilter> = _financeFilter.asStateFlow()

  val financeTarget: StateFlow<FinanceTargetEntity?> =
    repository.financeTarget.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  val allTransactions: StateFlow<List<TransactionEntity>> =
    repository.allTransactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val filteredTransactions: StateFlow<List<TransactionEntity>> = combine(
    repository.allTransactions,
    _financeFilter
  ) { list, filter ->
    val currentMonth = DateTimeUtils.getCurrentMonthPrefix()
    when (filter) {
      FinanceFilter.ALL -> list
      FinanceFilter.THIS_MONTH -> list.filter { it.date.startsWith(currentMonth) }
      FinanceFilter.INCOME_ONLY -> list.filter { it.type == "INCOME" }
      FinanceFilter.EXPENSE_ONLY -> list.filter { it.type == "EXPENSE" }
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun setFinanceFilter(filter: FinanceFilter) {
    _financeFilter.value = filter
  }

  fun addTransaction(
    title: String,
    amount: Double,
    type: String,
    category: String,
    date: String,
    notes: String
  ) {
    viewModelScope.launch {
      repository.insertTransaction(
        TransactionEntity(
          title = title.trim(),
          amount = amount,
          type = type,
          category = category,
          date = date,
          notes = notes.trim()
        )
      )
    }
  }

  fun deleteTransaction(transaction: TransactionEntity) {
    viewModelScope.launch {
      repository.deleteTransaction(transaction)
    }
  }

  fun updateFinanceTarget(budgetLimit: Double, savingsTarget: Double) {
    viewModelScope.launch {
      repository.saveFinanceTarget(budgetLimit, savingsTarget)
    }
  }

  // --- Custom Targets (Daily Goals & Wishlist) ---
  private val _targetTab = MutableStateFlow(TargetTab.DAILY_HABITS)
  val targetTab: StateFlow<TargetTab> = _targetTab.asStateFlow()

  fun setTargetTab(tab: TargetTab) {
    _targetTab.value = tab
  }

  val dailyGoals: StateFlow<List<DailyGoalEntity>> =
    repository.allDailyGoals.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val wishlistGoals: StateFlow<List<WishlistEntity>> =
    repository.allWishlists.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  fun incrementDailyGoal(goal: DailyGoalEntity, step: Int = goal.stepIncrement) {
    viewModelScope.launch {
      repository.incrementDailyGoalProgress(goal, step)
    }
  }

  fun resetDailyGoal(goal: DailyGoalEntity) {
    viewModelScope.launch {
      repository.resetDailyGoal(goal)
    }
  }

  fun addDailyGoal(
    title: String,
    iconEmoji: String,
    targetValue: Int,
    unit: String,
    stepIncrement: Int
  ) {
    viewModelScope.launch {
      repository.insertDailyGoal(
        DailyGoalEntity(
          title = title.trim(),
          iconEmoji = iconEmoji.ifBlank { "🎯" },
          targetValue = targetValue.coerceAtLeast(1),
          currentValue = 0,
          unit = unit.trim().ifBlank { "kali" },
          stepIncrement = stepIncrement.coerceAtLeast(1),
          lastUpdatedDate = DateTimeUtils.getTodayDate()
        )
      )
    }
  }

  fun updateDailyGoal(goal: DailyGoalEntity) {
    viewModelScope.launch {
      repository.updateDailyGoal(goal)
    }
  }

  fun deleteDailyGoal(goal: DailyGoalEntity) {
    viewModelScope.launch {
      repository.deleteDailyGoal(goal)
    }
  }

  fun addWishlist(
    title: String,
    targetAmount: Double,
    savedAmount: Double,
    category: String,
    targetDate: String,
    notes: String,
    imageUri: Uri? = null
  ) {
    viewModelScope.launch(Dispatchers.IO) {
      val imagePath = imageUri?.let { ImageStore.importImage(getApplication(), it, "wish", 1280) } ?: ""
      repository.insertWishlist(
        WishlistEntity(
          title = title.trim(),
          targetAmount = targetAmount,
          savedAmount = savedAmount,
          category = category,
          targetDate = targetDate,
          notes = notes.trim(),
          isAchieved = savedAmount >= targetAmount,
          imagePath = imagePath
        )
      )
    }
  }

  fun setWishlistImage(wishlist: WishlistEntity, uri: Uri) {
    viewModelScope.launch(Dispatchers.IO) {
      val newPath = ImageStore.importImage(getApplication(), uri, "wish", 1280) ?: return@launch
      repository.updateWishlistImage(wishlist.id, newPath)
      ImageStore.delete(wishlist.imagePath)
    }
  }

  fun removeWishlistImage(wishlist: WishlistEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.updateWishlistImage(wishlist.id, "")
      ImageStore.delete(wishlist.imagePath)
    }
  }

  fun addWishlistSavings(wishlist: WishlistEntity, amountToAdd: Double) {
    viewModelScope.launch {
      repository.addWishlistSavings(wishlist, amountToAdd)
    }
  }

  fun updateWishlist(wishlist: WishlistEntity) {
    viewModelScope.launch {
      repository.updateWishlist(wishlist)
    }
  }

  fun deleteWishlist(wishlist: WishlistEntity) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.deleteWishlist(wishlist)
      ImageStore.delete(wishlist.imagePath)
    }
  }
}

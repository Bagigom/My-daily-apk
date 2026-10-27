package com.example.ui.screens.timer

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TimerSessionEntity
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TimerAccent
import com.example.ui.viewmodel.DailyFocusViewModel
import com.example.ui.viewmodel.TimerMode
import com.example.ui.viewmodel.TimerPeriod
import com.example.util.DateTimeUtils

@Composable
fun TimerScreen(
  viewModel: DailyFocusViewModel,
  modifier: Modifier = Modifier
) {
  val timerMode by viewModel.timerMode.collectAsState()
  val isRunning by viewModel.isTimerRunning.collectAsState()
  val isPaused by viewModel.isTimerPaused.collectAsState()
  val elapsedSeconds by viewModel.elapsedSeconds.collectAsState()
  val pomodoroMins by viewModel.pomodoroTargetMinutes.collectAsState()
  val activityTitle by viewModel.timerTitle.collectAsState()
  val activityCategory by viewModel.timerCategory.collectAsState()
  val timerPeriod by viewModel.timerPeriod.collectAsState()
  val allSessions by viewModel.allTimerSessions.collectAsState()
  val weeklyStats by viewModel.weeklyStats.collectAsState()

  val today = DateTimeUtils.getTodayDate()
  val todaySessions = allSessions.filter { it.date == today }
  val totalTodaySeconds = todaySessions.sumOf { it.durationSeconds }
  val totalWeeklySeconds = weeklyStats.sumOf { it.totalSeconds }

  val categories = listOf("Fokus", "Kerja", "Belajar", "Olahraga", "Membaca", "Meditasi", "Lainnya")

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("timer_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Card: Mode Selector & Live Stopwatch / Pomodoro Timer
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
          containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          // Timer Mode Selector (Stopwatch vs Pomodoro)
          Row(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(MaterialTheme.colorScheme.surface)
              .padding(4.dp),
            horizontalArrangement = Arrangement.Center
          ) {
            FilterChip(
              selected = timerMode == TimerMode.STOPWATCH,
              onClick = { viewModel.setTimerMode(TimerMode.STOPWATCH) },
              label = { Text("Stopwatch", fontSize = 12.sp) },
              enabled = !isRunning && !isPaused
            )
            Spacer(modifier = Modifier.width(6.dp))
            FilterChip(
              selected = timerMode == TimerMode.POMODORO,
              onClick = { viewModel.setTimerMode(TimerMode.POMODORO) },
              label = { Text("Pomodoro", fontSize = 12.sp) },
              enabled = !isRunning && !isPaused
            )
          }

          if (timerMode == TimerMode.POMODORO && !isRunning && !isPaused) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf(15, 25, 45, 60).forEach { mins ->
                FilterChip(
                  selected = pomodoroMins == mins,
                  onClick = { viewModel.setPomodoroMinutes(mins) },
                  label = { Text("${mins}m", fontSize = 11.sp) }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Activity Title input & Category Chips
          OutlinedTextField(
            value = activityTitle,
            onValueChange = { viewModel.setTimerActivity(it, activityCategory) },
            label = { Text("Aktivitas yang sedang dikerjakan") },
            placeholder = { Text("Misal: Belajar Kotlin / Coding Feature") },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("timer_activity_input")
          )

          Spacer(modifier = Modifier.height(8.dp))

          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(categories) { cat ->
              FilterChip(
                selected = activityCategory == cat,
                onClick = { viewModel.setTimerActivity(activityTitle, cat) },
                label = { Text(cat, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                  selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Big Digital Timer Clock Display
          val infiniteTransition = rememberInfiniteTransition(label = "pulse")
          val pulseScale by infiniteTransition.animateFloat(
            initialValue = 1f,
            targetValue = if (isRunning) 1.04f else 1f,
            animationSpec = infiniteRepeatable(
              animation = tween(1000, easing = FastOutSlowInEasing),
              repeatMode = RepeatMode.Reverse
            ),
            label = "pulseScale"
          )

          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(200.dp)
              .scale(pulseScale)
              .clip(CircleShape)
              .background(
                if (isRunning) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                else MaterialTheme.colorScheme.surface
              )
              .padding(16.dp)
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = DateTimeUtils.formatDuration(elapsedSeconds),
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace,
                color = if (isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("timer_clock_display")
              )
              Text(
                text = when {
                  isRunning -> "Sedang Berjalan..."
                  isPaused -> "Dijeda"
                  else -> "Siap Mulai"
                },
                style = MaterialTheme.typography.labelSmall,
                color = if (isRunning) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Control Buttons: Start / Pause, Finish & Save, Reset
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (isRunning) {
              // Pause Button
              Button(
                onClick = { viewModel.pauseTimer() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("pause_timer_button")
              ) {
                Icon(Icons.Default.Pause, contentDescription = "Jeda")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Jeda")
              }
            } else {
              // Start / Resume Button
              Button(
                onClick = { viewModel.startTimer() },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("start_timer_button")
              ) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Mulai")
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (isPaused) "Lanjutkan" else "Mulai Timer")
              }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Finish and Save Button (enabled when duration > 0)
            if (isRunning || isPaused || elapsedSeconds > 0) {
              Button(
                onClick = { viewModel.finishAndSaveTimer() },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("finish_timer_button")
              ) {
                Icon(Icons.Default.Check, contentDescription = "Selesai")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Selesai")
              }

              Spacer(modifier = Modifier.width(8.dp))

              IconButton(
                onClick = { viewModel.resetTimer() },
                modifier = Modifier.testTag("reset_timer_button")
              ) {
                Icon(
                  Icons.Default.Refresh,
                  contentDescription = "Reset Timer",
                  tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }

    // Tab Header: Harian vs Mingguan (Daily & Weekly Views)
    item {
      TabRow(
        selectedTabIndex = if (timerPeriod == TimerPeriod.DAILY) 0 else 1,
        containerColor = MaterialTheme.colorScheme.surface
      ) {
        Tab(
          selected = timerPeriod == TimerPeriod.DAILY,
          onClick = { viewModel.setTimerPeriod(TimerPeriod.DAILY) },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Statistik Harian")
            }
          }
        )
        Tab(
          selected = timerPeriod == TimerPeriod.WEEKLY,
          onClick = { viewModel.setTimerPeriod(TimerPeriod.WEEKLY) },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.BarChart, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Statistik Mingguan")
            }
          }
        )
      }
    }

    // Daily View
    if (timerPeriod == TimerPeriod.DAILY) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Total Fokus Hari Ini",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
              )
              Text(
                text = DateTimeUtils.formatDurationHuman(totalTodaySeconds),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
              )
            }
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.secondary,
              contentColor = MaterialTheme.colorScheme.onSecondary
            ) {
              Text(
                text = "${todaySessions.size} Sesi",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }

      if (todaySessions.isEmpty()) {
        item {
          Text(
            text = "Belum ada sesi fokus yang tersimpan hari ini. Mulai timermu sekarang!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(16.dp)
          )
        }
      } else {
        item {
          Text(
            text = "Riwayat Sesi Hari Ini",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
        }

        items(todaySessions, key = { it.id }) { session ->
          TimerSessionItem(
            session = session,
            onDelete = { viewModel.deleteTimerSession(session) }
          )
        }
      }
    } else {
      // Weekly View
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
          )
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "Total Fokus 7 Hari Terakhir",
              style = MaterialTheme.typography.titleSmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
              text = DateTimeUtils.formatDurationHuman(totalWeeklySeconds),
              fontSize = 24.sp,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
              text = "Rata-rata: ${DateTimeUtils.formatDurationHuman(totalWeeklySeconds / 7)} / hari",
              style = MaterialTheme.typography.bodySmall,
              color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bar Chart Columns for 7 Days
            val maxSeconds = (weeklyStats.maxOfOrNull { it.totalSeconds } ?: 1L).coerceAtLeast(1800L) // min 30 min scale

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .padding(top = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Bottom
            ) {
              weeklyStats.forEach { dayStat ->
                val barRatio = (dayStat.totalSeconds.toFloat() / maxSeconds).coerceIn(0.04f, 1f)
                val isToday = dayStat.date == today

                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier = Modifier.weight(1f)
                ) {
                  // Duration label above bar if non-zero
                  if (dayStat.totalSeconds > 0) {
                    Text(
                      text = "${dayStat.totalSeconds / 60}m",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.Bold,
                      color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                  }

                  Spacer(modifier = Modifier.height(4.dp))

                  // Visual Bar
                  Box(
                    modifier = Modifier
                      .width(22.dp)
                      .height((90 * barRatio).dp)
                      .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                      .background(
                        if (isToday) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                      )
                  )

                  Spacer(modifier = Modifier.height(6.dp))

                  // Day Label (Sen, Sel, Rab...)
                  Text(
                    text = dayStat.dayLabel,
                    fontSize = 11.sp,
                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                  )
                }
              }
            }
          }
        }
      }

      item {
        Text(
          text = "Rincian Waktu Harian Mingguan",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }

      items(weeklyStats) { stat ->
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
          )
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "${stat.dayLabel}, ${DateTimeUtils.formatShortDate(stat.date)}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = if (stat.totalSeconds > 0) stat.formattedDuration else "Belum ada sesi",
                style = MaterialTheme.typography.bodySmall,
                color = if (stat.totalSeconds > 0) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.surfaceVariant
            ) {
              Text(
                text = "${stat.totalSeconds / 60} menit",
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun TimerSessionItem(
  session: TimerSessionEntity,
  onDelete: () -> Unit
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    )
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(42.dp)
          .clip(CircleShape)
          .background(TimerAccent.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          Icons.Default.Timer,
          contentDescription = null,
          tint = TimerAccent,
          modifier = Modifier.size(22.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = session.activityTitle,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.secondaryContainer
          ) {
            Text(
              text = session.category,
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSecondaryContainer,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          Text(
            text = "${DateTimeUtils.formatTime(session.startTimeMillis)} - ${DateTimeUtils.formatTime(session.endTimeMillis)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.primaryContainer
      ) {
        Text(
          text = DateTimeUtils.formatDurationHuman(session.durationSeconds),
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.onPrimaryContainer,
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
      }

      IconButton(onClick = onDelete) {
        Icon(
          Icons.Default.DeleteOutline,
          contentDescription = "Hapus Sesi",
          tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
        )
      }
    }
  }
}

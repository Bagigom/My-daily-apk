package com.example.ui.screens.tasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EventNote
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskEntity
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.Gradients
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.DailyFocusViewModel
import com.example.ui.viewmodel.TaskFilter
import com.example.util.DateTimeUtils

@Composable
fun TasksScreen(
  viewModel: DailyFocusViewModel,
  modifier: Modifier = Modifier
) {
  val tasks by viewModel.tasks.collectAsState()
  val activeFilter by viewModel.taskFilter.collectAsState()
  val activeCategory by viewModel.taskCategoryFilter.collectAsState()
  var showAddDialog by remember { mutableStateOf(false) }

  val totalTasks = tasks.size
  val completedTasks = tasks.count { it.isCompleted }
  val progress = if (totalTasks > 0) completedTasks.toFloat() / totalTasks else 0f

  val categories = listOf("Semua", "Kerja", "Belajar", "Ibadah", "Pribadi", "Kesehatan", "Keuangan")

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("tasks_list"),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      // Header Card: Daily Progress
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
          )
        ) {
          Column(
            modifier = Modifier.background(Gradients.Violet).padding(20.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "Checklist Tugas Harian",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = Color.White
                )
                Text(
                  text = DateTimeUtils.formatDisplayDate(DateTimeUtils.getTodayDate()),
                  style = MaterialTheme.typography.bodySmall,
                  color = Color.White.copy(alpha = 0.8f)
                )
              }
              Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.22f),
                contentColor = Color.White
              ) {
                Text(
                  text = "$completedTasks/$totalTasks",
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            LinearProgressIndicator(
              progress = { progress },
              modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(5.dp)),
              color = Color.White,
              trackColor = Color.White.copy(alpha = 0.25f)
            )

            Spacer(modifier = Modifier.height(8.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = if (progress >= 1f && totalTasks > 0) "Luar biasa! Semua tugas selesai 🎉" else "Terus semangat capai target hari ini!",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
              )
              Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }

      // Filter Chips (Status)
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          TaskFilterChip(
            label = "Semua",
            selected = activeFilter == TaskFilter.ALL,
            onClick = { viewModel.setTaskFilter(TaskFilter.ALL) }
          )
          TaskFilterChip(
            label = "Hari Ini",
            selected = activeFilter == TaskFilter.TODAY,
            onClick = { viewModel.setTaskFilter(TaskFilter.TODAY) }
          )
          TaskFilterChip(
            label = "Belum Selesai",
            selected = activeFilter == TaskFilter.PENDING,
            onClick = { viewModel.setTaskFilter(TaskFilter.PENDING) }
          )
          TaskFilterChip(
            label = "Selesai",
            selected = activeFilter == TaskFilter.COMPLETED,
            onClick = { viewModel.setTaskFilter(TaskFilter.COMPLETED) }
          )
        }
      }

      // Filter Row (Category)
      item {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(categories) { cat ->
            val isSelected = if (cat == "Semua") activeCategory == null else activeCategory == cat
            FilterChip(
              selected = isSelected,
              onClick = {
                viewModel.setTaskCategoryFilter(if (cat == "Semua") null else cat)
              },
              label = { Text(cat, fontSize = 12.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
              )
            )
          }
        }
      }

      // Empty State
      if (tasks.isEmpty()) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(top = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
              containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.EventNote,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "Belum ada kegiatan",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Tekan tombol + di bawah untuk menambah kegiatan harianmu.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      }

      // Tasks List Items
      items(tasks, key = { it.id }) { task ->
        TaskItemCard(
          task = task,
          onToggle = { viewModel.toggleTaskCompletion(task) },
          onDelete = { viewModel.deleteTask(task) }
        )
      }
    }

    // Floating Action Button to Add Task
    FloatingActionButton(
      onClick = { showAddDialog = true },
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(bottom = 16.dp, end = 20.dp)
        .testTag("add_task_fab"),
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
      Icon(Icons.Default.Add, contentDescription = "Tambah Kegiatan")
    }

    if (showAddDialog) {
      AddTaskDialog(
        onDismiss = { showAddDialog = false },
        onConfirm = { title, desc, category, priority, date ->
          viewModel.addTask(title, desc, category, priority, date)
          showAddDialog = false
        }
      )
    }
  }
}

@Composable
private fun TaskFilterChip(
  label: String,
  selected: Boolean,
  onClick: () -> Unit
) {
  FilterChip(
    selected = selected,
    onClick = onClick,
    label = { Text(label, fontSize = 12.sp) }
  )
}

@Composable
fun TaskItemCard(
  task: TaskEntity,
  onToggle: () -> Unit,
  onDelete: () -> Unit
) {
  val priorityColor = when (task.priority) {
    "Tinggi" -> ExpenseRed
    "Sedang" -> WarningAmber
    else -> SuccessGreen
  }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable { onToggle() }
      .testTag("task_item_${task.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (task.isCompleted) {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      } else {
        MaterialTheme.colorScheme.surface
      }
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (task.isCompleted) 0.dp else 2.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Checkbox(
        checked = task.isCompleted,
        onCheckedChange = { onToggle() },
        colors = CheckboxDefaults.colors(
          checkedColor = MaterialTheme.colorScheme.primary
        ),
        modifier = Modifier.testTag("task_checkbox_${task.id}")
      )

      Spacer(modifier = Modifier.width(8.dp))

      Column(modifier = Modifier.weight(1f)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            color = if (task.isCompleted) {
              MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            } else {
              MaterialTheme.colorScheme.onSurface
            },
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )

          // Priority indicator pill
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = priorityColor.copy(alpha = 0.15f),
            modifier = Modifier.padding(start = 6.dp)
          ) {
            Text(
              text = task.priority,
              color = priorityColor,
              style = MaterialTheme.typography.labelSmall,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        if (task.description.isNotBlank()) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = task.description,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Category tag
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.secondaryContainer
          ) {
            Text(
              text = task.category,
              color = MaterialTheme.colorScheme.onSecondaryContainer,
              style = MaterialTheme.typography.labelSmall,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          Text(
            text = DateTimeUtils.formatShortDate(task.date),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
          )
        }
      }

      IconButton(
        onClick = onDelete,
        modifier = Modifier.testTag("delete_task_${task.id}")
      ) {
        Icon(
          Icons.Default.DeleteOutline,
          contentDescription = "Hapus Tugas",
          tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
        )
      }
    }
  }
}

@Composable
fun AddTaskDialog(
  onDismiss: () -> Unit,
  onConfirm: (title: String, desc: String, category: String, priority: String, date: String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var desc by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Kerja") }
  var priority by remember { mutableStateOf("Sedang") }
  val date by remember { mutableStateOf(DateTimeUtils.getTodayDate()) }

  val categoryOptions = listOf("Kerja", "Belajar", "Ibadah", "Pribadi", "Kesehatan", "Keuangan")
  val priorityOptions = listOf("Tinggi", "Sedang", "Rendah")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Tambah Kegiatan Harian",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Nama Kegiatan") },
          placeholder = { Text("Contoh: Selesaikan laporan mingguan") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("task_title_input"),
          singleLine = true
        )

        OutlinedTextField(
          value = desc,
          onValueChange = { desc = it },
          label = { Text("Catatan / Keterangan (Opsional)") },
          modifier = Modifier.fillMaxWidth(),
          maxLines = 2
        )

        Text(
          text = "Kategori",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold
        )
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(categoryOptions) { cat ->
            FilterChip(
              selected = category == cat,
              onClick = { category = cat },
              label = { Text(cat, fontSize = 11.sp) }
            )
          }
        }

        Text(
          text = "Prioritas",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          priorityOptions.forEach { p ->
            FilterChip(
              selected = priority == p,
              onClick = { priority = p },
              label = { Text(p, fontSize = 11.sp) }
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (title.isNotBlank()) {
            onConfirm(title, desc, category, priority, date)
          }
        },
        enabled = title.isNotBlank(),
        modifier = Modifier.testTag("save_task_button")
      ) {
        Text("Simpan Kegiatan")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}

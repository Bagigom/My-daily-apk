package com.example.ui.screens.targets

import androidx.compose.foundation.background
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import com.example.ui.components.rememberGalleryPicker
import com.example.ui.components.rememberLocalBitmap
import com.example.ui.components.rememberUriBitmap
import com.example.ui.theme.Gradients
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyGoalEntity
import com.example.data.model.WishlistEntity
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.DailyFocusViewModel
import com.example.ui.viewmodel.TargetTab
import com.example.util.DateTimeUtils

@Composable
fun TargetsScreen(
  viewModel: DailyFocusViewModel,
  modifier: Modifier = Modifier
) {
  val targetTab by viewModel.targetTab.collectAsState()
  val dailyGoals by viewModel.dailyGoals.collectAsState()
  val wishlistGoals by viewModel.wishlistGoals.collectAsState()

  var showAddDailyGoalDialog by remember { mutableStateOf(false) }
  var showAddWishlistDialog by remember { mutableStateOf(false) }
  var selectedWishlistForSavings by remember { mutableStateOf<WishlistEntity?>(null) }
  var wishlistForImage by remember { mutableStateOf<WishlistEntity?>(null) }
  val pickWishlistImage = rememberGalleryPicker { uri ->
    wishlistForImage?.let { viewModel.setWishlistImage(it, uri) }
  }

  Box(modifier = modifier.fillMaxSize()) {
    Column(modifier = Modifier.fillMaxSize()) {
      // Segmented Tabs: Target Harian vs Target Keinginan
      TabRow(
        selectedTabIndex = if (targetTab == TargetTab.DAILY_HABITS) 0 else 1,
        containerColor = MaterialTheme.colorScheme.surface
      ) {
        Tab(
          selected = targetTab == TargetTab.DAILY_HABITS,
          onClick = { viewModel.setTargetTab(TargetTab.DAILY_HABITS) },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("🎯 Target Harian", fontWeight = FontWeight.Bold)
            }
          }
        )
        Tab(
          selected = targetTab == TargetTab.WISHLIST,
          onClick = { viewModel.setTargetTab(TargetTab.WISHLIST) },
          text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text("✨ Target Keinginan", fontWeight = FontWeight.Bold)
            }
          }
        )
      }

      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .testTag("targets_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        if (targetTab == TargetTab.DAILY_HABITS) {
          // Info banner for Daily Habits
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
              )
            ) {
              Row(
                modifier = Modifier.background(Gradients.Violet).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = "Target Kebiasaan Harian",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                  Text(
                    text = "Bisa dicustom jumlah, satuan (ml, menit, dll), serta kelipatan step sesuai kebutuhanmu.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                  )
                }
              }
            }
          }

          if (dailyGoals.isEmpty()) {
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text("Belum ada target harian", fontWeight = FontWeight.Bold)
                  Text("Tekan tombol + untuk membuat target harian kustom pertamamu.", style = MaterialTheme.typography.bodySmall)
                }
              }
            }
          } else {
            items(dailyGoals, key = { it.id }) { goal ->
              DailyGoalItemCard(
                goal = goal,
                onIncrement = { step -> viewModel.incrementDailyGoal(goal, step) },
                onReset = { viewModel.resetDailyGoal(goal) },
                onDelete = { viewModel.deleteDailyGoal(goal) }
              )
            }
          }
        } else {
          // Wishlist / Dream Target View
          item {
            Card(
              modifier = Modifier.fillMaxWidth(),
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
              )
            ) {
              Row(
                modifier = Modifier.background(Gradients.Sunset).padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Box(
                  modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.25f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                  )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                  Text(
                    text = "Target Keinginan (Wishlist)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                  Text(
                    text = "Pantau impian barang, gadget, ataupun liburan dengan mencicil tabungan.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                  )
                }
              }
            }
          }

          if (wishlistGoals.isEmpty()) {
            item {
              Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                  containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                )
              ) {
                Column(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text("Belum ada target impian", fontWeight = FontWeight.Bold)
                  Text("Tekan tombol + untuk menambahkan barang atau tujuan impianmu.", style = MaterialTheme.typography.bodySmall)
                }
              }
            }
          } else {
            items(wishlistGoals, key = { it.id }) { item ->
              WishlistItemCard(
                wishlist = item,
                onAddSavings = { selectedWishlistForSavings = item },
                onPickImage = {
                  wishlistForImage = item
                  pickWishlistImage()
                },
                onRemoveImage = { viewModel.removeWishlistImage(item) },
                onDelete = { viewModel.deleteWishlist(item) }
              )
            }
          }
        }
      }
    }

    // Floating Action Button
    FloatingActionButton(
      onClick = {
        if (targetTab == TargetTab.DAILY_HABITS) {
          showAddDailyGoalDialog = true
        } else {
          showAddWishlistDialog = true
        }
      },
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(bottom = 16.dp, end = 20.dp)
        .testTag("add_target_fab"),
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
      Icon(
        Icons.Default.Add,
        contentDescription = if (targetTab == TargetTab.DAILY_HABITS) "Buat Target Harian" else "Tambah Target Impian"
      )
    }

    // Dialog: Create Daily Goal
    if (showAddDailyGoalDialog) {
      AddDailyGoalDialog(
        onDismiss = { showAddDailyGoalDialog = false },
        onConfirm = { title, emoji, targetVal, unit, step ->
          viewModel.addDailyGoal(title, emoji, targetVal, unit, step)
          showAddDailyGoalDialog = false
        }
      )
    }

    // Dialog: Create Wishlist
    if (showAddWishlistDialog) {
      AddWishlistDialog(
        onDismiss = { showAddWishlistDialog = false },
        onConfirm = { title, targetAmt, savedAmt, cat, date, notes, imageUri ->
          viewModel.addWishlist(title, targetAmt, savedAmt, cat, date, notes, imageUri)
          showAddWishlistDialog = false
        }
      )
    }

    // Dialog: Add Savings to Wishlist
    selectedWishlistForSavings?.let { wishlist ->
      AddSavingsDialog(
        wishlist = wishlist,
        onDismiss = { selectedWishlistForSavings = null },
        onConfirm = { amountToAdd ->
          viewModel.addWishlistSavings(wishlist, amountToAdd)
          selectedWishlistForSavings = null
        }
      )
    }
  }
}

@Composable
fun DailyGoalItemCard(
  goal: DailyGoalEntity,
  onIncrement: (step: Int) -> Unit,
  onReset: () -> Unit,
  onDelete: () -> Unit
) {
  val progress = if (goal.targetValue > 0) {
    (goal.currentValue.toFloat() / goal.targetValue).coerceIn(0f, 1f)
  } else 0f

  val isCompleted = goal.isCompleted || goal.currentValue >= goal.targetValue

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("daily_goal_${goal.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
      else MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = if (isCompleted) 0.dp else 2.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
          // Emoji avatar
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Text(text = goal.iconEmoji, fontSize = 20.sp)
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = goal.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              if (isCompleted) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                  Icons.Default.CheckCircle,
                  contentDescription = "Tercapai",
                  tint = SuccessGreen,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            // Streak Pill
            if (goal.streak > 0) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.LocalFireDepartment,
                  contentDescription = null,
                  tint = WarningAmber,
                  modifier = Modifier.size(14.dp)
                )
                Text(
                  text = "${goal.streak} hari streak",
                  style = MaterialTheme.typography.labelSmall,
                  color = WarningAmber,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }
        }

        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
          Icon(
            Icons.Default.DeleteOutline,
            contentDescription = "Hapus",
            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Progress bar and numbers
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "${goal.currentValue} / ${goal.targetValue} ${goal.unit}",
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = "${(progress * 100).toInt()}%",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold,
          color = if (isCompleted) SuccessGreen else MaterialTheme.colorScheme.primary
        )
      }

      Spacer(modifier = Modifier.height(6.dp))

      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(8.dp)
          .clip(RoundedCornerShape(4.dp)),
        color = if (isCompleted) SuccessGreen else MaterialTheme.colorScheme.primary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
      )

      Spacer(modifier = Modifier.height(12.dp))

      // Quick Action Buttons (+step, -step)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (goal.currentValue > 0) {
          OutlinedButton(
            onClick = { onIncrement(-goal.stepIncrement) },
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.height(34.dp)
          ) {
            Text("-${goal.stepIncrement} ${goal.unit}", fontSize = 12.sp)
          }
          Spacer(modifier = Modifier.width(8.dp))
        }

        Button(
          onClick = { onIncrement(goal.stepIncrement) },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (isCompleted) SuccessGreen else MaterialTheme.colorScheme.primary
          ),
          contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
          modifier = Modifier.height(34.dp)
        ) {
          Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("+${goal.stepIncrement} ${goal.unit}", fontSize = 12.sp)
        }
      }
    }
  }
}

@Composable
fun WishlistItemCard(
  wishlist: WishlistEntity,
  onAddSavings: () -> Unit,
  onPickImage: () -> Unit,
  onRemoveImage: () -> Unit,
  onDelete: () -> Unit
) {
  val progress = if (wishlist.targetAmount > 0) {
    (wishlist.savedAmount / wishlist.targetAmount).toFloat().coerceIn(0f, 1f)
  } else 0f

  val isAchieved = wishlist.isAchieved || wishlist.savedAmount >= wishlist.targetAmount

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .testTag("wishlist_item_${wishlist.id}"),
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(
      containerColor = MaterialTheme.colorScheme.surface
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Column {
    WishlistImageBanner(
      path = wishlist.imagePath,
      onPick = onPickImage,
      onRemove = onRemoveImage
    )
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.tertiaryContainer
          ) {
            Text(
              text = wishlist.category,
              color = MaterialTheme.colorScheme.onTertiaryContainer,
              style = MaterialTheme.typography.labelSmall,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = wishlist.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
          )
        }

        IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
          Icon(
            Icons.Default.DeleteOutline,
            contentDescription = "Hapus Target Impian",
            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
            modifier = Modifier.size(18.dp)
          )
        }
      }

      if (wishlist.notes.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = wishlist.notes,
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Amount details
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Column {
          Text(
            text = "Terkumpul",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = DateTimeUtils.formatRupiah(wishlist.savedAmount),
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = IncomeGreen
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "Target Impian",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Text(
            text = DateTimeUtils.formatRupiah(wishlist.targetAmount),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      LinearProgressIndicator(
        progress = { progress },
        modifier = Modifier
          .fillMaxWidth()
          .height(10.dp)
          .clip(RoundedCornerShape(5.dp)),
        color = if (isAchieved) SuccessGreen else MaterialTheme.colorScheme.tertiary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (wishlist.targetDate.isNotBlank()) {
          Text(
            text = "Target: ${DateTimeUtils.formatShortDate(wishlist.targetDate)}",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        } else {
          Spacer(modifier = Modifier.width(1.dp))
        }

        if (isAchieved) {
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = SuccessGreen.copy(alpha = 0.15f)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Tercapai 🎉", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
          }
        } else {
          Button(
            onClick = onAddSavings,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
            modifier = Modifier.height(36.dp)
          ) {
            Icon(Icons.Default.Savings, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Tambah Tabungan", fontSize = 12.sp)
          }
        }
      }
    }
    }
  }
}

@Composable
fun WishlistImageBanner(
  path: String,
  onPick: () -> Unit,
  onRemove: () -> Unit
) {
  if (path.isBlank()) {
    TextButton(
      onClick = onPick,
      modifier = Modifier.padding(start = 8.dp, top = 4.dp)
    ) {
      Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text("Tambah foto dari galeri", fontSize = 12.sp)
    }
  } else {
    val bitmap by rememberLocalBitmap(path, 1024)
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(160.dp)
        .clickable { onPick() }
    ) {
      val bmp = bitmap
      if (bmp != null) {
        Image(
          bitmap = bmp,
          contentDescription = "Foto target impian",
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
      } else {
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
        )
      }
      Box(
        modifier = Modifier
          .align(Alignment.BottomCenter)
          .fillMaxWidth()
          .height(56.dp)
          .background(
            Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.45f)))
          )
      )
      IconButton(
        onClick = onRemove,
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(6.dp)
          .size(32.dp)
          .clip(CircleShape)
          .background(Color.Black.copy(alpha = 0.45f))
      ) {
        Icon(
          Icons.Default.Close,
          contentDescription = "Hapus foto",
          tint = Color.White,
          modifier = Modifier.size(16.dp)
        )
      }
      Text(
        text = "Ketuk untuk ganti foto",
        color = Color.White,
        fontSize = 11.sp,
        modifier = Modifier
          .align(Alignment.BottomStart)
          .padding(12.dp)
      )
    }
  }
}

@Composable
fun AddDailyGoalDialog(
  onDismiss: () -> Unit,
  onConfirm: (title: String, emoji: String, targetVal: Int, unit: String, step: Int) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var emoji by remember { mutableStateOf("💧") }
  var targetValStr by remember { mutableStateOf("2000") }
  var unit by remember { mutableStateOf("ml") }
  var stepStr by remember { mutableStateOf("250") }

  val emojiOptions = listOf("💧", "📖", "🏃", "🧘", "💻", "🍎", "⚡", "🎯", "⭐", "🚴")
  val unitPresets = listOf("ml", "menit", "halaman", "kali", "km", "jam")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text("Buat Target Harian Kustom", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Nama Target Harian") },
          placeholder = { Text("Contoh: Minum Air Putih / Baca Buku") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Text("Pilih Ikon Emoji", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(emojiOptions) { em ->
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (emoji == em) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant)
                .clickable { emoji = em },
              contentAlignment = Alignment.Center
            ) {
              Text(em, fontSize = 20.sp)
            }
          }
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          OutlinedTextField(
            value = targetValStr,
            onValueChange = { targetValStr = it.filter { ch -> ch.isDigit() } },
            label = { Text("Target Angka") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.weight(1f),
            singleLine = true
          )

          OutlinedTextField(
            value = unit,
            onValueChange = { unit = it },
            label = { Text("Satuan") },
            placeholder = { Text("ml/menit") },
            modifier = Modifier.weight(1f),
            singleLine = true
          )
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(unitPresets) { u ->
            FilterChip(
              selected = unit == u,
              onClick = { unit = u },
              label = { Text(u, fontSize = 11.sp) }
            )
          }
        }

        OutlinedTextField(
          value = stepStr,
          onValueChange = { stepStr = it.filter { ch -> ch.isDigit() } },
          label = { Text("Kelipatan Penambahan (+step)") },
          placeholder = { Text("Misal: 250 untuk +250ml") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
      }
    },
    confirmButton = {
      val parsedTarget = targetValStr.toIntOrNull() ?: 1
      val parsedStep = stepStr.toIntOrNull() ?: 1
      Button(
        onClick = {
          if (title.isNotBlank()) {
            onConfirm(title, emoji, parsedTarget, unit, parsedStep)
          }
        },
        enabled = title.isNotBlank() && parsedTarget > 0
      ) {
        Text("Simpan Target Harian")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}

@Composable
fun AddWishlistDialog(
  onDismiss: () -> Unit,
  onConfirm: (title: String, targetAmount: Double, savedAmount: Double, category: String, targetDate: String, notes: String, imageUri: Uri?) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var targetAmountStr by remember { mutableStateOf("") }
  var savedAmountStr by remember { mutableStateOf("0") }
  var category by remember { mutableStateOf("Elektronik") }
  var targetDate by remember { mutableStateOf("2026-12-31") }
  var notes by remember { mutableStateOf("") }
  var imageUri by remember { mutableStateOf<Uri?>(null) }
  val openGallery = rememberGalleryPicker { imageUri = it }
  val previewBitmap by rememberUriBitmap(imageUri, 256)

  val categories = listOf("Elektronik", "Travel", "Kendaraan", "Gaya Hidup", "Edukasi", "Lainnya")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text("Tambah Target Impian (Wishlist)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Nama Barang / Impian") },
          placeholder = { Text("Contoh: Laptop M3 / Liburan Bali") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        OutlinedTextField(
          value = targetAmountStr,
          onValueChange = { targetAmountStr = it.filter { ch -> ch.isDigit() } },
          label = { Text("Target Biaya (Rp)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        OutlinedTextField(
          value = savedAmountStr,
          onValueChange = { savedAmountStr = it.filter { ch -> ch.isDigit() } },
          label = { Text("Tabungan Awal (Rp)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Text("Kategori", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(categories) { cat ->
            FilterChip(
              selected = category == cat,
              onClick = { category = cat },
              label = { Text(cat, fontSize = 11.sp) }
            )
          }
        }

        OutlinedTextField(
          value = targetDate,
          onValueChange = { targetDate = it },
          label = { Text("Estimasi Tanggal (yyyy-MM-dd)") },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Catatan Rencana (Opsional)") },
          modifier = Modifier.fillMaxWidth(),
          maxLines = 2
        )

        Text("Foto Impian (Opsional)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
              .size(72.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(MaterialTheme.colorScheme.surfaceVariant)
              .clickable { openGallery() }
          ) {
            val pb = previewBitmap
            if (pb != null) {
              Image(
                bitmap = pb,
                contentDescription = "Foto terpilih",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
              )
            } else {
              Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
          }
          Spacer(modifier = Modifier.width(12.dp))
          OutlinedButton(onClick = openGallery) {
            Text(if (imageUri == null) "Pilih dari Galeri" else "Ganti Foto", fontSize = 12.sp)
          }
        }
      }
    },
    confirmButton = {
      val targetAmt = targetAmountStr.toDoubleOrNull() ?: 0.0
      val savedAmt = savedAmountStr.toDoubleOrNull() ?: 0.0
      Button(
        onClick = {
          if (title.isNotBlank() && targetAmt > 0) {
            onConfirm(title, targetAmt, savedAmt, category, targetDate, notes, imageUri)
          }
        },
        enabled = title.isNotBlank() && targetAmt > 0
      ) {
        Text("Simpan Impian")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}

@Composable
fun AddSavingsDialog(
  wishlist: WishlistEntity,
  onDismiss: () -> Unit,
  onConfirm: (amount: Double) -> Unit
) {
  var amountStr by remember { mutableStateOf("") }
  val quickAmounts = listOf(50000, 100000, 250000, 500000, 1000000)

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text("Tambah Tabungan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        Text(
          text = "Target: ${wishlist.title}",
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = "Terkumpul saat ini: ${DateTimeUtils.formatRupiah(wishlist.savedAmount)} dari ${DateTimeUtils.formatRupiah(wishlist.targetAmount)}",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
          value = amountStr,
          onValueChange = { amountStr = it.filter { ch -> ch.isDigit() } },
          label = { Text("Jumlah Ditabung (Rp)") },
          placeholder = { Text("100000") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        Text("Pilihan Cepat:", style = MaterialTheme.typography.labelSmall)
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(quickAmounts) { amt ->
            FilterChip(
              selected = amountStr == amt.toString(),
              onClick = { amountStr = amt.toString() },
              label = { Text(DateTimeUtils.formatRupiah(amt.toDouble()), fontSize = 11.sp) }
            )
          }
        }
      }
    },
    confirmButton = {
      val parsedAmount = amountStr.toDoubleOrNull() ?: 0.0
      Button(
        onClick = {
          if (parsedAmount > 0) {
            onConfirm(parsedAmount)
          }
        },
        enabled = parsedAmount > 0
      ) {
        Text("Tabung Sekarang")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}

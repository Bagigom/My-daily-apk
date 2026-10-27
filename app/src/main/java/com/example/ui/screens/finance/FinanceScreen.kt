package com.example.ui.screens.finance

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.WarningAmber
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.Gradients
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.DailyFocusViewModel
import com.example.ui.viewmodel.FinanceFilter
import com.example.util.DateTimeUtils

@Composable
fun FinanceScreen(
  viewModel: DailyFocusViewModel,
  modifier: Modifier = Modifier
) {
  val allTransactions by viewModel.allTransactions.collectAsState()
  val filteredTransactions by viewModel.filteredTransactions.collectAsState()
  val activeFilter by viewModel.financeFilter.collectAsState()
  val financeTarget by viewModel.financeTarget.collectAsState()

  var showAddTxDialog by remember { mutableStateOf(false) }
  var showTargetDialog by remember { mutableStateOf(false) }

  val currentMonth = DateTimeUtils.getCurrentMonthPrefix()
  val thisMonthTransactions = allTransactions.filter { it.date.startsWith(currentMonth) }

  val totalIncomeAll = allTransactions.filter { it.type == "INCOME" }.sumOf { it.amount }
  val totalExpenseAll = allTransactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
  val currentBalance = totalIncomeAll - totalExpenseAll

  val thisMonthIncome = thisMonthTransactions.filter { it.type == "INCOME" }.sumOf { it.amount }
  val thisMonthExpense = thisMonthTransactions.filter { it.type == "EXPENSE" }.sumOf { it.amount }
  val thisMonthNetSavings = thisMonthIncome - thisMonthExpense

  val budgetLimit = financeTarget?.monthlyBudgetLimit ?: 3500000.0
  val savingsTarget = financeTarget?.monthlySavingsTarget ?: 2000000.0

  val expenseBudgetRatio = if (budgetLimit > 0) (thisMonthExpense / budgetLimit).toFloat().coerceIn(0f, 1.5f) else 0f
  val savingsProgressRatio = if (savingsTarget > 0) (thisMonthNetSavings / savingsTarget).toFloat().coerceIn(0f, 1f) else 0f

  Box(modifier = modifier.fillMaxSize()) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("finance_screen"),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Balance & Cashflow Card
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
          )
        ) {
          Column(modifier = Modifier.background(Gradients.Ocean).padding(20.dp)) {
            Text(
              text = "Total Saldo Kas",
              style = MaterialTheme.typography.bodyMedium,
              color = Color.White.copy(alpha = 0.85f)
            )
            Text(
              text = DateTimeUtils.formatRupiah(currentBalance),
              fontSize = 30.sp,
              fontWeight = FontWeight.ExtraBold,
              color = Color.White,
              modifier = Modifier.testTag("total_balance_text")
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Month Income & Expense Summary
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Color.White.copy(alpha = 0.16f))
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              // Income
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(IncomeGreen),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "Pemasukan",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f)
                  )
                  Text(
                    text = DateTimeUtils.formatRupiah(thisMonthIncome),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                }
              }

              // Expense
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(ExpenseRed),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "Pengeluaran",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.85f)
                  )
                  Text(
                    text = DateTimeUtils.formatRupiah(thisMonthExpense),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                  )
                }
              }
            }
          }
        }
      }

      // Financial Target Section Card (Target Keuangan: Batas Anggaran & Tabungan)
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
          )
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.Savings,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Target Keuangan Bulan Ini",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold
                )
              }

              IconButton(
                onClick = { showTargetDialog = true },
                modifier = Modifier.size(32.dp)
              ) {
                Icon(
                  Icons.Default.Edit,
                  contentDescription = "Atur Target",
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Budget Limit Progress
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Batas Pengeluaran",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "${DateTimeUtils.formatRupiah(thisMonthExpense)} / ${DateTimeUtils.formatRupiah(budgetLimit)}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
              progress = { expenseBudgetRatio.coerceIn(0f, 1f) },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = if (expenseBudgetRatio > 0.9f) ExpenseRed else MaterialTheme.colorScheme.primary,
              trackColor = MaterialTheme.colorScheme.surface
            )

            if (expenseBudgetRatio > 0.9f) {
              Spacer(modifier = Modifier.height(4.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  Icons.Default.WarningAmber,
                  contentDescription = null,
                  tint = ExpenseRed,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = if (expenseBudgetRatio >= 1f) "Pengeluaran sudah melebihi batas anggaran!" else "Perhatian: Pengeluaran mendekati batas!",
                  style = MaterialTheme.typography.labelSmall,
                  color = ExpenseRed
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Savings Target Progress
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Target Tabungan",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "${DateTimeUtils.formatRupiah(thisMonthNetSavings.coerceAtLeast(0.0))} / ${DateTimeUtils.formatRupiah(savingsTarget)}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = IncomeGreen
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
              progress = { savingsProgressRatio },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = IncomeGreen,
              trackColor = MaterialTheme.colorScheme.surface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Tercapai ${(savingsProgressRatio * 100).toInt()}% dari target tabungan",
              style = MaterialTheme.typography.labelSmall,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
      }

      // Filter Chips
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          FilterChip(
            selected = activeFilter == FinanceFilter.ALL,
            onClick = { viewModel.setFinanceFilter(FinanceFilter.ALL) },
            label = { Text("Semua", fontSize = 12.sp) }
          )
          FilterChip(
            selected = activeFilter == FinanceFilter.THIS_MONTH,
            onClick = { viewModel.setFinanceFilter(FinanceFilter.THIS_MONTH) },
            label = { Text("Bulan Ini", fontSize = 12.sp) }
          )
          FilterChip(
            selected = activeFilter == FinanceFilter.INCOME_ONLY,
            onClick = { viewModel.setFinanceFilter(FinanceFilter.INCOME_ONLY) },
            label = { Text("Pemasukan", fontSize = 12.sp) }
          )
          FilterChip(
            selected = activeFilter == FinanceFilter.EXPENSE_ONLY,
            onClick = { viewModel.setFinanceFilter(FinanceFilter.EXPENSE_ONLY) },
            label = { Text("Pengeluaran", fontSize = 12.sp) }
          )
        }
      }

      // Transactions List Header
      item {
        Text(
          text = "Riwayat Catatan Keuangan",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
      }

      if (filteredTransactions.isEmpty()) {
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
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "Belum ada catatan transaksi",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            }
          }
        }
      } else {
        items(filteredTransactions, key = { it.id }) { tx ->
          TransactionItemCard(
            transaction = tx,
            onDelete = { viewModel.deleteTransaction(tx) }
          )
        }
      }
    }

    // Floating Action Button to Add Transaction
    FloatingActionButton(
      onClick = { showAddTxDialog = true },
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(bottom = 16.dp, end = 20.dp)
        .testTag("add_transaction_fab"),
      containerColor = MaterialTheme.colorScheme.primary,
      contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
      Icon(Icons.Default.Add, contentDescription = "Catat Transaksi")
    }

    // Add Transaction Dialog
    if (showAddTxDialog) {
      AddTransactionDialog(
        onDismiss = { showAddTxDialog = false },
        onConfirm = { title, amount, type, category, date, notes ->
          viewModel.addTransaction(title, amount, type, category, date, notes)
          showAddTxDialog = false
        }
      )
    }

    // Edit Target Dialog
    if (showTargetDialog) {
      EditFinanceTargetDialog(
        currentBudget = budgetLimit,
        currentSavings = savingsTarget,
        onDismiss = { showTargetDialog = false },
        onConfirm = { newBudget, newSavings ->
          viewModel.updateFinanceTarget(newBudget, newSavings)
          showTargetDialog = false
        }
      )
    }
  }
}

@Composable
fun TransactionItemCard(
  transaction: TransactionEntity,
  onDelete: () -> Unit
) {
  val isIncome = transaction.type == "INCOME"

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
          .size(40.dp)
          .clip(CircleShape)
          .background(
            if (isIncome) IncomeGreen.copy(alpha = 0.15f)
            else ExpenseRed.copy(alpha = 0.15f)
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = if (isIncome) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
          contentDescription = null,
          tint = if (isIncome) IncomeGreen else ExpenseRed,
          modifier = Modifier.size(20.dp)
        )
      }

      Spacer(modifier = Modifier.width(12.dp))

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = transaction.title,
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Row(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant
          ) {
            Text(
              text = transaction.category,
              style = MaterialTheme.typography.labelSmall,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          Text(
            text = DateTimeUtils.formatShortDate(transaction.date),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = (if (isIncome) "+" else "-") + DateTimeUtils.formatRupiah(transaction.amount),
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold,
          color = if (isIncome) IncomeGreen else ExpenseRed
        )
      }

      IconButton(
        onClick = onDelete,
        modifier = Modifier.size(36.dp)
      ) {
        Icon(
          Icons.Default.DeleteOutline,
          contentDescription = "Hapus Transaksi",
          tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f),
          modifier = Modifier.size(18.dp)
        )
      }
    }
  }
}

@Composable
fun AddTransactionDialog(
  onDismiss: () -> Unit,
  onConfirm: (title: String, amount: Double, type: String, category: String, date: String, notes: String) -> Unit
) {
  var type by remember { mutableStateOf("EXPENSE") }
  var title by remember { mutableStateOf("") }
  var amountStr by remember { mutableStateOf("") }
  var category by remember { mutableStateOf("Makanan") }
  val date by remember { mutableStateOf(DateTimeUtils.getTodayDate()) }
  var notes by remember { mutableStateOf("") }

  val expenseCategories = listOf("Makanan", "Transport", "Belanja", "Tagihan", "Hiburan", "Kesehatan", "Lainnya")
  val incomeCategories = listOf("Gaji", "Bonus", "Bisnis", "Investasi", "Hadiah", "Lainnya")

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = if (type == "EXPENSE") "Catat Pengeluaran" else "Catat Pemasukan",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Toggle Income vs Expense
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.Center
        ) {
          FilterChip(
            selected = type == "EXPENSE",
            onClick = {
              type = "EXPENSE"
              category = "Makanan"
            },
            label = { Text("Pengeluaran") },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = ExpenseRed.copy(alpha = 0.2f),
              selectedLabelColor = ExpenseRed
            )
          )
          Spacer(modifier = Modifier.width(8.dp))
          FilterChip(
            selected = type == "INCOME",
            onClick = {
              type = "INCOME"
              category = "Gaji"
            },
            label = { Text("Pemasukan") },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = IncomeGreen.copy(alpha = 0.2f),
              selectedLabelColor = IncomeGreen
            )
          )
        }

        OutlinedTextField(
          value = title,
          onValueChange = { title = it },
          label = { Text("Keterangan Singkat") },
          placeholder = { Text(if (type == "EXPENSE") "Misal: Makan Siang" else "Misal: Gaji Bulanan") },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("tx_title_input"),
          singleLine = true
        )

        OutlinedTextField(
          value = amountStr,
          onValueChange = { amountStr = it.filter { ch -> ch.isDigit() } },
          label = { Text("Jumlah (Rp)") },
          placeholder = { Text("50000") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("tx_amount_input"),
          singleLine = true
        )

        Text(
          text = "Kategori",
          style = MaterialTheme.typography.labelMedium,
          fontWeight = FontWeight.Bold
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          items(if (type == "EXPENSE") expenseCategories else incomeCategories) { cat ->
            FilterChip(
              selected = category == cat,
              onClick = { category = cat },
              label = { Text(cat, fontSize = 11.sp) }
            )
          }
        }

        OutlinedTextField(
          value = notes,
          onValueChange = { notes = it },
          label = { Text("Catatan Tambahan (Opsional)") },
          modifier = Modifier.fillMaxWidth(),
          maxLines = 2
        )
      }
    },
    confirmButton = {
      val parsedAmount = amountStr.toDoubleOrNull() ?: 0.0
      Button(
        onClick = {
          if (title.isNotBlank() && parsedAmount > 0) {
            onConfirm(title, parsedAmount, type, category, date, notes)
          }
        },
        enabled = title.isNotBlank() && parsedAmount > 0,
        modifier = Modifier.testTag("save_tx_button")
      ) {
        Text("Simpan Transaksi")
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
fun EditFinanceTargetDialog(
  currentBudget: Double,
  currentSavings: Double,
  onDismiss: () -> Unit,
  onConfirm: (budgetLimit: Double, savingsTarget: Double) -> Unit
) {
  var budgetStr by remember { mutableStateOf(currentBudget.toLong().toString()) }
  var savingsStr by remember { mutableStateOf(currentSavings.toLong().toString()) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Text(
        text = "Atur Target Keuangan Bulanan",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
      )
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "Sesuaikan batas maksimal pengeluaran dan target tabungan untuk bulan ini.",
          style = MaterialTheme.typography.bodySmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
          value = budgetStr,
          onValueChange = { budgetStr = it.filter { ch -> ch.isDigit() } },
          label = { Text("Batas Pengeluaran Bulanan (Rp)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )

        OutlinedTextField(
          value = savingsStr,
          onValueChange = { savingsStr = it.filter { ch -> ch.isDigit() } },
          label = { Text("Target Tabungan Bulanan (Rp)") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          modifier = Modifier.fillMaxWidth(),
          singleLine = true
        )
      }
    },
    confirmButton = {
      val budget = budgetStr.toDoubleOrNull() ?: currentBudget
      val savings = savingsStr.toDoubleOrNull() ?: currentSavings
      Button(
        onClick = { onConfirm(budget, savings) }
      ) {
        Text("Simpan Target")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Batal")
      }
    }
  )
}

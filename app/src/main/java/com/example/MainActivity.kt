package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Checklist
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.settings.AppSettings
import com.example.data.settings.BackgroundType
import com.example.data.settings.ThemeMode
import com.example.ui.components.AppBackground
import com.example.ui.components.AppearanceSheet
import com.example.ui.screens.finance.FinanceScreen
import com.example.ui.screens.targets.TargetsScreen
import com.example.ui.screens.tasks.TasksScreen
import com.example.ui.screens.timer.TimerScreen
import com.example.ui.theme.Gradients
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.DailyFocusViewModel
import com.example.ui.viewmodel.MainTab
import com.example.util.DateTimeUtils

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      DailyFocusApp()
    }
  }
}

@Composable
fun DailyFocusApp(
  viewModel: DailyFocusViewModel = viewModel()
) {
  val settings by viewModel.settings.collectAsState()
  val systemDark = isSystemInDarkTheme()
  val darkTheme = when (settings.themeMode) {
    ThemeMode.SYSTEM -> systemDark
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
  }

  // Ikon status bar & navigasi menyesuaikan tema pilihan (bukan hanya tema sistem),
  // dan dianggap "gelap" bila foto latar sudah digelapkan.
  val activity = LocalContext.current as ComponentActivity
  val barsOnDark = darkTheme ||
    (settings.backgroundType == BackgroundType.IMAGE && settings.dim >= 0.3f)
  SideEffect {
    activity.enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.auto(
        AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT, detectDarkMode = { barsOnDark }
      ),
      navigationBarStyle = SystemBarStyle.auto(
        AndroidColor.TRANSPARENT, AndroidColor.TRANSPARENT, detectDarkMode = { barsOnDark }
      )
    )
  }

  MyApplicationTheme(darkTheme = darkTheme) {
    AppShell(viewModel = viewModel, settings = settings, darkTheme = darkTheme)
  }
}

@Composable
private fun AppShell(
  viewModel: DailyFocusViewModel,
  settings: AppSettings,
  darkTheme: Boolean
) {
  val currentTab by viewModel.currentTab.collectAsState()
  var showAppearance by remember { mutableStateOf(false) }

  Box(modifier = Modifier.fillMaxSize()) {
    AppBackground(settings = settings, darkTheme = darkTheme)

    Scaffold(
      modifier = Modifier.fillMaxSize(),
      containerColor = Color.Transparent,
      contentWindowInsets = WindowInsets.safeDrawing,
      topBar = {
        HeaderBar(
          tab = currentTab,
          onOpenAppearance = { showAppearance = true }
        )
      },
      bottomBar = {
        FloatingNavBar(
          currentTab = currentTab,
          onSelect = { viewModel.setTab(it) }
        )
      }
    ) { paddingValues ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(paddingValues)
      ) {
        Crossfade(targetState = currentTab, label = "tabTransition") { tab ->
          when (tab) {
            MainTab.TASKS -> TasksScreen(viewModel = viewModel)
            MainTab.TIMER -> TimerScreen(viewModel = viewModel)
            MainTab.FINANCE -> FinanceScreen(viewModel = viewModel)
            MainTab.TARGETS -> TargetsScreen(viewModel = viewModel)
          }
        }
      }
    }

    if (showAppearance) {
      AppearanceSheet(
        settings = settings,
        darkTheme = darkTheme,
        onDismiss = { showAppearance = false },
        onThemeModeChange = { viewModel.setThemeMode(it) },
        onPresetSelected = { viewModel.selectBackgroundPreset(it) },
        onImagePicked = { viewModel.setBackgroundImage(it) },
        onClearImage = { viewModel.clearBackgroundImage() },
        onDimChange = { viewModel.setBackgroundDim(it) }
      )
    }
  }
}

@Composable
private fun HeaderBar(
  tab: MainTab,
  onOpenAppearance: () -> Unit
) {
  val title = when (tab) {
    MainTab.TASKS -> "Tugas Harian"
    MainTab.TIMER -> "Timer & Fokus"
    MainTab.FINANCE -> "Keuangan"
    MainTab.TARGETS -> "Target & Impian"
  }

  Surface(
    modifier = Modifier
      .statusBarsPadding()
      .padding(horizontal = 16.dp, vertical = 8.dp)
      .fillMaxWidth(),
    shape = RoundedCornerShape(26.dp),
    color = MaterialTheme.colorScheme.surface,
    shadowElevation = 6.dp
  ) {
    Row(
      modifier = Modifier.padding(start = 18.dp, end = 10.dp, top = 12.dp, bottom = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = DateTimeUtils.formatDisplayDate(DateTimeUtils.getTodayDate()),
          style = MaterialTheme.typography.labelSmall,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
          text = title,
          style = MaterialTheme.typography.headlineSmall,
          color = MaterialTheme.colorScheme.onSurface
        )
      }
      Box(
        modifier = Modifier
          .size(44.dp)
          .clip(CircleShape)
          .background(Gradients.Violet)
      ) {
        IconButton(
          onClick = onOpenAppearance,
          modifier = Modifier
            .fillMaxSize()
            .testTag("open_appearance_button")
        ) {
          Icon(
            imageVector = Icons.Filled.Palette,
            contentDescription = "Ubah tampilan & latar belakang",
            tint = Color.White
          )
        }
      }
    }
  }
}

@Composable
private fun FloatingNavBar(
  currentTab: MainTab,
  onSelect: (MainTab) -> Unit
) {
  Box(
    modifier = Modifier
      .navigationBarsPadding()
      .padding(horizontal = 16.dp, vertical = 8.dp)
  ) {
    Surface(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(30.dp),
      color = MaterialTheme.colorScheme.surface,
      shadowElevation = 10.dp
    ) {
      NavigationBar(
        modifier = Modifier.testTag("bottom_nav_bar"),
        containerColor = Color.Transparent,
        tonalElevation = 0.dp,
        windowInsets = WindowInsets(0, 0, 0, 0)
      ) {
        NavItem("Tugas", Icons.Filled.Checklist, Icons.Outlined.Checklist,
          currentTab == MainTab.TASKS, "nav_tab_tasks") { onSelect(MainTab.TASKS) }
        NavItem("Timer", Icons.Filled.Timer, Icons.Outlined.Timer,
          currentTab == MainTab.TIMER, "nav_tab_timer") { onSelect(MainTab.TIMER) }
        NavItem("Keuangan", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet,
          currentTab == MainTab.FINANCE, "nav_tab_finance") { onSelect(MainTab.FINANCE) }
        NavItem("Target", Icons.Filled.Flag, Icons.Outlined.Flag,
          currentTab == MainTab.TARGETS, "nav_tab_targets") { onSelect(MainTab.TARGETS) }
      }
    }
  }
}

@Composable
private fun RowScope.NavItem(
  label: String,
  selectedIcon: ImageVector,
  unselectedIcon: ImageVector,
  selected: Boolean,
  tag: String,
  onClick: () -> Unit
) {
  NavigationBarItem(
    selected = selected,
    onClick = onClick,
    icon = {
      Icon(
        imageVector = if (selected) selectedIcon else unselectedIcon,
        contentDescription = label,
        modifier = Modifier.size(24.dp)
      )
    },
    label = {
      Text(
        text = label,
        fontSize = 12.sp,
        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
      )
    },
    colors = NavigationBarItemDefaults.colors(
      indicatorColor = MaterialTheme.colorScheme.primaryContainer,
      selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
      selectedTextColor = MaterialTheme.colorScheme.primary
    ),
    modifier = Modifier.testTag(tag)
  )
}

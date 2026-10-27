package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.DailyGoalEntity
import com.example.data.model.FinanceTargetEntity
import com.example.data.model.TaskEntity
import com.example.data.model.TimerSessionEntity
import com.example.data.model.TransactionEntity
import com.example.data.model.WishlistEntity
import com.example.util.DateTimeUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
  entities = [
    TaskEntity::class,
    TimerSessionEntity::class,
    TransactionEntity::class,
    FinanceTargetEntity::class,
    DailyGoalEntity::class,
    WishlistEntity::class
  ],
  version = 2,
  exportSchema = false
)
abstract class DailyFocusDatabase : RoomDatabase() {

  abstract fun dailyFocusDao(): DailyFocusDao

  companion object {
    @Volatile
    private var INSTANCE: DailyFocusDatabase? = null

    // v1 -> v2: tambah kolom foto pada target impian tanpa menghapus data pengguna.
    private val MIGRATION_1_2 = object : Migration(1, 2) {
      override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE wishlist_goals ADD COLUMN imagePath TEXT NOT NULL DEFAULT ''")
      }
    }

    fun getDatabase(context: Context, scope: CoroutineScope): DailyFocusDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          DailyFocusDatabase::class.java,
          "daily_focus_db"
        )
        .addMigrations(MIGRATION_1_2)
        .addCallback(DatabaseCallback(scope))
        .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialData(database.dailyFocusDao())
          }
        }
      }

      suspend fun populateInitialData(dao: DailyFocusDao) {
        val today = DateTimeUtils.getTodayDate()
        val currentMonth = DateTimeUtils.getCurrentMonthPrefix()

        // 1. Initial Tasks
        dao.insertTask(
          TaskEntity(
            title = "Review target kerja mingguan",
            description = "Periksa checklist proyek dan evaluasi pencapaian",
            category = "Kerja",
            priority = "Tinggi",
            date = today,
            isCompleted = true,
            completedAt = System.currentTimeMillis() - 3600000
          )
        )
        dao.insertTask(
          TaskEntity(
            title = "Belajar Android Jetpack Compose 1 jam",
            description = "Fokus pada materi state management & Room",
            category = "Belajar",
            priority = "Tinggi",
            date = today,
            isCompleted = false
          )
        )
        dao.insertTask(
          TaskEntity(
            title = "Jogging sore 30 menit",
            description = "Keliling taman kompleks untuk kebugaran",
            category = "Kesehatan",
            priority = "Sedang",
            date = today,
            isCompleted = false
          )
        )
        dao.insertTask(
          TaskEntity(
            title = "Catat pengeluaran & cek sisa saldo",
            description = "Rutin input struk belanja hari ini",
            category = "Keuangan",
            priority = "Sedang",
            date = today,
            isCompleted = false
          )
        )

        // 2. Initial Timer Sessions (History)
        val now = System.currentTimeMillis()
        dao.insertTimerSession(
          TimerSessionEntity(
            activityTitle = "Sesi Coding Proyek",
            category = "Kerja",
            startTimeMillis = now - 7200000,
            endTimeMillis = now - 4500000,
            durationSeconds = 2700, // 45 mins
            date = today,
            notes = "Menyelesaikan modul database dan UI"
          )
        )
        dao.insertTimerSession(
          TimerSessionEntity(
            activityTitle = "Membaca Buku Self Development",
            category = "Belajar",
            startTimeMillis = now - 4200000,
            endTimeMillis = now - 2700000,
            durationSeconds = 1500, // 25 mins
            date = today,
            notes = "Bab 3: Kebiasaan mikro"
          )
        )

        // 3. Initial Transactions & Finance Target
        dao.insertTransaction(
          TransactionEntity(
            title = "Gaji Bulanan",
            amount = 7500000.0,
            type = "INCOME",
            category = "Gaji",
            date = today,
            notes = "Pemasukan utama awal bulan"
          )
        )
        dao.insertTransaction(
          TransactionEntity(
            title = "Makan Siang & Kopi",
            amount = 45000.0,
            type = "EXPENSE",
            category = "Makanan",
            date = today,
            notes = "Ayam bakar + es teh manis"
          )
        )
        dao.insertTransaction(
          TransactionEntity(
            title = "Bensin Motor",
            amount = 35000.0,
            type = "EXPENSE",
            category = "Transport",
            date = today,
            notes = "Pertamax isi penuh"
          )
        )

        dao.insertOrUpdateFinanceTarget(
          FinanceTargetEntity(
            id = 1,
            monthlyBudgetLimit = 3500000.0,
            monthlySavingsTarget = 2500000.0,
            targetMonth = currentMonth
          )
        )

        // 4. Initial Daily Goals (Customizable Daily Targets)
        dao.insertDailyGoal(
          DailyGoalEntity(
            title = "Minum Air Putih",
            iconEmoji = "💧",
            targetValue = 2000,
            currentValue = 1250,
            unit = "ml",
            stepIncrement = 250,
            streak = 5,
            lastUpdatedDate = today,
            isCompleted = false
          )
        )
        dao.insertDailyGoal(
          DailyGoalEntity(
            title = "Membaca Buku",
            iconEmoji = "📖",
            targetValue = 20,
            currentValue = 10,
            unit = "halaman",
            stepIncrement = 5,
            streak = 3,
            lastUpdatedDate = today,
            isCompleted = false
          )
        )
        dao.insertDailyGoal(
          DailyGoalEntity(
            title = "Olahraga Harian",
            iconEmoji = "🏃",
            targetValue = 30,
            currentValue = 30,
            unit = "menit",
            stepIncrement = 10,
            streak = 7,
            lastUpdatedDate = today,
            isCompleted = true
          )
        )
        dao.insertDailyGoal(
          DailyGoalEntity(
            title = "Meditasi / Ketenangan",
            iconEmoji = "🧘",
            targetValue = 15,
            currentValue = 0,
            unit = "menit",
            stepIncrement = 5,
            streak = 2,
            lastUpdatedDate = today,
            isCompleted = false
          )
        )

        // 5. Initial Wishlist (Target Keinginan Custom)
        dao.insertWishlist(
          WishlistEntity(
            title = "Laptop Kerja Baru (MacBook / ThinkPad)",
            targetAmount = 18000000.0,
            savedAmount = 9500000.0,
            category = "Elektronik",
            targetDate = "2026-12-31",
            notes = "Untuk pengembangan aplikasi dan kerja produktif",
            isAchieved = false
          )
        )
        dao.insertWishlist(
          WishlistEntity(
            title = "Liburan ke Bali",
            targetAmount = 5000000.0,
            savedAmount = 2750000.0,
            category = "Travel",
            targetDate = "2026-11-20",
            notes = "Tiket pesawat + hotel + kuliner",
            isAchieved = false
          )
        )
        dao.insertWishlist(
          WishlistEntity(
            title = "Sepatu Lari Profesional",
            targetAmount = 1200000.0,
            savedAmount = 1200000.0,
            category = "Gaya Hidup",
            targetDate = "2026-10-15",
            notes = "Target lari 5K tercapai!",
            isAchieved = true
          )
        )
      }
    }
  }
}

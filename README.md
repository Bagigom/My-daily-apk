# DailyFocus (versi offline)

Aplikasi manajemen tugas harian, timer fokus, keuangan, serta target harian & impian.
**Berjalan 100% offline** — tanpa Firebase, Gemini, maupun izin INTERNET. Semua data
disimpan di perangkat (Room/SQLite + file gambar di penyimpanan internal).

## Yang berubah dari versi sebelumnya
- Dependensi online dihapus (Firebase, Retrofit, OkHttp, Moshi, secrets plugin, google-services).
- Izin `INTERNET` dihapus eksplisit di `AndroidManifest.xml`.
- Build tidak lagi butuh `.env`, `debug.keystore`, maupun API key.
- UI baru: latar belakang gradien, kartu efek kaca, header & navigasi melayang, kartu utama bergradien.
- Tombol palet (kanan atas) membuka **Tampilan & Latar Belakang**:
  mode tema (Sistem/Terang/Gelap), 8 warna latar, **foto latar dari galeri**, dan pengatur gelap-terang foto.
- **Target Impian** bisa diberi foto dari galeri (saat membuat atau lewat ketukan pada kartu).
- Database naik ke versi 2 dengan migrasi, sehingga data lama tidak hilang.

## Membuat APK
### Opsi A — Android Studio
1. Buka folder ini lewat **File → Open**, tunggu Gradle sync.
2. Colokkan HP (aktifkan USB debugging) lalu tekan **Run**, atau
   **Build → Build APK(s)** → file ada di `app/build/outputs/apk/debug/app-debug.apk`.

### Opsi B — Command line
```
gradle assembleDebug        # atau ./gradlew assembleDebug bila wrapper dibuat oleh Android Studio
```
Butuh JDK 17+ dan Android SDK (platform 36).

### Opsi C — GitHub Actions (tanpa install apa pun)
Unggah proyek ke GitHub → tab **Actions → Build APK → Run workflow** → unduh artefak APK.

APK release memakai kunci debug bawaan kecuali variabel `KEYSTORE_PATH`, `STORE_PASSWORD`,
`KEY_ALIAS`, `KEY_PASSWORD` diisi.

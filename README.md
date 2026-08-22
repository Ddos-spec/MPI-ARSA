<div align="center">

# MPI ARSA

**Aplikasi pembelajaran interaktif MPI ARSA untuk Android**

[![Latest Release](https://img.shields.io/github/v/release/Ddos-spec/MPI-ARSA?style=flat-square&label=release)](https://github.com/Ddos-spec/MPI-ARSA/releases/latest)
[![APK Build](https://img.shields.io/github/actions/workflow/status/Ddos-spec/MPI-ARSA/build-apk.yml?branch=main&style=flat-square&label=build)](https://github.com/Ddos-spec/MPI-ARSA/actions/workflows/build-apk.yml)
[![Runtime Test](https://img.shields.io/github/actions/workflow/status/Ddos-spec/MPI-ARSA/runtime-smoke.yml?branch=main&style=flat-square&label=runtime%20test)](https://github.com/Ddos-spec/MPI-ARSA/actions/workflows/runtime-smoke.yml)
<!-- APP_BADGE_START -->
[![Android](https://img.shields.io/badge/Android-5.0%2B-3DDC84?style=flat-square&logo=android&logoColor=white)](#persyaratan)
<!-- APP_BADGE_END -->

### [⬇️ DOWNLOAD APK TERBARU](https://github.com/Ddos-spec/MPI-ARSA/releases/latest/download/MPI-ARSA.apk)

[Semua versi](https://github.com/Ddos-spec/MPI-ARSA/releases) · [Build status](https://github.com/Ddos-spec/MPI-ARSA/actions) · [Laporkan masalah](https://github.com/Ddos-spec/MPI-ARSA/issues)

</div>

---

## Tentang MPI ARSA

MPI ARSA adalah aplikasi Android untuk menjalankan materi pembelajaran interaktif berbasis Articulate Storyline dalam pengalaman layar penuh. Materi utama berjalan dari aset lokal aplikasi melalui Android WebView agar dapat dibuka langsung dari launcher Android.

## Download

| File | Kegunaan |
| --- | --- |
| **[MPI-ARSA.apk](https://github.com/Ddos-spec/MPI-ARSA/releases/latest/download/MPI-ARSA.apk)** | APK terbaru untuk instalasi |
| [GitHub Releases](https://github.com/Ddos-spec/MPI-ARSA/releases) | Arsip versi aplikasi |
| [MPI-ARSA.apk.sha256](https://github.com/Ddos-spec/MPI-ARSA/releases/latest/download/MPI-ARSA.apk.sha256) | Checksum SHA-256 APK |
| [build-info.txt](https://github.com/Ddos-spec/MPI-ARSA/releases/latest/download/build-info.txt) | Informasi versi dan source build |

> APK ini didistribusikan langsung melalui GitHub untuk kebutuhan proyek/klien, bukan melalui Google Play.

## Cara instal

1. Download `MPI-ARSA.apk` dari tombol di atas.
2. Buka file APK di perangkat Android.
3. Jika Android meminta izin, aktifkan **Install unknown apps** untuk aplikasi/browser yang digunakan mengunduh APK.
4. Pilih **Install**, lalu buka **MPI ARSA**.

Android dapat menampilkan peringatan karena APK dipasang dari luar Google Play.

## Persyaratan

<!-- APP_REQUIREMENTS_START -->
- Android **5.0 (API 21)** atau lebih baru.
- Target SDK aplikasi: **35**.
<!-- APP_REQUIREMENTS_END -->
- Android System WebView / Google Chrome aktif dan diperbarui.
- Tampilan aplikasi menggunakan orientasi **landscape**.

## Pengalaman aplikasi

- Full-screen immersive Android experience.
- Loading state dan timeout saat materi dimuat.
- Tombol **Coba Lagi** jika WebView gagal atau renderer berhenti.
- Double-back untuk mencegah aplikasi tertutup karena salah tekan.
- Download HTTP/HTTPS menggunakan Android Download Manager.
- Asset lokal aplikasi dapat disimpan ke folder Downloads.
- Pilihan quiz aktif diberi feedback visual lebih jelas.

## Versi source saat ini

<!-- APP_METADATA_START -->
**v1.0.7** · `versionCode 8` · `minSdk 21` · `targetSdk 35`
<!-- APP_METADATA_END -->

Metadata di atas disinkronkan otomatis dari `app/build.gradle`. Badge **Latest Release** menunjukkan APK publik terbaru.

## Build, test, dan distribusi

Flow dibuat sederhana:

1. **Build APK** — membangun APK dan mengemas materi Storyline.
2. **Runtime Smoke Test** — memasang APK pada emulator Android API 21, 29, dan 34; mengecek `CONTENT_READY`, crash, serta layar blank.
3. **Publish APK** — APK yang lolos test dipublikasikan otomatis ke GitHub Releases.

Dengan pola ini, update project tetap gampang tetapi APK yang gagal render tidak langsung dilempar ke pengguna.

## Struktur repository

```text
MPI-ARSA/
├── app/                    # Android application source
├── dist/                   # Arsip APK lama
├── .github/workflows/      # Build, runtime test, publish, dan metadata README
├── build.gradle
├── settings.gradle
└── README.md
```

## URL download permanen

```text
https://github.com/Ddos-spec/MPI-ARSA/releases/latest/download/MPI-ARSA.apk
```

URL ini tetap sama meskipun versi aplikasi berubah.

---

<div align="center">

**MPI ARSA Android** · Source, build, test, dan distribusi dikelola melalui GitHub.

</div>

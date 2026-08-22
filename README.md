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
| **[MPI-ARSA.apk](https://github.com/Ddos-spec/MPI-ARSA/releases/latest/download/MPI-ARSA.apk)** | APK production-signed terbaru — pilihan utama untuk pengguna |
| [GitHub Releases](https://github.com/Ddos-spec/MPI-ARSA/releases) | Arsip versi resmi |
| [MPI-ARSA.apk.sha256](https://github.com/Ddos-spec/MPI-ARSA/releases/latest/download/MPI-ARSA.apk.sha256) | Checksum SHA-256 untuk verifikasi APK |
| [build-info.txt](https://github.com/Ddos-spec/MPI-ARSA/releases/latest/download/build-info.txt) | Versi, source commit, dan build run APK |

> **Untuk pengguna biasa:** tekan **DOWNLOAD APK TERBARU** di bagian paling atas. Build debug dari GitHub Actions hanya digunakan untuk pengujian dan tidak dipublikasikan sebagai rilis production.

## Cara instal

1. Download `MPI-ARSA.apk` dari tombol di atas.
2. Buka file APK di perangkat Android.
3. Jika Android meminta izin, aktifkan **Install unknown apps** untuk aplikasi/browser yang digunakan mengunduh APK.
4. Pilih **Install**, lalu buka **MPI ARSA**.

Pastikan APK berasal dari halaman **Releases** repository `Ddos-spec/MPI-ARSA` ini.

## Persyaratan

<!-- APP_REQUIREMENTS_START -->
- Android **5.0 (API 21)** atau lebih baru.
- Target SDK aplikasi: **35**.
<!-- APP_REQUIREMENTS_END -->
- Android System WebView / Google Chrome aktif dan diperbarui.
- Tampilan aplikasi menggunakan orientasi **landscape**.

## Pengalaman aplikasi

- Full-screen immersive Android experience.
- Loading state dan timeout yang jelas saat materi dimuat.
- Tombol **Coba Lagi** jika WebView gagal atau renderer berhenti.
- Double-back untuk mencegah aplikasi tertutup karena salah tekan.
- Download HTTP/HTTPS menggunakan Android Download Manager.
- Asset lokal aplikasi dapat disimpan ke folder Downloads.
- Pilihan quiz yang sedang aktif diberi feedback visual lebih jelas.

## Versi source saat ini

<!-- APP_METADATA_START -->
**v1.0.7** · `versionCode 8` · `minSdk 21` · `targetSdk 35`
<!-- APP_METADATA_END -->

Metadata di atas disinkronkan otomatis dari `app/build.gradle` ketika konfigurasi versi berubah. Badge **Latest Release** berasal langsung dari GitHub Releases sehingga selalu menunjukkan rilis resmi terbaru.

## Build, test, dan distribusi

Pipeline dibagi berdasarkan tanggung jawab:

1. **Build APK** — membangun APK debug khusus pengujian dan mengemas materi Storyline.
2. **Runtime Smoke Test** — memasang APK pada emulator Android, menunggu sinyal `CONTENT_READY`, memeriksa crash, dan menolak layar yang blank/hampir seragam.
3. **Publish Signed APK** — hanya berjalan untuk source yang masih relevan, membangun ulang APK release dari aset yang sudah diuji, memverifikasi signature serta flag debug, lalu membuat GitHub Release yang immutable.

Satu `versionName` hanya boleh menghasilkan satu rilis. Jika binary berubah, versi harus dinaikkan; workflow tidak lagi menimpa asset pada tag versi lama.

### Release signing

Rilis production membutuhkan GitHub Actions secrets berikut:

- `ANDROID_KEYSTORE_BASE64`
- `ANDROID_KEYSTORE_PASSWORD`
- `ANDROID_KEY_ALIAS`
- `ANDROID_KEY_PASSWORD`

Jika signing belum tersedia, build debug dan runtime test tetap berjalan tetapi **tidak ada debug APK yang dipromosikan menjadi production release**.

## Struktur repository

```text
MPI-ARSA/
├── app/                    # Android application source
├── dist/                   # Arsip APK lama; bukan jalur production terbaru
├── .github/workflows/      # Build, test, publish, dan maintenance
├── build.gradle
├── settings.gradle
└── README.md
```

## URL download permanen

```text
https://github.com/Ddos-spec/MPI-ARSA/releases/latest/download/MPI-ARSA.apk
```

URL tersebut tidak mengandung nomor versi. Begitu rilis production baru diterbitkan, link yang sama otomatis mengarah ke APK terbaru.

---

<div align="center">

**MPI ARSA Android** · Source, build, test, dan distribusi dikelola melalui GitHub.

</div>

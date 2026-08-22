<div align="center">

# MPI ARSA

**Aplikasi pembelajaran interaktif MPI ARSA untuk Android**

[![Latest Release](https://img.shields.io/github/v/release/Ddos-spec/MPI-ARSA?style=flat-square&label=version)](https://github.com/Ddos-spec/MPI-ARSA/releases/latest)
[![APK Build](https://img.shields.io/github/actions/workflow/status/Ddos-spec/MPI-ARSA/build-apk.yml?branch=main&style=flat-square&label=APK%20build)](https://github.com/Ddos-spec/MPI-ARSA/actions/workflows/build-apk.yml)
<!-- APP_BADGE_START -->
[![Android API](https://img.shields.io/badge/Android%20API-21%2B-3DDC84?style=flat-square&logo=android&logoColor=white)](#persyaratan)
<!-- APP_BADGE_END -->

### [⬇️ DOWNLOAD APK TERBARU](https://github.com/Ddos-spec/MPI-ARSA/releases/latest/download/MPI-ARSA.apk)

[Semua versi](https://github.com/Ddos-spec/MPI-ARSA/releases) · [Build status](https://github.com/Ddos-spec/MPI-ARSA/actions) · [Laporkan masalah](https://github.com/Ddos-spec/MPI-ARSA/issues)

</div>

---

## Tentang MPI ARSA

MPI ARSA adalah aplikasi Android yang membungkus materi pembelajaran interaktif berbasis Articulate Storyline menjadi pengalaman aplikasi layar penuh. Konten utama dijalankan dari aset lokal aplikasi melalui Android WebView sehingga pengguna dapat membuka materi langsung dari launcher Android.

## Download

| File | Kegunaan |
| --- | --- |
| **[MPI-ARSA.apk](https://github.com/Ddos-spec/MPI-ARSA/releases/latest/download/MPI-ARSA.apk)** | APK versi terbaru — pilihan utama untuk pengguna |
| [GitHub Releases](https://github.com/Ddos-spec/MPI-ARSA/releases) | Arsip seluruh rilis dan versi |
| [MPI-ARSA.apk.sha256](https://github.com/Ddos-spec/MPI-ARSA/releases/latest/download/MPI-ARSA.apk.sha256) | Checksum SHA-256 untuk verifikasi file |
| [Download langsung dari branch main](https://github.com/Ddos-spec/MPI-ARSA/raw/refs/heads/main/dist/MPI-ARSA.apk) | Jalur APK stabil sebagai fallback |

> **Untuk pengguna biasa:** cukup tekan **DOWNLOAD APK TERBARU** di bagian paling atas README ini.

## Cara instal

1. Download `MPI-ARSA.apk` dari tombol di atas.
2. Buka file APK di perangkat Android.
3. Jika Android meminta izin, aktifkan **Install unknown apps** untuk aplikasi/browser yang digunakan mengunduh APK.
4. Pilih **Install**, lalu buka **MPI ARSA**.

Android dapat menampilkan peringatan keamanan untuk APK yang dipasang di luar Google Play. Pastikan file berasal dari repository **Ddos-spec/MPI-ARSA** ini.

## Persyaratan

<!-- APP_REQUIREMENTS_START -->
- Android **API 21** atau lebih baru.
- Target SDK aplikasi: **35**.
<!-- APP_REQUIREMENTS_END -->
- Android System WebView / Google Chrome aktif dan diperbarui.
- Tampilan aplikasi menggunakan orientasi **landscape**.

## Fitur teknis

- Full-screen immersive Android experience.
- Articulate Storyline dijalankan sebagai aset lokal aplikasi.
- Local HTTPS origin melalui `WebViewAssetLoader`.
- Hardware-accelerated WebView.
- Link eksternal dibuka melalui aplikasi Android yang sesuai.
- Build APK otomatis menggunakan GitHub Actions.
- APK terbaru otomatis dipublikasikan ke **GitHub Releases**.
- Stable release asset: `MPI-ARSA.apk`.
- SHA-256 checksum dipublikasikan bersama setiap release.

## Versi saat ini

<!-- APP_METADATA_START -->
**v1.0.6** · `versionCode 7` · `minSdk 21` · `targetSdk 35`
<!-- APP_METADATA_END -->

Data di atas tidak diedit manual. Workflow publikasi membaca `versionName`, `versionCode`, `minSdk`, dan `targetSdk` langsung dari `app/build.gradle`, kemudian memperbarui README setiap build sukses.

## Struktur repository

```text
MPI-ARSA/
├── app/                    # Android application source
├── dist/                   # APK yang dipublikasikan otomatis
├── .github/workflows/      # Build, publish, dan runtime checks
├── build.gradle
├── settings.gradle
└── README.md
```

## Build & distribusi

Setiap perubahan pada branch `main` menjalankan pipeline build APK. Setelah build berhasil, pipeline publikasi akan:

1. mengambil APK hasil build;
2. membaca metadata aplikasi dari `app/build.gradle`;
3. memperbarui informasi versi dan SDK di README;
4. memperbarui APK di folder `dist/`;
5. membuat atau memperbarui GitHub Release untuk versi tersebut;
6. mengunggah `MPI-ARSA.apk` dan checksum SHA-256.

Dengan pola ini, pengguna selalu bisa memakai URL berikut untuk mengambil APK terbaru:

```text
https://github.com/Ddos-spec/MPI-ARSA/releases/latest/download/MPI-ARSA.apk
```

---

<div align="center">

**MPI ARSA Android** · Source, build, dan distribusi aplikasi dikelola melalui GitHub.

</div>
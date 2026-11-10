# Vývojové prostředí projektu Spirala

## 1. Zjištěný stav lokálního prostředí (Windows Host)

- **Operační systém:** Windows 10/11 (`10.0.26200`)
- **JDK pro Android build:** OpenJDK / Oracle JDK `21.0.12` (`C:\Program Files\Java\jdk-21.0.12`) — pevně nastaveno v `gradle.properties`.
- **Android SDK:** `C:\Users\Ladislav\AppData\Local\Android\Sdk`
  - Nainstalované platformy: `android-34`, `android-35`, `android-36`
  - Build-Tools: `34.0.0`, `35.0.0`, `36.1.0`
  - Platform-Tools: `adb` a `fastboot` verze `36.0.2`
- **Gradle:** Wrapper `8.13`

## 2. Požadavky pro plný AOSP a Kernel build

Sestavení samotného operačního systému AOSP a Linux kernelu nelze provádět přímo v nativním Windows prostředí — vyžaduje 64bitové Linux prostředí (Ubuntu 22.04/24.04 LTS nebo WSL2):
- Minimálně 32 GB RAM (plus swap),
- 350–450 GB volného místa na SSD/NVMe disku (ext4),
- Nainstalované balíčky pro AOSP (`repo`, `git`, `bc`, `bison`, `build-essential`, `curl`, `flex`, `g++-multilib`, `gcc-multilib`, `gnupg`, `gperf`, `libncurses5-dev`, `libssl-dev`, ` libxml2-utils`, `lzop`, `openjdk-17-jdk`, `rsync`, `schedtool`, `squashfs-tools`, `xsltproc`, `zip`, `zlib1g-dev`).

> **Upozornění před budoucím flashováním zařízení:**
> Odemčení bootloaderu (`fastboot oem unlock`) a přepis systémových oddílů vymaže veškerá uživatelská data v telefonu a při použití nekompatibilního obrazového souboru může vést k nefunkčnosti zařízení (brick). Před každým flashováním vždy ověříme přesné kódové označení modelu (`fastboot getvar product` / `ro.product.device`).


# Architektura projektu Spirala

Projekt **Spirala** je jednoúčelový divadelní titulkovací systém postavený primárně na telefonech **OnePlus Nord CE 4 Lite 5G (`camry` / `CPH2619`)** (s modulární podporou pro alternativní zařízení **Motorola**) s vlastním Android/AOSP prostředím a nativní klientskou aplikací v jazyce Kotlin.

## Rozdělení vrstev

1. **`android/` (Systémová vrstva AOSP)**
   - Produktová definice `teatro_spirala` bez Google Mobile Services (GMS) a bez zbytečných systémových aplikací.
   - Systémové properties (`system.prop`) pro trvalou Wi-Fi konektivitu a potlačení úvodního průvodce (`SetupWizard`).
2. **`device/` (Konfigurace zařízení OnePlus a Motorola)**
   - Primární cílová konfigurace `device/oneplus/common/` a `device/oneplus/camry/` pro **OnePlus Nord CE 4 Lite 5G** (Qualcomm Snapdragon 695 5G `SM6375` / `holi`, 6,67" AMOLED).
   - Modulární šablona `device/motorola/` pro případné alternativní modely Motorola.
3. **`kernel/` (Konfigurace jádra Linux)**
   - Jaderné konfigurační fragmenty (`kernel/oneplus/sm6375/fragments/teatro_kiosk.config` a `kernel/motorola/fragments/teatro_kiosk.config`) zajišťující stabilitu Wi-Fi (`qca_cld3`), BBR řízení zahlcení, správu wakelocků a řízení displeje.
4. **`vendor/` (Proprietární součásti)**
   - Pravidla a manifesty (`vendor/oneplus/camry/proprietary-files.txt`) pro proprietární binární soubory (Adreno 619 GPU, WCN399x Wi-Fi firmware, Health/nabíjecí HAL).
5. **`protocol/` (Komunikační protokol)**
   - Samostatný Kotlin/JVM modul definující formát paketů (`ProtocolPacket`), typy zpráv (`CUE`, `BLACKOUT`, `ACK`, `HEARTBEAT`, `RECONNECT`, `SYNC`, `LANGUAGE_CHANGE`, `SCRIPT_SYNC`), verzování protokolu a JSON serializaci.
6. **`app/` (Titulkovací Android aplikace)**
   - Nativní aplikace v Kotlinu + Jetpack Compose.
   - Celoobrazovkový režim (Immersive Sticky), černé divadelní pozadí (`#000000`), tlumená teplá typografie (`#E8E2D5`), podpora `BLACKOUT`, přepínání jazyka, velikosti písma, jasu a kontrola pořadí titulků pomocí sekvenčních čísel.
7. **`scripts/` (Automatizační a servisní skripty)**
   - Skripty pro kontrolu prostředí, sestavení APK, spouštění testů a instalaci přes ADB.


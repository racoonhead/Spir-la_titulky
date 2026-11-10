# Architektura projektu Spirala

Projekt **Spirala** je jednoúčelový divadelní titulkovací systém postavený na telefonech **Motorola** s vlastním Android/AOSP prostředím a nativní klientskou aplikací v jazyce Kotlin.

## Rozdělení vrstev

1. **`android/` (Systémová vrstva AOSP)**
   - Produktová definice `teatro_spirala` bez Google Mobile Services (GMS) a bez zbytečných systémových aplikací.
   - Systémové properties (`system.prop`) pro trvalou Wi-Fi konektivitu a potlačení úvodního průvodce (`SetupWizard`).
2. **`device/` (Konfigurace zařízení Motorola)**
   - Společný základ `device/motorola/common/` a šablona `device/motorola/template/` pro konkrétní model telefonu Motorola.
3. **`kernel/` (Konfigurace jádra Linux)**
   - Jaderné konfigurační fragmenty (`teatro_kiosk.config`) zajišťující stabilitu Wi-Fi a správu wakelocků.
4. **`vendor/` (Proprietární součásti)**
   - Pravidla a manifesty pro proprietární binární soubory (GPU, Wi-Fi firmware, nabíjení) extrahované z cílového zařízení Motorola.
5. **`protocol/` (Komunikační protokol)**
   - Samostatný Kotlin/JVM modul definující formát paketů (`ProtocolPacket`), typy zpráv (`CUE`, `BLACKOUT`, `ACK`, `HEARTBEAT`, `RECONNECT`, `SYNC`, `LANGUAGE_CHANGE`, `SCRIPT_SYNC`), verzování protokolu a JSON serializaci.
6. **`app/` (Titulkovací Android aplikace)**
   - Nativní aplikace v Kotlinu + Jetpack Compose.
   - Celoobrazovkový režim (Immersive Sticky), černé divadelní pozadí (`#000000`), tlumená teplá typografie (`#E8E2D5`), podpora `BLACKOUT`, přepínání jazyka, velikosti písma, jasu a kontrola pořadí titulků pomocí sekvenčních čísel.
7. **`scripts/` (Automatizační a servisní skripty)**
   - Skripty pro kontrolu prostředí, sestavení APK, spouštění testů a instalaci přes ADB.


# Cílové zařízení: OnePlus Nord CE 4 Lite 5G (`camry` / `CPH2619`)

Dokumentace technických parametrů, odemykání bootloaderu a architektury AOSP/Kernelu pro vybraný terminál **OnePlus Nord CE 4 Lite 5G**.

---

## 1. Technická specifikace

| Parametr | Hodnota | Význam pro divadelní provoz (Spirala) |
| :--- | :--- | :--- |
| **Modelové označení (EU)** | `CPH2619` | Evropská distribuce se stabilním `fastboot` rozhraním |
| **Kódové označení (Device / Platform)** | `camry` / `holi` (`blair`) | Sdílená referenční platforma Qualcomm |
| **Displej** | **6,67" AMOLED**, 1080 × 2400 px (20:9, ~394 ppi) | **Absolutní černá (`0,0 cd/m²`)** na pozadí `#000000` i v režimu `BLACKOUT` — žádné oslňování v hledišti |
| **Obnovovací frekvence** | 60 / 120 Hz | V AOSP zafixováno na **60 Hz** pro minimální spotřebu energie |
| **Čipset (SoC)** | **Qualcomm Snapdragon 695 5G (`SM6375`)**, 6 nm | Osvědčený 8jádrový čipset (2× Kryo 660 Gold + 6× Kryo 660 Silver) s výbornou podporou AOSP |
| **Grafický čip (GPU)** | Qualcomm Adreno 619 | Plná hardwarová akcelerace Jetpack Compose |
| **Paměť** | 8 GB LPDDR4X RAM / 128–256 GB UFS 2.2 | Dostatečná rezerva pro cache scénářů a plynulý chod bez swapování |
| **Baterie a nabíjení** | 5 110 / 5 500 mAh | Výdrž přes 12–16 hodin nepřetržitého provozu se zobrazenými titulky na AMOLED panelu |

---

## 2. Proč byl zvolen OnePlus Nord CE 4 Lite 5G

1. **AMOLED panel (nulový parazitní svit):** Na rozdíl od levných telefonů s IPS LCD displejem (kde ve tmě šedavě svítí celá plocha podsvícení) svítí na AMOLEDu pouze aktivní pixely textu titulku (`#E8E2D5`). V režimu `BLACKOUT` je displej opticky k nerozeznání od vypnutého telefonu.
2. **Hromadné odemykání bootloaderu bez omezení:** OnePlus nevyžaduje registraci účtů, vkládání SIM karet, čekací lhůty (7–30 dní) ani denní kvóty (jako Xiaomi HyperOS) a netrpí přepalováním pojistek Knox (jako Samsung).
3. **Platforma Qualcomm Snapdragon 695 (`SM6375` / `holi`):** Sdílená architektura s podporou **Project Treble (VNDK)** a **GKI (Generic Kernel Image)** umožňuje dvě cesty nasazení vlastního systému:
   - **Cesta A (Treble / AOSP GSI nad továrním vendorem):** Sestavení čistého AOSP `system.img` obsahujícího `SpiralaSubtitleApp` jako systémovou Kiosk aplikaci při zachování originálního `vendor` a `boot` oddílu (maximální stabilita Wi-Fi, nabíjení a AMOLED kalibrace).
   - **Cesta B (Plný device tree `device/oneplus/camry` + kernel `sm6375`):** Kompletní kompilace všech oddílů ze zdrojových kódů (`OnePlusOSS` kernel + vendor blobs).

---

## 3. Postup odemčení bootloaderu (příprava telefonů)

> **⚠️ Upozornění:** Odemčení bootloaderu provede tovární reset (`userdata` wipe). Doporučujeme zakoupit evropskou revizi **`CPH2619`** a před odemčením na telefonech neprovádět OTA aktualizace továrního OxygenOS, aby zůstala zachována plná kompatibilita `fastboot` režimu.

1. V továrním systému přejděte do **Nastavení → O zařízení → Verze** a 7× klepněte na **Číslo sestavení**.
2. V **Nastavení → Další nastavení → Možnosti pro vývojáře** povolte:
   - **OEM odemykání (OEM unlocking)**
   - **Ladění USB (USB debugging)**
3. Připojte telefon k PC a ověřte spojení:
   ```powershell
   adb devices
   adb reboot bootloader
   ```
4. V režimu Fastboot ověřte zařízení a odemkněte bootloader:
   ```powershell
   fastboot devices
   fastboot flashing unlock
   ```
5. Na obrazovce telefonu potvrďte odemčení tlačítky hlasitosti a zapínacím tlačítkem (`Unlock the bootloader`).

---

## 4. Struktura souborů v repozitáři pro OnePlus Nord CE 4 Lite 5G

- [`device/oneplus/common/common.mk`](../device/oneplus/common/common.mk) — společná konfigurace pro zařízení OnePlus (AMOLED color management, DC dimming, povolené systémové balíčky).
- [`device/oneplus/camry/BoardConfig.mk`](../device/oneplus/camry/BoardConfig.mk) — konfigurace desky `holi` (`SM6375`), `arm64-v8a` (`kryo300`), A/B oddílů, Super partition a AVB 2.0.
- [`device/oneplus/camry/device.mk`](../device/oneplus/camry/device.mk) — produktová definice modelu `CPH2619` (`camry`), DPI (`xxhdpi`) a fixace obnovovací frekvence displeje na 60 Hz.
- [`kernel/oneplus/sm6375/fragments/teatro_kiosk.config`](../kernel/oneplus/sm6375/fragments/teatro_kiosk.config) — jaderný konfigurační fragment pro čipset Snapdragon 695 (`CONFIG_QCA_CLD_WLAN`, `CONFIG_PM_WAKELOCKS`, `CONFIG_TCP_CONG_BBR`, `CONFIG_DRM_MSM_DSI`).
- [`vendor/oneplus/camry/proprietary-files.txt`](../vendor/oneplus/camry/proprietary-files.txt) — seznam proprietárních binárních knihoven (Adreno 619 GPU, WCN399x Wi-Fi, Health HAL).


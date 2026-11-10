# Spirala — Divadelní titulkovací systém (Android / AOSP)

**Spirala** je jednoúčelová platforma pro zobrazování divadelních titulků v reálném čase, postavená na upraveném systému **Android (AOSP)** a nativní klientské aplikaci v jazyce **Kotlin** pro telefony **OnePlus Nord CE 4 Lite 5G (`CPH2619` / `camry`)**.

Zařízení slouží jako dedikovaný terminál zapůjčovaný divákům během představení: po zapnutí automaticky nastartuje do titulkovací aplikace v režimu celé obrazovky, uzamkne systém proti opuštění aplikace (Kiosk / Device Owner), připojí se k lokální divadelní Wi-Fi síti a přijímá řídicí pokyny (`CUE`, `BLACKOUT`, synchronizaci scénáře) z centrálního titulkovacího serveru.

---

## Hlavní vlastnosti

### Titulkovací aplikace (`:app`)
- **Divadelní celoobrazovkový režim (Immersive Sticky):** Čistě černé pozadí (`#000000`) s nulovým parazitním svitem a teplou tlumenou typografií (`#E8E2D5`), která neoslňuje diváka ani okolí v zatemněném hledišti.
- **Režim `BLACKOUT`:** Okamžité zatemnění displeje na pokyn ze serveru nebo obsluhy.
- **Vícejazyčný provoz:** Okamžité přepínání jazykové mutace titulků (např. `CS`, `EN`, `DE`).
- **Individuální ergonomie:** Nastavení velikosti písma (`20sp` – `56sp`) a jasu obrazovky přímo divákem v bezpečných mezích.
- **Garantované pořadí titulků:** Kontrola sekvenčních čísel (`sequence`) eliminuje duplicitní nebo opožděné pakety.
- **Offline odolnost:** Lokální cache scénáře (Room / SQLite) a automatická obnova spojení (WebSocket reconnect) při krátkodobém výpadku Wi-Fi.

### Vlastní systém Android / AOSP (`android/`, `device/`, `kernel/`, `vendor/`)
- **Čistý AOSP základ bez GMS:** Žádné služby Google Play, žádné notifikace, žádné zbytečné systémové aplikace a žádná závislost na internetu.
- **Kiosk uzamčení:** Registrace aplikace jako systémové `HOME` aktivity v kombinaci s `DevicePolicyManager` (Lock Task Mode / Device Owner).
- **Optimalizace napájení a Wi-Fi:** Potlačení uspávání Wi-Fi adaptéru a buzení obrazovky bez nutnosti zásahu diváka.
- **Cílový hardware OnePlus Nord CE 4 Lite 5G (`camry` / `CPH2619`):** 6,67" AMOLED displej s nulovým parazitním svitem na černé (`0,0 cd/m²`), čipset Qualcomm Snapdragon 695 5G (`SM6375` / `holi`), device tree v `device/oneplus/camry`, jaderný fragment `kernel/oneplus/sm6375` a vendor manifest `vendor/oneplus/camry`.

---

## Struktura projektu

```text
Spirala/
├── android/        # Produktová konfigurace AOSP (teatro_spirala.mk, system.prop)
├── device/         # Device tree pro OnePlus Nord CE 4 Lite 5G (oneplus/common, oneplus/camry)
├── kernel/         # Konfigurační fragmenty Linux kernelu (oneplus/sm6375, wakelocky, Wi-Fi BBR)
├── vendor/         # Manifesty a pravidla pro proprietární binární soubory (oneplus/camry)
├── protocol/       # Kotlin/JVM modul komunikačního protokolu (JSON serializace, typy zpráv)
├── app/            # Nativní Android titulkovací aplikace (Kotlin + Jetpack Compose)
├── scripts/        # PowerShell skripty pro kontrolu prostředí, build, testy a ADB instalaci
└── docs/           # Technická dokumentace (architektura, prostředí, cílové zařízení OnePlus, nabídka)
```

---

## Komunikační protokol (`:protocol`)

Komunikace mezi centrálním serverem a terminály probíhá přes **WebSocket** v lokální Wi-Fi síti pomocí JSON zpráv.

### Příklad zprávy `CUE`
```json
{
  "type": "CUE",
  "protocol_version": 1,
  "show_id": "show_001",
  "sequence": 125,
  "cue_id": "cue_125",
  "language": "cs",
  "text": "Ukázkový text titulku"
}
```

### Podporované typy zpráv (`MessageType`)
| Typ zprávy | Popis |
| :--- | :--- |
| `CUE` | Zobrazení konkrétního titulku s pořadovým číslem `sequence` |
| `BLACKOUT` | Okamžité zhasnutí titulku (černá obrazovka) |
| `ACK` | Potvrzení přijetí zprávy terminálem |
| `HEARTBEAT` | Udržování a kontrola živého spojení |
| `RECONNECT` | Pokyn k reinicializaci spojení |
| `SYNC` | Vynucená synchronizace aktuálního stavu představení |
| `LANGUAGE_CHANGE` | Vzdálená změna aktivního jazyka terminálu |
| `SCRIPT_SYNC` | Přenos a uložení celého scénáře do lokální cache |

---

## Rychlý start (Sestavení aplikace a testy)

### Požadavky pro vývoj Android aplikace
- **JDK:** Java 21 LTS (projekt očekává výchozí cestu v `gradle.properties`, případně vlastní `JAVA_HOME`)
- **Android SDK:** API 35 (`android-35`), Build-Tools `35.0.0+`, Platform-Tools (`adb`, `fastboot`)
- **Gradle:** Součástí repozitáře je připravený Gradle Wrapper (`8.13`)

### 1. Kontrola vývojového prostředí
```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\check-env.ps1
```

### 2. Spuštění jednotkových testů a sestavení Debug APK
```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\build-app.ps1
```
Případně přímo přes Gradle Wrapper:
```powershell
.\gradlew.bat test :app:assembleDebug
```
Výstupní instalační balíček naleznete v:
`app/build/outputs/apk/debug/app-debug.apk`

### 3. Instalace a spuštění na připojeném telefonu přes ADB
```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\install-app.ps1
```

---

## Sestavení vlastního AOSP systému a kernelu

Kompilace kompletního obrazu systému Android (AOSP) a jádra Linux vyžaduje **64bitové prostředí Linux** (Ubuntu 22.04 / 24.04 LTS nebo WSL2):
- Minimálně **32 GB RAM** (doporučeno 64 GB RAM nebo odpovídající swap),
- **350–450 GB** volného místa na SSD/NVMe disku,
- Nainstalované nástroje `repo`, `git`, `fastboot`, `adb` a standardní AOSP build závislosti.

Podrobnosti o nastavení prostředí a cílovém zařízení jsou uvedeny v dokumentaci:
- [Cílové zařízení: OnePlus Nord CE 4 Lite 5G (`docs/device-oneplus-nord-ce4-lite.md`)](docs/device-oneplus-nord-ce4-lite.md)
- [Architektura systému (`docs/architecture.md`)](docs/architecture.md)
- [Vývojové prostředí (`docs/environment.md`)](docs/environment.md)
- [Cenová nabídka a rozsah prací (`docs/nabidka.md`)](docs/nabidka.md)

> **⚠️ Bezpečnostní upozornění před flashováním zařízení:**
> Odemčení bootloaderu (`fastboot flashing unlock`) a nahrávání vlastních systémových obrazů (`boot`, `system`, `vendor`, `vbmeta`) nenávratně vymaže všechna uživatelská data v telefonu. Použití obrazů určených pro jiný model nebo revizi může vést k trvalému znefunkčnění zařízení (hard brick). Před každou operací vždy ověřte přesné označení zařízení (`CPH2619` / `camry`).

---

## Stav projektu a plán etap

- [x] **Etapa 1:** Kontrola prostředí, vytvoření modulární struktury projektu, datový model protokolu (`:protocol`) a první plně funkční celoobrazovková verze aplikace (`:app`) s testovacím scénářem a jednotkovými testy.
- [ ] **Etapa 2:** WebSocket klient s automatickým reconnectem, heartbeatem, zpracováním mezer v sekvencích a lokální Room/SQLite databází scénáře pro offline provoz.
- [ ] **Etapa 3:** Kiosk režim, Device Owner (DPC) konfigurace, správa napájení a automatické připojení k divadelní Wi-Fi.
- [ ] **Etapa 4:** Integrace telefonu **OnePlus Nord CE 4 Lite 5G (`camry` / `CPH2619`)** — device tree, kernel defconfig (`SM6375`), extrakce vendor blobs, sestavení a ověření AOSP obrazu.

---

## Licence a autoři

Vyvíjeno pro projekt **Teatro Svjotlo — Spirala**.


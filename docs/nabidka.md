# Cenová nabídka a rozsah prací — Projekt Spirala (Softwarová část terminálů)

**Předmět plnění:** Kompletní softwarová část jednoúčelových divadelních titulkovacích terminálů (cílová platforma **OnePlus Nord CE 4 Lite 5G**, s modulární přenositelností na telefony Motorola) — vývoj klientské Android aplikace, komunikační vrstvy s offline cache, systémové uzamčení (Kiosk / Device Owner) a příprava vlastního operačního systému Android (AOSP) včetně konfigurace jádra (Linux kernel) a ověření funkčnosti zařízení.

---

## 1. Vymezení rozsahu prací (Scope)

### Součástí dodávky JE:
- **Nativní Android aplikace (`:app`):** Celoobrazovkový divadelní režim (Immersive Sticky), nulový parazitní svit (`#000000`), režim `BLACKOUT`, přepínání jazykových mutací, uživatelské nastavení velikosti písma a jasu v bezpečných mezích.
- **Komunikační vrstva a offline cache (`:protocol` + DB):** WebSocket klient s automatickou obnovou spojení (reconnect), udržováním spojení (`HEARTBEAT`), potvrzováním zpráv (`ACK`), kontrolou pořadí (`sequence`) a lokální databází scénáře (Room / SQLite) pro plynulý provoz při výpadku Wi-Fi.
- **Kiosk režim a systémová konfigurace:** Uzamčení zařízení do jednoúčelového režimu (Device Owner / `DevicePolicyManager` / Lock Task Mode), automatický start aplikace po zapnutí telefonu, potlačení notifikací, správa napájení (zákaz hlubokého uspávání Wi-Fi a řízené buzení displeje).
- **Vlastní Android systém (AOSP), device tree, kernel a vendor:** Příprava čistého systému Android bez Google služeb (GMS) a bez zbytečných systémových aplikací, konfigurace pro vybraný model telefonu **OnePlus Nord CE 4 Lite 5G (`CPH2619` / `camry`, Snapdragon 695 5G, 6,67" AMOLED)** (device tree, úprava Linux kernelu, integrace proprietárních ovladačů / vendor blobs).
- **Testování, automatizace a dokumentace:** Jednotkové a integrační testy, skripty pro automatizované sestavení a nahrání systému/aplikace (`adb` / `fastboot`) a technická dokumentace pro údržbu.

### Součástí dodávky NENÍ:
- Vývoj centrálního titulkovacího serveru a režijního (odbavovacího) softwaru.
- Návrh, dodávka a konfigurace síťové infrastruktury (Wi-Fi routery, AP, switche).
- Dodávka a fyzické úpravy hardwaru (telefony, nabíjecí stanice, držáky).

---

## 2. Rozpočet a hodinová náročnost

- **Hodinová sazba:** **600 Kč / hod.**

| Oblast prací | Odhad pracnosti (hod.) | Odhad ceny (Kč) |
| :--- | :---: | :---: |
| **1. AOSP a build prostředí** | 20–35 h | 12 000 – 21 000 Kč |
| **2. Device tree, kernel, vendor (OnePlus)** | 50–100 h | 30 000 – 60 000 Kč |
| **3. Android aplikace** | 35–60 h | 21 000 – 36 000 Kč |
| **4. Kiosk a systémová konfigurace** | 15–30 h | 9 000 – 18 000 Kč |
| **5. Komunikace a offline cache** | 20–35 h | 12 000 – 21 000 Kč |
| **6. Testování a dokumentace** | 25–40 h | 15 000 – 24 000 Kč |
| **Celkem** | **165–300 h** | **99 000 – 180 000 Kč** |

---

## 3. Cenové varianty a cílový rozpočet

- **Cílová (realistická) cena projektu:** **144 000 Kč** *(240 hodin × 600 Kč/h)*
- **Jednodušší realizace:** **99 000 Kč** *(165 hodin — dobře zdokumentovaný model telefonu s dostupnými AOSP/LineageOS podklady a bez nutnosti hlubokých zásahů do kernelu)*
- **Složitější realizace:** **180 000 Kč+** *(300+ hodin — model s uzavřenými ovladači, složitou extrakcí vendor blobs, laděním proprietárních HAL modulů nebo nestandardním řešením napájení/OTA)*

---

## 4. Doporučený způsob realizace a fakturace (po etapách)

Práce budou vykazovány v hodinové sazbě **600 Kč/h** a fakturovány po uzavření jednotlivých ověřitelných etap:

1. **Etapa 1 — Základ aplikace a komunikačního protokolu**
   - Architektura projektu, datový model protokolu (`:protocol`), první funkční celoobrazovková verze aplikace (`:app`) s přepínáním jazyků, nastavením písma/jasu, režimem `BLACKOUT` a jednotkovými testy.
2. **Etapa 2 — Síťová komunikace a offline odolnost**
   - WebSocket klient s automatickým reconnectem, `HEARTBEAT`/`ACK`, synchronizace scénáře (`SCRIPT_SYNC`) do lokální Room/SQLite databáze a záložní offline režim.
3. **Etapa 3 — Kiosk uzamčení a správa zařízení (Device Owner)**
   - Implementace Device Policy Controlleru (Lock Task Mode), automatický start po zapnutí, řízení napájení/Wi-Fi a automatizované instalační skripty (ověřitelné již na sériovém telefonu OnePlus Nord CE 4 Lite 5G).
4. **Etapa 4 — Vlastní AOSP systém, kernel a integrace OnePlus Nord CE 4 Lite 5G**
   - Sestavení čistého AOSP obrazu, device tree (`camry` / `SM6375`), kernel konfigurace, vendor blobs, flashování a zátěžové ověření celého terminálu.

> **Důležitá podmínka k Etapě 4 (AOSP a kernel):**
> Náročnost vývoje AOSP a kernelu nelze fixovat předem bez technického ověření vzorkového kusu cílového telefonu **OnePlus Nord CE 4 Lite 5G (`CPH2619`)** (ověření revize firmwaru, odemčení bootloaderu přes `fastboot flashing unlock` a kompatibility jaderných modulů). Přesný odhad této etapy bude potvrzen po otestování vzorkového kusu zařízení.


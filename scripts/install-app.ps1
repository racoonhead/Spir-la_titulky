# Skript pro instalaci a spuštění titulkovací aplikace na připojeném telefonu Motorola přes ADB
$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
$apkPath = Join-Path $projectRoot "app\build\outputs\apk\debug\app-debug.apk"

if (-not (Test-Path $apkPath)) {
    Write-Host "APK soubor neexistuje, spouštím nejprve build..." -ForegroundColor Yellow
    & "$PSScriptRoot\build-app.ps1"
}

Write-Host "=== Kontrola připojených ADB zařízení ===" -ForegroundColor Cyan
$devices = & $adb devices | Select-String -Pattern "\tdevice$"
if (-not $devices) {
    Write-Error "Žádné zařízení v režimu ADB 'device' nebylo nalezeno. Připojte telefon a povolte USB ladění."
}

Write-Host "=== Instaluji APK: $apkPath ===" -ForegroundColor Cyan
& $adb install -r $apkPath

Write-Host "=== Spouštím MainActivity ===" -ForegroundColor Cyan
& $adb shell am start -n "cz.teatrosvjotlo.spirala.app/.MainActivity"


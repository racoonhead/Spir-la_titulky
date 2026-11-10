# Skript pro kontrolu vývojového prostředí projektu Spirala
$ErrorActionPreference = "Stop"

Write-Host "=== Kontrola vývojového prostředí Spirala ===" -ForegroundColor Cyan

$jdkPath = "C:\Program Files\Java\jdk-21.0.12"
$sdkPath = "$env:LOCALAPPDATA\Android\Sdk"
$adbPath = "$sdkPath\platform-tools\adb.exe"
$fastbootPath = "$sdkPath\platform-tools\fastboot.exe"

if (Test-Path "$jdkPath\bin\java.exe") {
    Write-Host "[OK] JDK 21 nalezeno: $jdkPath" -ForegroundColor Green
} else {
    Write-Host "[CHYBA] JDK 21 nebylo nalezeno v $jdkPath" -ForegroundColor Red
}

if (Test-Path $sdkPath) {
    Write-Host "[OK] Android SDK nalezeno: $sdkPath" -ForegroundColor Green
} else {
    Write-Host "[CHYBA] Android SDK nebylo nalezeno v $sdkPath" -ForegroundColor Red
}

if (Test-Path $adbPath) {
    $adbVer = & $adbPath version | Select-Object -First 1
    Write-Host "[OK] ADB připraveno: $adbVer" -ForegroundColor Green
} else {
    Write-Host "[CHYBA] ADB nebylo nalezeno" -ForegroundColor Red
}

if (Test-Path $fastbootPath) {
    $fbVer = & $fastbootPath --version | Select-Object -First 1
    Write-Host "[OK] Fastboot připraven: $fbVer" -ForegroundColor Green
} else {
    Write-Host "[CHYBA] Fastboot nebyl nalezen" -ForegroundColor Red
}


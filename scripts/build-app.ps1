# Skript pro sestavení a spuštění jednotkových testů titulkovací aplikace a protokolu
param(
    [switch]$Release
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path -Parent $PSScriptRoot
Push-Location $projectRoot

try {
    Write-Host "=== Spouštím jednotkové testy (:protocol a :app) ===" -ForegroundColor Cyan
    .\gradlew.bat test
    if ($LASTEXITCODE -ne 0) {
        throw "Jednotkové testy selhaly s návratovým kódem $LASTEXITCODE."
    }

    if ($Release) {
        Write-Host "=== Sestavuji Release APK ===" -ForegroundColor Cyan
        .\gradlew.bat :app:assembleRelease
    } else {
        Write-Host "=== Sestavuji Debug APK ===" -ForegroundColor Cyan
        .\gradlew.bat :app:assembleDebug
    }
    if ($LASTEXITCODE -ne 0) {
        throw "Sestavení APK selhalo s návratovým kódem $LASTEXITCODE."
    }

    Write-Host "=== Build úspěšně dokončen ===" -ForegroundColor Green
} finally {
    Pop-Location
}

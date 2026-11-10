# Společná konfigurace pro zařízení Motorola v projektu Spirala
# Modulární vrstva sdílená všemi podporovanými modely Motorola.

PRODUCT_CHARACTERISTICS := nosdcard

# Odstranění nepotřebných výchozích AOSP aplikací (prohlížeč, kalendář, kontakty, galerie, hudba, SMS)
PRODUCT_ARTIFACT_PATH_REQUIREMENT_ALLOWED_LIST += \
    system/priv-app/SpiralaSubtitleApp/SpiralaSubtitleApp.apk


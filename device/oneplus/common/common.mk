# Společná konfigurace pro zařízení OnePlus v projektu Spirala
# Modulární vrstva sdílená modely OnePlus (Qualcomm Snapdragon platforma).

PRODUCT_CHARACTERISTICS := nosdcard

# Potlačení nadbytečných systémových balíčků a povolení klientské Kiosk aplikace
PRODUCT_ARTIFACT_PATH_REQUIREMENT_ALLOWED_LIST += \
    system/priv-app/SpiralaSubtitleApp/SpiralaSubtitleApp.apk

# Výchozí systémové vlastnosti pro AMOLED displeje OnePlus v divadelním provozu
PRODUCT_PROPERTY_OVERRIDES += \
    ro.surface_flinger.use_color_management=true \
    persist.sys.sf.color_saturation=1.0 \
    persist.vendor.dc_dimming.enable=1


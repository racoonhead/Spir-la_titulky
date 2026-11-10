# Základní produktová konfigurace AOSP pro jednoúčelový divadelní terminál Spirala
# Bez Google Mobile Services (GMS), bez zbytečných systémových aplikací.

$(call inherit-product, $(SRC_TARGET_DIR)/product/core_64_bit_only.mk)
$(call inherit-product, $(SRC_TARGET_DIR)/product/aosp_base_telephony.mk)

PRODUCT_NAME := teatro_spirala
PRODUCT_BRAND := TeatroSvjotlo
PRODUCT_MODEL := Spirala Subtitle Terminal
PRODUCT_MANUFACTURER := Motorola

# Zařazení titulkovací aplikace jako výchozí systémové HOME aplikace
PRODUCT_PACKAGES += \
    SpiralaSubtitleApp

# Systémové properties pro kiosk a divadelní provoz
PRODUCT_PROPERTY_OVERRIDES += \
    ro.setupwizard.mode=DISABLED \
    ro.com.android.dataroaming=false \
    persist.sys.usb.config=adb \
    ro.teatro.kiosk.enabled=true \
    wifi.supplicant_scan_interval=15

# Načtení společné konfigurace Motorola
$(call inherit-product-if-exists, device/motorola/common/common.mk)


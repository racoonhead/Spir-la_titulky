# Device konfigurace pro OnePlus Nord CE 4 Lite 5G (camry / CPH2619)
# Displej: 6.67" AMOLED (1080x2400, 120Hz), SoC: Qualcomm SM6375 (Snapdragon 695 5G)

$(call inherit-product, device/oneplus/common/common.mk)
$(call inherit-product-if-exists, vendor/oneplus/camry/camry-vendor.mk)

PRODUCT_DEVICE := camry
PRODUCT_BRAND := OnePlus
PRODUCT_MODEL := CPH2619
PRODUCT_MANUFACTURER := OnePlus

# Hustota obrazovky pro 6.67" FHD+ AMOLED (1080x2400, ~394 ppi)
PRODUCT_AAPT_CONFIG := normal
PRODUCT_AAPT_PREF_CONFIG := xxhdpi

# Optimalizace pro divadelní provoz (60 Hz pro úsporu baterie a stabilní podání černé na AMOLEDu)
PRODUCT_PROPERTY_OVERRIDES += \
    ro.surface_flinger.set_idle_timer_ms=1000 \
    persist.vendor.display.default_fps=60 \
    ro.vendor.qti.va_aosp.support=1


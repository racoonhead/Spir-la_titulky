# Šablona device.mk pro konkrétní model Motorola
# Zde se připojuje vendor tree a specifické overlaye pro daný hardware.

$(call inherit-product, device/motorola/common/common.mk)
$(call inherit-product-if-exists, vendor/motorola/template/template-vendor.mk)


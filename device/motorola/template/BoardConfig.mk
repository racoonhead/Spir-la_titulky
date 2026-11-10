# Šablona BoardConfig.mk pro konkrétní model telefonu Motorola (bude upřesněno dle kódového označení)
# Příklad: devon / rhode / fogos / apod.

BOARD_VENDOR := motorola
TARGET_ARCH := arm64
TARGET_ARCH_VARIANT := armv8-a
TARGET_CPU_ABI := arm64-v8a
TARGET_CPU_VARIANT := generic

# A/B Seamless OTA podpora (typická pro moderní telefony Motorola)
AB_OTA_UPDATER := true
AB_OTA_PARTITIONS += \
    boot \
    dtbo \
    system \
    system_ext \
    product \
    vendor \
    vbmeta


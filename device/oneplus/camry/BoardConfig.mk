# BoardConfig.mk pro OnePlus Nord CE 4 Lite 5G
# Kódové označení zařízení: camry (EU model: CPH2619)
# Čipset: Qualcomm Snapdragon 695 5G (SM6375 / platforma holi / blair)

BOARD_VENDOR := oneplus
TARGET_BOARD_PLATFORM := holi
TARGET_BOOTLOADER_BOARD_NAME := holi

# Architektura CPU (2x Kryo 660 Gold @ 2.2 GHz + 6x Kryo 660 Silver @ 1.7 GHz)
TARGET_ARCH := arm64
TARGET_ARCH_VARIANT := armv8-2a-dotprod
TARGET_CPU_ABI := arm64-v8a
TARGET_CPU_VARIANT := generic
TARGET_CPU_VARIANT_RUNTIME := kryo300

# Grafický čip Qualcomm Adreno 619
TARGET_USES_VULKAN := true
BOARD_USES_ADRENO := true

# Kernel a GKI konfigurace (Snapdragon 695 / Linux 5.4 / 6.1 GKI)
BOARD_KERNEL_BASE := 0x00000000
BOARD_KERNEL_PAGESIZE := 4096
BOARD_INCLUDE_DTB_IN_BOOTIMG := true
BOARD_BOOT_HEADER_VERSION := 3
TARGET_KERNEL_ARCH := arm64
TARGET_KERNEL_CONFIG := vendor/holi-qgki_defconfig

# A/B Seamless OTA a dynamické oddíly (Super partition)
AB_OTA_UPDATER := true
AB_OTA_PARTITIONS += \
    boot \
    vendor_boot \
    dtbo \
    vbmeta \
    vbmeta_system \
    system \
    system_ext \
    product \
    vendor \
    odm

BOARD_SUPER_PARTITION_GROUPS := oneplus_dynamic_partitions
BOARD_ONEPLUS_DYNAMIC_PARTITIONS_PARTITION_LIST := \
    system \
    system_ext \
    product \
    vendor \
    odm

# Android Verified Boot (AVB 2.0)
BOARD_AVB_ENABLE := true
BOARD_AVB_MAKE_VBMETA_IMAGE_ARGS += --flags 3


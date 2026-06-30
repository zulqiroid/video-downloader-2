package com.all.video.downloader.fast.hd.secure.video.downloader.domain.settings

enum class DownloadLocationType(
    val storageValue: String
) {
    INTERNAL_STORAGE("internal_storage"),
    SD_CARD("sd_card"),
    CUSTOM_FOLDER("custom_folder");

    companion object {
        fun fromStorageValue(value: String?): DownloadLocationType {
            return entries.firstOrNull { it.storageValue == value }
                ?: INTERNAL_STORAGE
        }
    }
}
package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states

import com.all.video.downloader.fast.hd.secure.video.downloader.R

object CompletedOptionsFactory {

    fun create(): List<DownloadOptionItem> {

        return listOf(

            DownloadOptionItem(
                id = DownloadOptionId.Rename,
                title = R.string.rename,
                description = R.string.change_the_file_name,
                icon = R.drawable.ic_pen_filled
            ),

            DownloadOptionItem(
                id = DownloadOptionId.MoveToVault,
                title = R.string.move_to_vault,
                description = R.string.secure_with_pin,
                icon = R.drawable.ic_vault_filled
            ),

            DownloadOptionItem(
                id = DownloadOptionId.Share,
                title = R.string.share,
                description = R.string.send_via_message,
                icon = R.drawable.ic_share
            ),

            DownloadOptionItem(
                id = DownloadOptionId.FileInfo,
                title = R.string.file_info,
                description = R.string.check_file_properties,
                icon = R.drawable.ic_info_filled
            ),

            DownloadOptionItem(
                id = DownloadOptionId.Delete,
                title = R.string.delete,
                description = R.string.remove_file_permanently,
                icon = R.drawable.ic_bin_filled,
                isDestructive = true
            )
        )
    }
}
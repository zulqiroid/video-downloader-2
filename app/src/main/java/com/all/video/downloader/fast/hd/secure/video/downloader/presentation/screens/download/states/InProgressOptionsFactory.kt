package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states

 import com.all.video.downloader.fast.hd.secure.video.downloader.R
 import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadStatus

object InProgressOptionsFactory {

    fun create(
        status: DownloadStatus
    ): List<DownloadOptionItem> {

        val primaryAction = when (status) {

            DownloadStatus.DOWNLOADING ->
                DownloadOptionItem(
                    id = DownloadOptionId.Pause,
                    title = R.string.pause_download,
                    description = R.string.stop_downloading,
                    icon = R.drawable.ic_pause
                )

            DownloadStatus.PAUSED ->
                DownloadOptionItem(
                    id = DownloadOptionId.Resume,
                    title = R.string.resume_download,
                    description = R.string.continue_downloading,
                    icon =  R.drawable.ic_play
                )

            DownloadStatus.FAILED ->
                DownloadOptionItem(
                    id = DownloadOptionId.Retry,
                    title = R.string.retry_download,
                    description = R.string.try_downloading_again,
                    icon =  R.drawable.ic_recreate_outlined
                )
            else -> null
        }

        return buildList {

            primaryAction?.let(::add)

            add(
                DownloadOptionItem(
                    id = DownloadOptionId.Cancel,
                    title = R.string.cancel_download,
                    description = R.string.remove_downloading,
                    icon = R.drawable.ic_cross_filled
                )
            )

            add(
                DownloadOptionItem(
                    id = DownloadOptionId.MoveToVault,
                    title = R.string.move_to_vault,
                    description = R.string.secure_with_pin,
                    icon = R.drawable.ic_vault_filled,
                    enabled = false
                )
            )

            add(
                DownloadOptionItem(
                    id = DownloadOptionId.FileInfo,
                    title = R.string.file_info,
                    description = R.string.check_file_properties,
                    icon = R.drawable.ic_info_filled
                )
            )

            add(
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
}
package com.video.downloader.data.repository.implementation

import android.content.Context
import android.os.Environment
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.video.downloader.data.worker.VideoDownloadWorker
import com.video.downloader.domain.models.DownloadItem
import com.video.downloader.domain.models.DownloadStatus
import com.video.downloader.domain.repository.DownloadProgressStore
import com.video.downloader.domain.repository.VideoDownloadRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoDownloadRepositoryImpl @Inject constructor(
    @ApplicationContext context: Context,
     private val progressStore: DownloadProgressStore,
) : VideoDownloadRepository {

    private val appContext = context.applicationContext

    private val workManager = WorkManager.getInstance(appContext)

    override fun observeDownloads(): Flow<List<DownloadItem>> {
        return progressStore.observeDownloads()
    }

    override suspend fun startDownload(
        url: String,
    ): Long {
        val id = System.currentTimeMillis()
        val fileName = "video_$id.mp4"

        enqueueDownload(
            id = id,
            url = url,
            fileName = fileName,
            replaceExisting = true
        )

        return id
    }

    override suspend fun pauseDownload(
        id: Long,
    ) {
        val item = progressStore.get(id) ?: return

        if (item.status != DownloadStatus.DOWNLOADING) {
            return
        }

        workManager.cancelUniqueWork(
            uniqueWorkName(id)
        )

        val tempFile = File(
            getDownloadFolder(),
            "${item.fileName}.part"
        )

        val downloadedBytes = tempFile
            .takeIf { it.exists() }
            ?.length()
            ?: item.downloadedBytes

        progressStore.upsert(
            item.copy(
                status = DownloadStatus.PAUSED,
                downloadedBytes = downloadedBytes,
                progress = calculateProgress(
                    downloadedBytes = downloadedBytes,
                    totalBytes = item.totalBytes
                ),
                speedBytesPerSec = 0L,
                lastEtaSeconds = -1L
            )
        )
    }

    override suspend fun resumeDownload(
        id: Long,
    ) {
        val item = progressStore.get(id) ?: return

        if (item.status != DownloadStatus.PAUSED && item.status != DownloadStatus.FAILED) {
            return
        }

        enqueueDownload(
            id = item.id,
            url = item.url,
            fileName = item.fileName,
            replaceExisting = true
        )
    }

    override suspend fun cancelDownload(
        id: Long,
    ) {
        val item = progressStore.get(id)

        workManager.cancelUniqueWork(
            uniqueWorkName(id)
        )

        if (item != null) {
            File(item.filePath).delete()

            File(
                getDownloadFolder(),
                "${item.fileName}.part"
            ).delete()
        }

        progressStore.remove(id)
    }

    override suspend fun getAllDownloadedFiles(): List<DownloadItem> {
        val folder = getDownloadFolder()

        if (!folder.exists()) return emptyList()

        val fileItems = folder
            .listFiles()
            ?.filter { file ->
                file.isFile && !file.name.endsWith(PART_FILE_EXTENSION)
            }
            ?.map { file ->
                DownloadItem(
                    id = file.absolutePath.hashCode().toLong(),
                    url = file.absolutePath,
                    fileName = file.name,
                    filePath = file.absolutePath,
                    progress = 100,
                    status = DownloadStatus.SUCCESS,
                    downloadedBytes = file.length(),
                    totalBytes = file.length()
                )
            }
            .orEmpty()

        fileItems.forEach { item ->
            progressStore.upsert(item)
        }

        return fileItems
    }

    private suspend fun enqueueDownload(
        id: Long,
        url: String,
        fileName: String,
        replaceExisting: Boolean,
    ) {
        val finalFile = File(
            getDownloadFolder(),
            fileName
        )

        val tempFile = File(
            getDownloadFolder(),
            "$fileName.part"
        )

        val currentItem = progressStore.get(id)

        progressStore.upsert(
            DownloadItem(
                id = id,
                url = url,
                fileName = fileName,
                filePath = finalFile.absolutePath,
                progress = currentItem?.progress ?: calculateProgress(
                    downloadedBytes = tempFile.length(),
                    totalBytes = currentItem?.totalBytes ?: 0L
                ),
                status = DownloadStatus.DOWNLOADING,
                downloadedBytes = tempFile.length(),
                totalBytes = currentItem?.totalBytes ?: 0L,
                speedBytesPerSec = 0L,
                lastEtaSeconds = -1L
            )
        )

        val inputData = Data.Builder()
            .putLong(
                VideoDownloadWorker.KEY_ID,
                id
            )
            .putString(
                VideoDownloadWorker.KEY_URL,
                url
            )
            .putString(
                VideoDownloadWorker.KEY_FILE_NAME,
                fileName
            )
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(
                NetworkType.CONNECTED
            )
            .build()

        val workRequest = OneTimeWorkRequestBuilder<VideoDownloadWorker>()
            .setInputData(inputData)
            .setConstraints(constraints)
            .addTag(WORK_TAG)
            .addTag(uniqueWorkName(id))
            .build()

        workManager.enqueueUniqueWork(
            uniqueWorkName(id),
            if (replaceExisting) {
                ExistingWorkPolicy.REPLACE
            } else {
                ExistingWorkPolicy.KEEP
            },
            workRequest
        )
    }

    private fun getDownloadFolder(): File {
        return File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ),
            DOWNLOAD_FOLDER_NAME
        ).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    private fun calculateProgress(
        downloadedBytes: Long,
        totalBytes: Long,
    ): Int {
        if (totalBytes <= 0L) return 0

        return ((downloadedBytes * 100L) / totalBytes)
            .toInt()
            .coerceIn(0, 100)
    }

    private fun uniqueWorkName(
        id: Long,
    ): String {
        return "$WORK_TAG-$id"
    }

    private companion object {
        private const val DOWNLOAD_FOLDER_NAME = "VideoDownloader"
        private const val WORK_TAG = "video_download"
        private const val PART_FILE_EXTENSION = ".part"
    }
}
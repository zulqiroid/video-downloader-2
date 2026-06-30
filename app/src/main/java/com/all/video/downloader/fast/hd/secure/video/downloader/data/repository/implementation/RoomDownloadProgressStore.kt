package com.all.video.downloader.fast.hd.secure.video.downloader.data.repository.implementation

import com.all.video.downloader.fast.hd.secure.video.downloader.data.local.room.download.DownloadProgressDao
import com.all.video.downloader.fast.hd.secure.video.downloader.data.local.room.download.toDomain
import com.all.video.downloader.fast.hd.secure.video.downloader.data.local.room.download.toEntity
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadItem
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.DownloadStatus
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.repository.DownloadProgressStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomDownloadProgressStore @Inject constructor(
    private val dao: DownloadProgressDao
) : DownloadProgressStore {

    private val mutex = Mutex()

    override fun observeDownloads(): Flow<List<DownloadItem>> {
        return dao.observeDownloads()
            .map { entities ->
                entities
                    .map { entity -> entity.toDomain() }
                    .filterActiveOnly()
            }
    }

    override suspend fun upsert(item: DownloadItem) {
        mutex.withLock {
            if (item.status == DownloadStatus.SUCCESS) {
                dao.remove(item.id)
                return@withLock
            }

            val existingEntity = dao.get(item.id)

            dao.upsert(
                item.toEntity(
                    existingEntity = existingEntity
                )
            )
        }
    }

    override suspend fun remove(id: Long) {
        mutex.withLock {
            dao.remove(id)
        }
    }

    override suspend fun get(id: Long): DownloadItem? {
        return dao.get(id)
            ?.toDomain()
            ?.takeIf { item ->
                item.status == DownloadStatus.DOWNLOADING ||
                        item.status == DownloadStatus.PAUSED ||
                        item.status == DownloadStatus.FAILED
            }
    }

    override suspend fun getAll(): List<DownloadItem> {
        return dao.getAll()
            .map { entity -> entity.toDomain() }
            .filterActiveOnly()
    }

    override suspend fun clear() {
        mutex.withLock {
            dao.clear()
        }
    }

    private fun List<DownloadItem>.filterActiveOnly(): List<DownloadItem> {
        return filter { item ->
            item.status == DownloadStatus.DOWNLOADING ||
                    item.status == DownloadStatus.PAUSED ||
                    item.status == DownloadStatus.FAILED
        }
    }
}
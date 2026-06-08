package com.video.downloader.data.repository.implementation.download

import com.video.downloader.BuildConfig
import com.video.downloader.data.remote.downloader.DownloaderApi
import com.video.downloader.domain.download.DownloadFetchResult
import com.video.downloader.domain.download.DownloadPlatformDetector
import com.video.downloader.domain.download.DownloadableMedia
import com.video.downloader.domain.repository.download.DownloaderRepository
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloaderRepositoryImpl @Inject constructor(
    private val api: DownloaderApi,
    private val platformDetector: DownloadPlatformDetector
) : DownloaderRepository {

    override suspend fun fetchDownloadInfo(
        inputUrl: String
    ): DownloadFetchResult {
        val cleanUrl = inputUrl.trim()

        debug {
            """
            fetchDownloadInfo() started
            inputUrl=${cleanUrl.safePreview()}
            """.trimIndent()
        }

        val platform = platformDetector.detect(cleanUrl)

        debug {
            """
            platform detection result
            detected=${platform?.name ?: "null"}
            inputUrl=${cleanUrl.safePreview()}
            """.trimIndent()
        }

        if (platform == null) {
            error {
                """
                Unsupported platform
                inputUrl=${cleanUrl.safePreview()}
                """.trimIndent()
            }

            throw IllegalArgumentException("This video platform is not supported yet.")
        }

        val response = api.fetchVideo(
            url = cleanUrl,
            platform = platform
        )

        debug {
            """
            API response received in repository
            apiStatus=${response.status}
            responsePlatform=${response.platform.orEmpty()}
            title=${response.title.orEmpty().safePreview(80)}
            downloadablesCount=${response.downloadables.size}
            """.trimIndent()
        }

        if (!response.status) {
            error {
                """
                API returned status=false
                platform=${platform.name}
                inputUrl=${cleanUrl.safePreview()}
                """.trimIndent()
            }

            throw IllegalStateException("Video fetch failed.")
        }

        val downloadables = response.downloadables.mapNotNull { item ->
            val mediaUrl = item.url?.trim().orEmpty()

            if (mediaUrl.isBlank()) {
                debug {
                    """
                    Skipping empty downloadable
                    quality=${item.quality.orEmpty()}
                    """.trimIndent()
                }

                null
            } else {
                DownloadableMedia(
                    quality = item.quality?.takeIf { it.isNotBlank() } ?: "Video",
                    url = mediaUrl
                )
            }
        }

        debug {
            """
            Downloadables mapped
            validDownloadables=${downloadables.size}
            qualities=${downloadables.map { it.quality }}
            best=${downloadables.firstOrNull()?.quality.orEmpty()}
            """.trimIndent()
        }

        if (downloadables.isEmpty()) {
            error {
                """
                No valid downloadable video found
                platform=${platform.name}
                inputUrl=${cleanUrl.safePreview()}
                """.trimIndent()
            }

            throw IllegalStateException("No downloadable video found.")
        }

        val result = DownloadFetchResult(
            title = response.title?.takeIf { it.isNotBlank() }
                ?: "${platform.displayName}_${System.currentTimeMillis()}",
            platform = response.platform?.takeIf { it.isNotBlank() }
                ?: platform.displayName,
            thumbnailUrl = response.imageUrl,
            downloadables = downloadables
        )

        debug {
            """
            fetchDownloadInfo() success
            title=${result.title.safePreview(80)}
            platform=${result.platform}
            thumbnailAvailable=${!result.thumbnailUrl.isNullOrBlank()}
            bestQuality=${result.bestDownloadable?.quality.orEmpty()}
            bestUrl=${result.bestDownloadable?.url.orEmpty().safePreview(120)}
            """.trimIndent()
        }

        return result
    }

    private fun debug(message: () -> String) {
        if (BuildConfig.DEBUG) {
            Timber.tag(TAG).d(message())
        }
    }

    private fun error(message: () -> String) {
        if (BuildConfig.DEBUG) {
            Timber.tag(TAG).e(message())
        }
    }

    private fun String.safePreview(
        limit: Int = 140
    ): String {
        if (isBlank()) return ""

        val normalized = replace("\n", " ")
            .replace("\r", " ")
            .trim()

        return if (normalized.length <= limit) {
            normalized
        } else {
            normalized.take(limit) + "...[truncated]"
        }
    }

    private companion object {
        private const val TAG = "DownloaderRepo"
    }
}
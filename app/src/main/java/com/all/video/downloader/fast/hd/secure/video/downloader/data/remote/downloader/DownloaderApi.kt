package com.all.video.downloader.fast.hd.secure.video.downloader.data.remote.downloader

import com.all.video.downloader.fast.hd.secure.video.downloader.BuildConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.data.remote.downloader.dto.DownloaderResponseDto
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.download.DownloadPlatform
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.AppApiRemoteConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.repository.VideoDownloaderRemoteConfigRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class DownloaderApi @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json,
    private val remoteConfigRepository: VideoDownloaderRemoteConfigRepository,
) {

    suspend fun fetchVideo(
        url: String,
        platform: DownloadPlatform,
    ): DownloaderResponseDto {
        val cleanUrl = url.trim()

        val apiConfig = remoteConfigRepository
            .current()
            .appConfig
            .api

        val validatedConfig = apiConfig.validateOrThrow()

        val endpoint = buildEndpoint(
            baseUrl = validatedConfig.downloaderBaseUrl.orEmpty(),
            endpointPath = platform.endpointPath
        )

        debug {
            """
            fetchVideo() started
            platform=${platform.name}
            endpoint=$endpoint
            inputUrl=${cleanUrl.safePreview()}
            formField=$FORM_FIELD_URL
            secretHeaderName=${validatedConfig.downloaderSecretHeader}
            secretHeaderAdded=${!validatedConfig.downloaderSecretKey.isNullOrBlank()}
            remoteConfigSource=${remoteConfigRepository.current().source}
            """.trimIndent()
        }

        return try {
            val response = httpClient.post(endpoint) {
                header(
                    key = validatedConfig.downloaderSecretHeader,
                    value = validatedConfig.downloaderSecretKey.orEmpty()
                )

                setBody(
                    MultiPartFormDataContent(
                        formData {
                            append(FORM_FIELD_URL, cleanUrl)
                        }
                    )
                )
            }

            debug {
                """
                fetchVideo() HTTP response received
                platform=${platform.name}
                statusCode=${response.status.value}
                statusDescription=${response.status.description}
                """.trimIndent()
            }

            val rawBody = response.bodyAsText()

            debug {
                """
                fetchVideo() raw response
                length=${rawBody.length}
                body=${rawBody.safeBodyPreview()}
                """.trimIndent()
            }

            val parsed = json.decodeFromString<DownloaderResponseDto>(rawBody)

            debug {
                """
                fetchVideo() parsed successfully
                apiStatus=${parsed.status}
                responsePlatform=${parsed.platform.orEmpty()}
                title=${parsed.title.orEmpty().safePreview(80)}
                thumbnailAvailable=${!parsed.imageUrl.isNullOrBlank()}
                downloadablesCount=${parsed.downloadables.size}
                qualities=${parsed.downloadables.map { it.quality.orEmpty() }}
                """.trimIndent()
            }

            parsed.downloadables.forEachIndexed { index, item ->
                debug {
                    """
                    downloadable[$index]
                    quality=${item.quality.orEmpty()}
                    url=${item.url.orEmpty().safePreview(120)}
                    """.trimIndent()
                }
            }

            parsed
        } catch (throwable: HttpRequestTimeoutException) {
            error(throwable) {
                """
                fetchVideo() timeout
                platform=${platform.name}
                endpoint=$endpoint
                inputUrl=${cleanUrl.safePreview()}
                """.trimIndent()
            }

            throw IOException("Request timed out. Please try again.", throwable)
        } catch (throwable: Throwable) {
            error(throwable) {
                """
                fetchVideo() failed
                platform=${platform.name}
                endpoint=$endpoint
                inputUrl=${cleanUrl.safePreview()}
                reason=${throwable.message.orEmpty()}
                """.trimIndent()
            }

            if (throwable is IOException) {
                throw throwable
            }

            throw IOException("Something went wrong while fetching your video link.", throwable)
        }
    }

    private fun AppApiRemoteConfig.validateOrThrow(): AppApiRemoteConfig {
        if (!downloaderEnabled) {
            throw IOException("Downloader service is disabled right now.")
        }

        if (downloaderBaseUrl.isNullOrBlank()) {
            throw IOException("Downloader service is not configured yet.")
        }

        if (downloaderSecretKey.isNullOrBlank()) {
            throw IOException("Downloader access key is not configured yet.")
        }

        if (downloaderSecretHeader.isBlank()) {
            throw IOException("Downloader secret header is not configured yet.")
        }

        return this
    }

    private fun buildEndpoint(
        baseUrl: String,
        endpointPath: String,
    ): String {
        val cleanBaseUrl = baseUrl.trim().trimEnd('/')
        val cleanEndpointPath = endpointPath.trim().trimStart('/')

        return "$cleanBaseUrl/$cleanEndpointPath"
    }

    private fun debug(message: () -> String) {
        if (BuildConfig.DEBUG) {
            Timber.tag(TAG).d(message())
        }
    }

    private fun error(
        throwable: Throwable,
        message: () -> String,
    ) {
        if (BuildConfig.DEBUG) {
            Timber.tag(TAG).e(throwable, message())
        }
    }

    private fun String.safePreview(
        limit: Int = 140,
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

    private fun String.safeBodyPreview(
        limit: Int = 2_000,
    ): String {
        if (isBlank()) return ""

        val normalized = replace("\n", " ")
            .replace("\r", " ")
            .trim()

        return if (normalized.length <= limit) {
            normalized
        } else {
            normalized.take(limit) + "...[truncated ${normalized.length} chars]"
        }
    }

    private companion object {
        private const val TAG = "DownloaderApi"

        /*
         * If backend form field is different, change only this.
         * Common names: url, link, video_url
         */
        private const val FORM_FIELD_URL = "url"
    }
}
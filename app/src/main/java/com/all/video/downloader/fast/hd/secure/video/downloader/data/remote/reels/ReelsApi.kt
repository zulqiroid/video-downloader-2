package com.all.video.downloader.fast.hd.secure.video.downloader.data.remote.reels

import com.all.video.downloader.fast.hd.secure.video.downloader.BuildConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.data.remote.reels.dto.ReelsCategoryResponseDto
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.AppApiRemoteConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.repository.VideoDownloaderRemoteConfigRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReelsApi @Inject constructor(
    private val httpClient: HttpClient,
    private val json: Json,
    private val remoteConfigRepository: VideoDownloaderRemoteConfigRepository
) {

    suspend fun fetchCategories(): List<ReelsCategoryResponseDto> {
        val apiConfig = remoteConfigRepository
            .current()
            .appConfig
            .api
            .validateOrThrow()

        val endpoint = buildEndpoint(
            baseUrl = apiConfig.downloaderBaseUrl.orEmpty(),
            endpointPath = REELS_CATEGORIES_PATH
        )

        debug {
            """
            fetchCategories() started
            endpoint=$endpoint
            secretHeaderName=${apiConfig.downloaderSecretHeader}
            secretHeaderAdded=${!apiConfig.downloaderSecretKey.isNullOrBlank()}
            """.trimIndent()
        }

        return try {
            val response = httpClient.get(endpoint) {
                header(
                    key = apiConfig.downloaderSecretHeader,
                    value = apiConfig.downloaderSecretKey.orEmpty()
                )
            }

            debug {
                """
                fetchCategories() HTTP response received
                statusCode=${response.status.value}
                statusDescription=${response.status.description}
                """.trimIndent()
            }

            val rawBody = response.bodyAsText()

            debug {
                """
                fetchCategories() raw response
                length=${rawBody.length}
                body=${rawBody.safeBodyPreview()}
                """.trimIndent()
            }

            val parsed = json.decodeFromString(
                deserializer = ListSerializer(ReelsCategoryResponseDto.serializer()),
                string = rawBody
            )

            debug {
                """
                fetchCategories() parsed successfully
                categories=${parsed.size}
                totalItems=${parsed.sumOf { category -> category.items.size }}
                """.trimIndent()
            }

            parsed
        } catch (throwable: HttpRequestTimeoutException) {
            error(throwable) {
                """
                fetchCategories() timeout
                endpoint=$endpoint
                """.trimIndent()
            }

            throw IOException("Reels request timed out. Please try again.", throwable)
        } catch (throwable: IOException) {
            error(throwable) {
                """
                fetchCategories() failed with IO exception
                endpoint=$endpoint
                reason=${throwable.message.orEmpty()}
                """.trimIndent()
            }

            throw throwable
        } catch (throwable: Throwable) {
            error(throwable) {
                """
                fetchCategories() failed
                endpoint=$endpoint
                reason=${throwable.message.orEmpty()}
                """.trimIndent()
            }

            throw IOException("Something went wrong while fetching reels.", throwable)
        }
    }

    private fun AppApiRemoteConfig.validateOrThrow(): AppApiRemoteConfig {
        if (!downloaderEnabled) {
            throw IOException("Reels service is disabled right now.")
        }

        if (downloaderBaseUrl.isNullOrBlank()) {
            throw IOException("Reels service is not configured yet.")
        }

        if (downloaderSecretKey.isNullOrBlank()) {
            throw IOException("Reels access key is not configured yet.")
        }

        if (downloaderSecretHeader.isBlank()) {
            throw IOException("Reels secret header is not configured yet.")
        }

        return this
    }

    private fun buildEndpoint(
        baseUrl: String,
        endpointPath: String
    ): String {
        val cleanBaseUrl = baseUrl
            .trim()
            .trimEnd('/')

        val cleanEndpointPath = endpointPath
            .trim()
            .trimStart('/')

        return "$cleanBaseUrl/$cleanEndpointPath"
    }

    private fun debug(message: () -> String) {
        if (BuildConfig.DEBUG) {
            Timber.tag(TAG).d(message())
        }
    }

    private fun error(
        throwable: Throwable,
        message: () -> String
    ) {
        if (BuildConfig.DEBUG) {
            Timber.tag(TAG).e(throwable, message())
        }
    }

    private fun String.safeBodyPreview(
        limit: Int = 2_000
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
        private const val TAG = "ReelsApi"
        private const val REELS_CATEGORIES_PATH = "/reels/categories/"
    }
}
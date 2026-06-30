package com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.data.repository

import com.core.ads.data.remote.AdsRemoteConfigMapper
import com.core.ads.data.remote.AdsRemoteConfigMapperResult
import com.core.ads.domain.AdsCoreConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.all.video.downloader.fast.hd.secure.video.downloader.BuildConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.di.IoDispatcher
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.RemoteConfigKeys
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.defaults.DefaultRemoteConfigJsonFactory
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.defaults.DefaultVideoDownloaderRemoteConfigFactory
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.RemoteConfigSource
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.VideoDownloaderRemoteConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.VideoDownloaderRemoteConfigSnapshot
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.repository.VideoDownloaderRemoteConfigRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.mapper.AppRemoteConfigMapper
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.mapper.AppRemoteConfigMapperResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseVideoDownloaderRemoteConfigRepository @Inject constructor(
    private val firebaseRemoteConfig: FirebaseRemoteConfig,
    private val appRemoteConfigMapper: AppRemoteConfigMapper,
    private val adsRemoteConfigMapper: AdsRemoteConfigMapper,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : VideoDownloaderRemoteConfigRepository {

    private val defaultJson: String = DefaultRemoteConfigJsonFactory.create()

    private val fallbackAppConfig: VideoDownloaderRemoteConfig =
        DefaultVideoDownloaderRemoteConfigFactory.create()

    /**
     * Ads should stay disabled until Remote Config explicitly enables them.
     * This is safer for production, consent, and premium flow wiring.
     */
    private val fallbackAdsConfig: AdsCoreConfig = AdsCoreConfig(
        adsEnabled = false,
        canRequestAds = false,
        isDebug = BuildConfig.DEBUG
    )

    private val defaultSnapshot: VideoDownloaderRemoteConfigSnapshot =
        buildSnapshot(
            rawJson = defaultJson,
            source = RemoteConfigSource.LOCAL_DEFAULT,
            errorMessage = null
        )

    private val mutableConfigState =
        MutableStateFlow(defaultSnapshot)

    override val configState: StateFlow<VideoDownloaderRemoteConfigSnapshot> =
        mutableConfigState.asStateFlow()

    @Volatile
    private var isConfigured: Boolean = false

    override fun current(): VideoDownloaderRemoteConfigSnapshot {
        return mutableConfigState.value
    }

    override suspend fun initialize(): VideoDownloaderRemoteConfigSnapshot {
        return withContext(ioDispatcher) {
            runCatching {
                ensureConfigured()

                val rawJson = firebaseRemoteConfig
                    .getString(RemoteConfigKeys.VIDEO_DOWNLOADER_CONFIG_V1)
                    .cleanOrDefault(defaultJson)

                val snapshot = buildSnapshot(
                    rawJson = rawJson,
                    source = RemoteConfigSource.FIREBASE_CACHED,
                    errorMessage = null
                )

                mutableConfigState.value = snapshot
                snapshot
            }.getOrElse { throwable ->
                val snapshot = buildSnapshot(
                    rawJson = defaultJson,
                    source = RemoteConfigSource.FALLBACK_AFTER_ERROR,
                    errorMessage = throwable.message ?: "Failed to initialize Remote Config."
                )

                mutableConfigState.value = snapshot
                snapshot
            }
        }
    }

    override suspend fun fetchAndActivate(): VideoDownloaderRemoteConfigSnapshot {
        return withContext(ioDispatcher) {
            runCatching {
                ensureConfigured()

                firebaseRemoteConfig
                    .fetchAndActivate()
                    .await()

                val rawJson = firebaseRemoteConfig
                    .getString(RemoteConfigKeys.VIDEO_DOWNLOADER_CONFIG_V1)
                    .cleanOrDefault(defaultJson)

                val snapshot = buildSnapshot(
                    rawJson = rawJson,
                    source = RemoteConfigSource.FIREBASE_REMOTE_ACTIVATED,
                    errorMessage = null
                )

                mutableConfigState.value = snapshot
                snapshot
            }.getOrElse { throwable ->
                val snapshot = buildSnapshot(
                    rawJson = defaultJson,
                    source = RemoteConfigSource.FALLBACK_AFTER_ERROR,
                    errorMessage = throwable.message ?: "Failed to fetch Remote Config."
                )

                mutableConfigState.value = snapshot
                snapshot
            }
        }
    }

    private suspend fun ensureConfigured() {
        if (isConfigured) return

        val settings = FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds(
                if (BuildConfig.DEBUG) {
                    DEBUG_MIN_FETCH_INTERVAL_SECONDS
                } else {
                    RELEASE_MIN_FETCH_INTERVAL_SECONDS
                }
            )
            .build()

        firebaseRemoteConfig
            .setConfigSettingsAsync(settings)
            .await()

        firebaseRemoteConfig
            .setDefaultsAsync(
                mapOf(
                    RemoteConfigKeys.VIDEO_DOWNLOADER_CONFIG_V1 to defaultJson
                )
            )
            .await()

        isConfigured = true
    }

    private fun buildSnapshot(
        rawJson: String,
        source: RemoteConfigSource,
        errorMessage: String?
    ): VideoDownloaderRemoteConfigSnapshot {
        val warnings = mutableListOf<String>()

        val appConfig = when (
            val result = appRemoteConfigMapper.mapFromJson(
                jsonString = rawJson,
                fallbackConfig = fallbackAppConfig
            )
        ) {
            is AppRemoteConfigMapperResult.Success -> {
                warnings += result.warnings.map { "APP_CONFIG: $it" }
                result.config
            }

            is AppRemoteConfigMapperResult.Failure -> {
                warnings += "APP_CONFIG: ${result.errorMessage}"
                result.fallbackConfig
            }
        }

        val adsConfig = when (
            val result = adsRemoteConfigMapper.mapFromJson(
                jsonString = rawJson,
                fallbackConfig = fallbackAdsConfig
            )
        ) {
            is AdsRemoteConfigMapperResult.Success -> {
                warnings += result.warnings.map { "ADS_CONFIG: $it" }
                result.config
            }

            is AdsRemoteConfigMapperResult.Failure -> {
                warnings += "ADS_CONFIG: ${result.errorMessage}"
                result.fallbackConfig
            }
        }

        return VideoDownloaderRemoteConfigSnapshot(
            rawJson = rawJson,
            appConfig = appConfig,
            adsConfig = adsConfig,
            source = source,
            warnings = warnings,
            errorMessage = errorMessage,
            updatedAtMillis = System.currentTimeMillis()
        )
    }

    private fun String?.cleanOrDefault(defaultValue: String): String {
        return this
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: defaultValue
    }

    private companion object {
        private const val DEBUG_MIN_FETCH_INTERVAL_SECONDS = 0L
        private const val RELEASE_MIN_FETCH_INTERVAL_SECONDS = 3600L
    }
}
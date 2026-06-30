package com.core.ads.data.remote

import com.core.ads.domain.AdsCoreConfig

sealed interface AdsRemoteConfigMapperResult {

    data class Success(
        val config: AdsCoreConfig,
        val warnings: List<String> = emptyList()
    ) : AdsRemoteConfigMapperResult

    data class Failure(
        val fallbackConfig: AdsCoreConfig,
        val errorMessage: String
    ) : AdsRemoteConfigMapperResult
}
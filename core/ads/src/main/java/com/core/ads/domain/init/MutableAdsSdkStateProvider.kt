package com.core.ads.domain.init

interface MutableAdsSdkStateProvider : AdsSdkStateProvider {

    fun markNotInitialized(message: String? = null)

    fun markInitializing(message: String? = null)

    fun markInitialized(message: String? = null)

    fun markBlocked(message: String)

    fun markFailed(message: String)
}
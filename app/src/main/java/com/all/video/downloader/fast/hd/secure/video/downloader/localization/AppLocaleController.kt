package com.all.video.downloader.fast.hd.secure.video.downloader.localization

import android.content.Context
import android.content.res.Configuration
import android.os.LocaleList
import com.all.video.downloader.fast.hd.secure.video.downloader.data.local.datastore.repository.LocalDataStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLocaleController @Inject constructor(
    private val localDataStoreRepository: LocalDataStoreRepository
) {

    fun observeAppLocale(): Flow<AppLocale> {
        return localDataStoreRepository
            .getSelectedLanguageCode()
            .map { languageCode ->
                AppLocale.fromLanguageCode(languageCode)
            }
            .distinctUntilChanged()
    }

    suspend fun setAppLocale(
        appLocale: AppLocale
    ) {
        localDataStoreRepository.setSelectedLanguageCode(
            code = appLocale.languageCode
        )
    }

    fun createLocalizedContext(
        context: Context,
        appLocale: AppLocale
    ): Context {
        val locale = appLocale.toJavaLocale()

        /**
         * Only set default locale in memory.
         * Do NOT call LocaleManager.applicationLocales here.
         *
         * Reason:
         * LocaleManager can recreate Activity.
         * Compose already updates UI through LocalizedAppProvider.
         */
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration).apply {
            setLocales(LocaleList(locale))
            setLayoutDirection(locale)
        }

        return context.createConfigurationContext(configuration)
    }
}
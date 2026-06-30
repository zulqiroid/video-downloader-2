package com.all.video.downloader.fast.hd.secure.video.downloader.localization

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import android.content.res.Configuration
import android.content.res.Resources
import android.text.TextUtils
import android.view.View
import androidx.activity.compose.LocalActivityResultRegistryOwner
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.savedstate.compose.LocalSavedStateRegistryOwner

@Composable
fun LocalizedAppProvider(
    appLocale: AppLocale,
    appLocaleController: AppLocaleController,
    content: @Composable () -> Unit
) {
    val baseContext = LocalContext.current

    /**
     * Yeh important hai:
     *
     * Raw createConfigurationContext(...) Activity nahi hota.
     * Agar hum usko direct LocalContext mein provide kar dein to
     * rememberLauncherForActivityResult / BackHandler / Activity lookup break ho sakti hai.
     *
     * Is wrapper ka baseContext original Activity rahega,
     * lekin resources localized context ke use honge.
     */
    val localizedContext = remember(
        baseContext,
        appLocale
    ) {
        val configurationContext = appLocaleController.createLocalizedContext(
            context = baseContext,
            appLocale = appLocale
        )

        LocalizedContextWrapper(
            baseContext = baseContext,
            localizedContext = configurationContext
        )
    }

    val localizedConfiguration = remember(
        localizedContext,
        appLocale
    ) {
        Configuration(localizedContext.resources.configuration)
    }

    val layoutDirection = remember(appLocale) {
        val direction = TextUtils.getLayoutDirectionFromLocale(
            appLocale.toJavaLocale()
        )
            LayoutDirection.Ltr
    }

    /**
     * Activity related composition locals ko preserve karna zaroori hai.
     * Warna result launchers, back handler, lifecycle based APIs crash kar sakti hain.
     */
    val activityResultRegistryOwner = checkNotNull(
        LocalActivityResultRegistryOwner.current
    ) {
        "No ActivityResultRegistryOwner was provided via LocalActivityResultRegistryOwner"
    }

    val onBackPressedDispatcherOwner = checkNotNull(
        LocalOnBackPressedDispatcherOwner.current
    ) {
        "No OnBackPressedDispatcherOwner was provided via LocalOnBackPressedDispatcherOwner"
    }
    val lifecycleOwner = LocalLifecycleOwner.current
    val savedStateRegistryOwner = LocalSavedStateRegistryOwner.current

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalConfiguration provides localizedConfiguration,
        LocalLayoutDirection provides layoutDirection,
        LocalActivityResultRegistryOwner provides activityResultRegistryOwner,
        LocalOnBackPressedDispatcherOwner provides onBackPressedDispatcherOwner,
        LocalLifecycleOwner provides lifecycleOwner,
        LocalSavedStateRegistryOwner provides savedStateRegistryOwner
    ) {
        content()
    }
}

private class LocalizedContextWrapper(
    baseContext: Context,
    private val localizedContext: Context
) : ContextWrapper(baseContext) {

    override fun getResources(): Resources {
        return localizedContext.resources
    }

    override fun getAssets(): AssetManager {
        return localizedContext.assets
    }

    override fun getTheme(): Resources.Theme {
        return localizedContext.theme
    }
}
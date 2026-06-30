package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.core.ads.ui.rememberAdsActivity
import com.core.ads.ui.rememberInterstitialAdGate
import com.core.ads.ui.rememberInterstitialBackNavigationAction
import com.all.video.downloader.fast.hd.secure.video.downloader.BuildConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdScreenKeys
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.navigation.Screen
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.events.MoreEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.events.MoreNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.events.MoreUiEffect
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.viewModel.MoreViewModel
import androidx.core.net.toUri

@Composable
fun MoreRootSRC(
    backStack: NavBackStack<NavKey>,
    viewModel: MoreViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    val activity = rememberAdsActivity()
    val interstitialAdGate = rememberInterstitialAdGate()

    val customFolderLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        context.persistTreeUriPermission(uri)

        viewModel.onEvent(
            MoreEvents.OnCustomFolderPicked(
                treeUri = uri.toString()
            )
        )
    }

    val navigateBackWithInterstitial = rememberInterstitialBackNavigationAction(
        screenKey = VideoDownloaderAdScreenKeys.MORE
    ) {
        backStack.remove(Screen.More)
    }

    BackHandler {
        viewModel.onEvent(MoreEvents.BackClicked)
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(
            MoreEvents.OnSdCardAvailabilityChanged(
                isAvailable = context.isRemovableStorageAvailable()
            )
        )
    }

    LaunchedEffect(viewModel.navEvents, activity, interstitialAdGate) {
        viewModel.navEvents.collect { navEvent ->
            when (navEvent) {
                MoreNavEvents.NavigateBack -> {
                    navigateBackWithInterstitial()
                }

                MoreNavEvents.NavigateToLanguage -> {
                    interstitialAdGate?.showForScreen(
                        activity = activity,
                        screenKey = VideoDownloaderAdScreenKeys.APP_LANGUAGE,
                        onComplete = {
                            backStack.add(Screen.AppLanguage)
                        }
                    ) ?: backStack.add(Screen.AppLanguage)
                }

                MoreNavEvents.NavigateToDownloadGuide -> {
                    interstitialAdGate?.showForScreen(
                        activity = activity,
                        screenKey = VideoDownloaderAdScreenKeys.DOWNLOAD_GUIDE,
                        onComplete = {
                            backStack.add(Screen.DownloadGuide)
                        }
                    ) ?: backStack.add(Screen.DownloadGuide)
                }
            }
        }
    }

    LaunchedEffect(viewModel.uiEffects, context) {
        viewModel.uiEffects.collect { effect ->
            when (effect) {
                MoreUiEffect.ShareApp -> {
                    context.shareApp()
                }

                MoreUiEffect.OpenRatePage -> {
                    context.openRatePage()
                }

                MoreUiEffect.OpenCustomFolderPicker -> {
                    customFolderLauncher.launch(null)
                }

                is MoreUiEffect.ShowMessage -> {
                    Toast.makeText(
                        context,
                        effect.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                is MoreUiEffect.SendFeedbackEmail -> {
                    context.sendFeedbackEmail(
                        toEmail = effect.toEmail,
                        subject = effect.subject,
                        body = effect.body,
                        chooserTitle = effect.chooserTitle
                    )
                }

                is MoreUiEffect.OpenPrivacyPolicy -> {
                    context.openExternalUrl(
                        url = effect.url,
                        fallbackMessage = "Unable to open Privacy Policy."
                    )
                }
            }
        }
    }

    MoreSRC(
        state = state,
        onEvent = viewModel::onEvent
    )
}

private fun Context.shareApp() {
    val appLink = "https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"

    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Video Downloader")
        putExtra(
            Intent.EXTRA_TEXT,
            "Download Video Downloader app:\n$appLink"
        )
    }

    startActivity(
        Intent.createChooser(sendIntent, "Share app").withNewTaskIfNeeded(this)
    )
}

private fun Context.openRatePage() {
    val packageName = BuildConfig.APPLICATION_ID

    val marketIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("market://details?id=$packageName")
    ).withNewTaskIfNeeded(this)

    val webIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
    ).withNewTaskIfNeeded(this)

    runCatching {
        startActivity(marketIntent)
    }.getOrElse {
        startActivity(webIntent)
    }
}

private fun Context.persistTreeUriPermission(uri: Uri) {
    val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION

    runCatching {
        contentResolver.takePersistableUriPermission(uri, flags)
    }
}

private fun Context.isRemovableStorageAvailable(): Boolean {
    return ContextCompat.getExternalFilesDirs(this, null)
        .filterNotNull()
        .any { file ->
            Environment.isExternalStorageRemovable(file)
        }
}

private fun Intent.withNewTaskIfNeeded(context: Context): Intent {
    if (context !is Activity) {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    return this
}

private fun Context.sendFeedbackEmail(
    toEmail: String,
    subject: String,
    body: String,
    chooserTitle: String,
) {
    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
        data = "mailto:${Uri.encode(toEmail)}".toUri()
        putExtra(Intent.EXTRA_EMAIL, arrayOf(toEmail))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }.withNewTaskIfNeeded(this)

    val chooserIntent = Intent.createChooser(
        emailIntent,
        chooserTitle
    ).withNewTaskIfNeeded(this)

    runCatching {
        startActivity(chooserIntent)
    }.getOrElse {
        Toast.makeText(
            this,
            "No email app found.",
            Toast.LENGTH_SHORT
        ).show()
    }
}

private fun Context.openExternalUrl(
    url: String,
    fallbackMessage: String,
) {
    val safeUrl = url.trim()

    if (safeUrl.isBlank()) {
        Toast.makeText(
            this,
            fallbackMessage,
            Toast.LENGTH_SHORT
        ).show()
        return
    }

    val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse(safeUrl)
    ).withNewTaskIfNeeded(this)

    runCatching {
        startActivity(intent)
    }.getOrElse {
        Toast.makeText(
            this,
            fallbackMessage,
            Toast.LENGTH_SHORT
        ).show()
    }
}
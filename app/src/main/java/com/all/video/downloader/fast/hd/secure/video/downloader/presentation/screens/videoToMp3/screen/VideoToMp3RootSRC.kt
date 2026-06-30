package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.navigation.Screen
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3.events.VideoToMp3Events
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3.events.VideoToMp3NavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.videoToMp3.viewModel.VideoToMp3ViewModel
import com.core.ads.ui.rememberAdsActivity
import com.core.ads.ui.rememberInterstitialAdGate
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdFeatureKeys
import androidx.activity.compose.BackHandler
import com.core.ads.ui.rememberInterstitialBackNavigationAction
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys.VideoDownloaderAdScreenKeys

@Composable
fun VideoToMp3RootSRC(
    backStack: NavBackStack<NavKey>,
    viewModel: VideoToMp3ViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    val navigateBackWithInterstitial = rememberInterstitialBackNavigationAction(
        screenKey = VideoDownloaderAdScreenKeys.VIDEO_TO_MP3
    ) {
        backStack.removeLastOrNull()
    }

    BackHandler {
        viewModel.onEvent(VideoToMp3Events.BackClicked)
    }

    val activity = rememberAdsActivity()
    val interstitialAdGate = rememberInterstitialAdGate()

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        context.takePersistableVideoPermission(uri)

        viewModel.onEvent(
            VideoToMp3Events.VideoSelected(
                uri = uri,
                fileName = context.queryDisplayName(uri)
            )
        )
    }

    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect { event ->
            when (event) {
                VideoToMp3NavEvents.NavigateBack -> {
                    navigateBackWithInterstitial()
                }
                VideoToMp3NavEvents.LaunchVideoPicker -> {
                    pickerLauncher.launch(arrayOf("video/*"))
                }

                is VideoToMp3NavEvents.NavigateToResult -> {
                    interstitialAdGate?.showForFeature(
                        activity = activity,
                        featureKey = VideoDownloaderAdFeatureKeys.VIDEO_TO_MP3_RESULT,
                        onComplete = {
                            backStack.add(
                                Screen.VideoToMp3Result(
                                    conversionId = event.conversionId
                                )
                            )
                        }
                    ) ?: backStack.add(
                        Screen.VideoToMp3Result(
                            conversionId = event.conversionId
                        )
                    )
                }
            }
        }
    }

    VideoToMp3SRC(
        state = state,
        onEvent = viewModel::onEvent
    )
}

private fun Context.takePersistableVideoPermission(uri: Uri) {
    runCatching {
        contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }
}

private fun Context.queryDisplayName(uri: Uri): String {
    val projection = arrayOf(OpenableColumns.DISPLAY_NAME)

    contentResolver.query(
        uri,
        projection,
        null,
        null,
        null
    )?.use { cursor ->
        val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

        if (cursor.moveToFirst() && index != -1) {
            return cursor.getString(index).orEmpty()
        }
    }

    return "selected_video.mp4"
}
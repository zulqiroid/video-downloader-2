package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.screen

import android.app.Activity
import android.content.ContentUris
 import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.componants.AudioPlayerItem
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.componants.VideoPlayerItem
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.events.MediaPlayerEvent
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.events.MediaPlayerNavEvent
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.events.VideoOptionsIntent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerState
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.viewModel.MediaPlayerViewModel
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.componants.MediaPlayerOptionsSheet
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.componants.MediaPlayerPlaybackSpeedSheet
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.util.Log
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.componants.MediaPlayerEqualizerSheet
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerOrientationMode
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.componants.MediaPlayerSubtitleSheet
import android.app.PictureInPictureParams
import android.content.pm.PackageManager
import android.util.Rational
import androidx.activity.ComponentActivity
 import androidx.activity.result.IntentSenderRequest.*
import androidx.compose.runtime.DisposableEffect
import androidx.core.app.PictureInPictureModeChangedInfo
import androidx.core.util.Consumer
import com.all.video.downloader.fast.hd.secure.video.downloader.core.MainActivity
import android.app.PendingIntent
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.Icon
import androidx.annotation.RequiresApi
import androidx.compose.runtime.rememberUpdatedState
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.events.MediaPlayerPictureInPictureAction

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MediaPlayerScreen(
    mediaList: List<MediaFile>,
    startIndex: Int,
    backStack: NavBackStack<NavKey>,
    viewModel: MediaPlayerViewModel = hiltViewModel(),

) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val activity = LocalActivity.current


    MediaPlayerPictureInPictureModeEffect(
        activity = activity,
        onModeChanged = { isInPictureInPictureMode ->
            viewModel.onEvent(
                MediaPlayerEvent.OnPictureInPictureModeChanged(
                    isInPictureInPictureMode = isInPictureInPictureMode
                )
            )
        }
    )

    MediaPlayerUserLeaveHintEffect(
        activity = activity,
        onUserLeaveHint = {
            viewModel.onEvent(MediaPlayerEvent.OnUserLeaveHint)
        }
    )

    MediaPlayerPictureInPictureActionEffect(
        activity = activity,
        onAction = { action ->
            viewModel.onEvent(
                MediaPlayerEvent.OnPictureInPictureAction(action)
            )
        }
    )

    LaunchedEffect(
        activity,
        state.isInPictureInPictureMode,
        state.isPlaying,
        state.isPlaybackEnded,
        state.currentIndex,
        state.mediaList,
        state.isShuffleEnabled,
        state.shuffleQueue,
        state.shuffleQueuePosition
    ) {
        if (state.isInPictureInPictureMode) {
            activity?.updateMediaPlayerPictureInPictureParams(state)
        }
    }

    val configuration = LocalConfiguration.current
    val isDeviceLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val isLandscape = when (state.orientationMode) {
        MediaPlayerOrientationMode.Portrait -> false
        MediaPlayerOrientationMode.Landscape -> true
    }

    MediaPlayerOrientationEffect(
        activity = activity,
        orientationMode = state.orientationMode,
        isLandscape = isLandscape
    )

    fun rotatePlayer() {
        viewModel.onEvent(
            MediaPlayerEvent.OnRotateClicked(
                isCurrentlyLandscape = isLandscape
            )
        )
    }

    fun exitPlayer() {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        viewModel.onEvent(MediaPlayerEvent.OnBackPressed)
        backStack.removeLastOrNull()
    }



    val screenBackgroundColor = Color(state.screenBackgroundColor)



    val writePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onBottomSheetIntent(
                VideoOptionsIntent.OnRenamePermissionGranted
            )
        } else {
            viewModel.onBottomSheetIntent(
                VideoOptionsIntent.OnRenamePermissionDenied
            )
        }
    }

    val subtitlePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            viewModel.onBottomSheetIntent(
                VideoOptionsIntent.OnSubtitlePicked(
                    uriString = uri.toString()
                )
            )
        } else {
            viewModel.onBottomSheetIntent(
                VideoOptionsIntent.OnSubtitlePickerCancelled
            )
        }
    }

    val deletePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.onBottomSheetIntent(
                VideoOptionsIntent.OnDeletePermissionGranted
            )
        } else {
            viewModel.onBottomSheetIntent(
                VideoOptionsIntent.OnDeletePermissionDenied
            )
        }
    }

    val writeSettingsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        viewModel.onBottomSheetIntent(
            VideoOptionsIntent.OnWriteSettingsPermissionReturned
        )
    }

    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect { event ->
            when (event) {
                is MediaPlayerNavEvent.RequestMediaWritePermission -> {
                    val currentActivity = activity ?: return@collect

                    val intentSender = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        MediaStore.createWriteRequest(
                            currentActivity.contentResolver,
                            listOf(event.uri)
                        ).intentSender
                    } else {
                        event.pendingIntent?.intentSender
                    }

                    if (intentSender != null) {
                        writePermissionLauncher.launch(
                            Builder(intentSender).build()
                        )
                    } else {
                        viewModel.onBottomSheetIntent(
                            VideoOptionsIntent.OnRenamePermissionDenied
                        )
                    }
                }

                is MediaPlayerNavEvent.RequestMediaDeletePermission -> {
                    val currentActivity = activity ?: return@collect

                    val intentSender = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        MediaStore.createDeleteRequest(
                            currentActivity.contentResolver,
                            listOf(event.uri)
                        ).intentSender
                    } else {
                        event.pendingIntent?.intentSender
                    }

                    if (intentSender != null) {
                        deletePermissionLauncher.launch(
                            Builder(intentSender).build()
                        )
                    } else {
                        viewModel.onBottomSheetIntent(
                            VideoOptionsIntent.OnDeletePermissionDenied
                        )
                    }
                }

                is MediaPlayerNavEvent.ShareMediaFile -> {
                    activity?.shareMediaFile(event.mediaFile)
                }

                MediaPlayerNavEvent.CloseMediaPlayer -> {
                    backStack.removeLastOrNull()
                }

                MediaPlayerNavEvent.RequestWriteSettingsPermission -> {
                    val currentActivity = activity ?: return@collect

                    val intent = Intent(
                        Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:${currentActivity.packageName}")
                    )

                    writeSettingsPermissionLauncher.launch(intent)
                }
                MediaPlayerNavEvent.RequestSubtitlePicker -> {
                    subtitlePickerLauncher.launch(SUPPORTED_SUBTITLE_MIME_TYPES)
                }

                MediaPlayerNavEvent.RequestEnterPictureInPicture -> {
                    val didEnterPictureInPicture = activity
                        ?.enterMediaPlayerPictureInPicture(state)
                        ?: false

                    if (!didEnterPictureInPicture) {
                        viewModel.onEvent(MediaPlayerEvent.OnPictureInPictureEnterFailed)
                    }
                }
            }
        }
    }

    LaunchedEffect(mediaList, startIndex) {
        viewModel.onEvent(
            MediaPlayerEvent.Load(
                mediaList = mediaList,
                startIndex = startIndex
            )
        )
    }

    if (state.mediaList.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )
        return
    }

    val canShowAds = !state.isPremiumUser


    val pagerItems = remember(
        state.mediaList,
        state.isPremiumUser,
    ) {
        buildMediaPlayerPagerItems(
            mediaList = state.mediaList,
         )
    }

    val initialPagerPage = remember(
        pagerItems,
        startIndex
    ) {
        pagerItems.pageIndexForMediaIndex(startIndex)
    }
    val pagerState = rememberPagerState(
        initialPage = initialPagerPage,
        pageCount = { pagerItems.size }
    )

    val isFullPageNativeAdVisible = pagerItems
        .getOrNull(pagerState.currentPage) is MediaPlayerPagerItem.NativeAd

    val shouldShowMediaPlayerBanner =
        canShowAds &&
                !isLandscape &&
                !isFullPageNativeAdVisible &&
                !state.isInPictureInPictureMode


    LaunchedEffect(state.currentIndex, pagerItems) {
        val targetPage = pagerItems.pageIndexForMediaIndex(state.currentIndex)

        if (pagerState.currentPage != targetPage) {
            pagerState.animateScrollToPage(targetPage)
        }
    }

    LaunchedEffect(pagerState.currentPage, pagerItems) {
        when (val item = pagerItems.getOrNull(pagerState.currentPage)) {
            is MediaPlayerPagerItem.Media -> {
                viewModel.onEvent(
                    MediaPlayerEvent.OnPageChanged(item.mediaIndex)
                )
            }

            is MediaPlayerPagerItem.NativeAd -> {
                viewModel.onEvent(MediaPlayerEvent.OnNativeAdPageVisible)
            }

            null -> Unit
        }
    }

    BackHandler {
        when {
            state.isInPictureInPictureMode -> Unit

            state.showSubtitleDialog -> {
                viewModel.onBottomSheetIntent(
                    VideoOptionsIntent.OnSubtitleDialogDismissed
                )
            }

            state.showEqualizerDialog -> {
                viewModel.onBottomSheetIntent(
                    VideoOptionsIntent.OnEqualizerDialogDismissed
                )
            }

            state.showPlaybackSpeedDialog -> {
                viewModel.onBottomSheetIntent(
                    VideoOptionsIntent.OnPlaybackSpeedDialogDismissed
                )
            }

            state.showBottomSheet -> {
                viewModel.onBottomSheetIntent(VideoOptionsIntent.OnDismiss)
            }

            isLandscape -> {
                rotatePlayer()
            }

            else -> {
                exitPlayer()
            }
        }
    }


    if (isLandscape) {
         Box(
            modifier = Modifier
                .fillMaxSize()
                .background(screenBackgroundColor)
        ) {
            MediaPlayerContent(
                state = state,
                pagerItems = pagerItems,
                pagerState = pagerState,
                screenBackgroundColor = screenBackgroundColor,
                isLandscape = true,
                viewModel = viewModel,
                onRotateClick = ::rotatePlayer,
                exitPlayer = ::exitPlayer
            )
        }
    } else {

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = screenBackgroundColor,
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(paddingValues)
            ) {
                MediaPlayerContent(
                    state = state,
                    pagerItems = pagerItems,
                    pagerState = pagerState,
                    screenBackgroundColor = screenBackgroundColor,
                    isLandscape = false,
                    viewModel = viewModel,
                    onRotateClick = ::rotatePlayer,
                    exitPlayer = ::exitPlayer
                )
            }
        }
    }
}

private fun Activity.shareMediaFile(
    mediaFile: MediaFile
) {
    val uri = mediaFile.toMediaStoreUri()

    val mimeType = if (mediaFile.isVideo) {
        "video/*"
    } else {
        "audio/*"
    }

    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = mimeType
        putExtra(Intent.EXTRA_STREAM, uri)
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }

    val chooser = Intent.createChooser(
        shareIntent,
        "Share ${mediaFile.fileName}"
    )

    if (shareIntent.resolveActivity(packageManager) != null) {
        startActivity(chooser)
    }
}

private fun MediaFile.toMediaStoreUri(): Uri {
    val collectionUri = if (isVideo) {
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI
    } else {
        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
    }

    return ContentUris.withAppendedId(collectionUri, id)
}




private sealed interface MediaPlayerPagerItem {

    data class Media(
        val media: MediaFile,
        val mediaIndex: Int
    ) : MediaPlayerPagerItem

    data class NativeAd(
        val slotKey: String
    ) : MediaPlayerPagerItem
}

private fun buildMediaPlayerPagerItems(
    mediaList: List<MediaFile>,
): List<MediaPlayerPagerItem> {
    if (mediaList.isEmpty()) return emptyList()

    val items = mutableListOf<MediaPlayerPagerItem>()


    mediaList.forEachIndexed { index, media ->
        items += MediaPlayerPagerItem.Media(
            media = media,
            mediaIndex = index
        )

     }

    return items
}

private fun List<MediaPlayerPagerItem>.pageIndexForMediaIndex(
    mediaIndex: Int
): Int {
    return indexOfFirst { item ->
        item is MediaPlayerPagerItem.Media && item.mediaIndex == mediaIndex
    }.coerceAtLeast(0)
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MediaPlayerContent(
    state: MediaPlayerState,
    pagerItems: List<MediaPlayerPagerItem>,
    pagerState: PagerState,
    screenBackgroundColor: Color,
    isLandscape: Boolean,
    viewModel: MediaPlayerViewModel,
    onRotateClick: () -> Unit,
    exitPlayer: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 0
        ) { page ->

            when (val item = pagerItems[page]) {
                is MediaPlayerPagerItem.Media -> {
                    val media = item.media
                    val isCurrentPage = state.currentIndex == item.mediaIndex

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(screenBackgroundColor)
                    ) {
                        if (isCurrentPage) {
                            if (media.isVideo) {
                                VideoPlayerItem(
                                    media = media,
                                    viewModel = viewModel,
                                    isLandscape = isLandscape,
                                    onBack = {
                                        if (isLandscape) {
                                            onRotateClick()
                                        } else {
                                            exitPlayer()
                                        }
                                    },
                                    onRotateClick = onRotateClick,
                                    onPictureInPictureClick = {
                                        viewModel.onEvent(MediaPlayerEvent.OnPictureInPictureClicked)
                                    },
                                    onThreeDotsClick = {
                                        viewModel.onEvent(MediaPlayerEvent.OnThreeDotsClick)
                                    }
                                )
                            } else {
                                AudioPlayerItem(
                                    media = media,
                                    viewModel = viewModel,
                                    onBack = {
                                        exitPlayer()
                                    },
                                    onPlayPause = {
                                        viewModel.onEvent(MediaPlayerEvent.OnPlayPauseClicked)
                                    },
                                    onForward = {
                                        viewModel.onEvent(MediaPlayerEvent.OnForwardClicked)
                                    },
                                    onRewind = {
                                        viewModel.onEvent(MediaPlayerEvent.OnRewindClicked)
                                    },
                                    onNext = {
                                        viewModel.onEvent(MediaPlayerEvent.OnNextClicked)
                                    },
                                    onPrevious = {
                                        viewModel.onEvent(MediaPlayerEvent.OnPreviousClicked)
                                    },
                                    onSeek = {
                                        viewModel.onEvent(MediaPlayerEvent.OnSeek(it))
                                    },
                                    onMuteToggle = {
                                        viewModel.onEvent(MediaPlayerEvent.OnMuteToggleClicked)
                                    },
                                    onVolumeChange = {
                                        viewModel.onEvent(MediaPlayerEvent.OnVolumeChanged(it))
                                    },
                                    onThreeDotsClick = {
                                        viewModel.onEvent(MediaPlayerEvent.OnThreeDotsClick)
                                    },
                                    onShuffleClick = {
                                        viewModel.onEvent(MediaPlayerEvent.OnShuffleClicked)
                                    },
                                    onRepeatClick = {
                                        viewModel.onEvent(MediaPlayerEvent.OnAudioRepeatClicked)
                                    },
                                )
                            }
                        }
                    }
                }

                is MediaPlayerPagerItem.NativeAd -> {

                }
            }
        }

        val currentMedia = state.mediaList.getOrNull(state.currentIndex)


        if (state.showBottomSheet && currentMedia != null) {
            MediaPlayerOptionsSheet(
                state = state,
                media = currentMedia,
                onIntent = viewModel::onBottomSheetIntent
            )
        }

        if (state.showPlaybackSpeedDialog) {
            MediaPlayerPlaybackSpeedSheet(
                currentSpeed = state.playbackSpeed,
                onSpeedSelected = { speed ->
                    viewModel.onBottomSheetIntent(
                        VideoOptionsIntent.OnPlaybackSpeedSelected(speed)
                    )
                },
                onResetClicked = {
                    viewModel.onBottomSheetIntent(
                        VideoOptionsIntent.OnPlaybackSpeedResetClicked
                    )
                },
                onDismiss = {
                    viewModel.onBottomSheetIntent(
                        VideoOptionsIntent.OnPlaybackSpeedDialogDismissed
                    )
                }
            )
        }
        if (state.showEqualizerDialog) {
            MediaPlayerEqualizerSheet(
                equalizerState = state.equalizerState,
                onIntent = viewModel::onBottomSheetIntent
            )
        }
        if (state.showSubtitleDialog) {
            MediaPlayerSubtitleSheet(
                subtitleState = state.subtitleState,
                onIntent = viewModel::onBottomSheetIntent
            )
        }
    }
}

@Composable
private fun MediaPlayerOrientationEffect(
    activity: Activity?,
    orientationMode: MediaPlayerOrientationMode,
    isLandscape: Boolean,
) {
    val view = LocalView.current

    DisposableEffect(activity) {
        val currentActivity = activity
        val window = currentActivity?.window
        val originalOrientation = currentActivity?.requestedOrientation
            ?: ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        onDispose {
            currentActivity?.requestedOrientation = originalOrientation

            if (window != null) {
                val controller = WindowCompat.getInsetsController(window, view)
                controller.show(WindowInsetsCompat.Type.systemBars())
                controller.isAppearanceLightStatusBars = true
                controller.isAppearanceLightNavigationBars = true
            }
        }
    }

    LaunchedEffect(activity, orientationMode) {
        activity?.requestedOrientation = orientationMode.toRequestedOrientation()
    }

    LaunchedEffect(activity, isLandscape) {
        val window = activity?.window ?: return@LaunchedEffect
        val controller = WindowCompat.getInsetsController(window, view)

        if (isLandscape) {
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.isAppearanceLightStatusBars = false
            controller.isAppearanceLightNavigationBars = false
            controller.hide(WindowInsetsCompat.Type.systemBars())
        } else {
            controller.show(WindowInsetsCompat.Type.systemBars())
            controller.isAppearanceLightStatusBars = true
            controller.isAppearanceLightNavigationBars = true
        }
    }
}

private fun MediaPlayerOrientationMode.toRequestedOrientation(): Int {
    return when (this) {
        MediaPlayerOrientationMode.Portrait -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        MediaPlayerOrientationMode.Landscape -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
}

@Composable
private fun MediaPlayerPictureInPictureModeEffect(
    activity: Activity?,
    onModeChanged: (Boolean) -> Unit,
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    DisposableEffect(activity) {
        val componentActivity = activity as? ComponentActivity

        if (componentActivity == null) {
            onDispose { }
        } else {
            val listener = Consumer<PictureInPictureModeChangedInfo> { info ->
                onModeChanged(info.isInPictureInPictureMode)
            }

            componentActivity.addOnPictureInPictureModeChangedListener(listener)

            onDispose {
                componentActivity.removeOnPictureInPictureModeChangedListener(listener)
            }
        }
    }
}


@Composable
private fun MediaPlayerUserLeaveHintEffect(
    activity: Activity?,
    onUserLeaveHint: () -> Unit,
) {
    val mainActivity = activity as? MainActivity ?: return

    LaunchedEffect(mainActivity) {
        mainActivity.userLeaveHints.collect {
            onUserLeaveHint()
        }
    }
}

private fun Activity.enterMediaPlayerPictureInPicture(
    state: MediaPlayerState,
): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return false

    val currentMedia = state.mediaList.getOrNull(state.currentIndex)
    if (currentMedia?.isVideo != true) return false

    val hasPictureInPictureFeature = packageManager.hasSystemFeature(
        PackageManager.FEATURE_PICTURE_IN_PICTURE
    )

    if (!hasPictureInPictureFeature) return false

    return runCatching {
        enterPictureInPictureMode(
            buildMediaPlayerPictureInPictureParams(state)
        )
    }.getOrDefault(false)
}

private fun Activity.updateMediaPlayerPictureInPictureParams(
    state: MediaPlayerState,
) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val currentMedia = state.mediaList.getOrNull(state.currentIndex)
    if (currentMedia?.isVideo != true) return

    runCatching {
        setPictureInPictureParams(
            buildMediaPlayerPictureInPictureParams(state)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Activity.buildMediaPlayerPictureInPictureParams(
    state: MediaPlayerState,
): PictureInPictureParams {
    val paramsBuilder = PictureInPictureParams.Builder()
        .setAspectRatio(
            Rational(
                PIP_ASPECT_RATIO_WIDTH,
                PIP_ASPECT_RATIO_HEIGHT
            )
        )
        .setActions(
            buildMediaPlayerPictureInPictureActions(state)
        )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        paramsBuilder.setSeamlessResizeEnabled(true)
    }

    return paramsBuilder.build()
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Activity.buildMediaPlayerPictureInPictureActions(
    state: MediaPlayerState,
): List<RemoteAction> {
    val canPlayPrevious = state.peekPreviousPictureInPictureVideoIndex() != null
    val canPlayNext = state.peekNextPictureInPictureVideoIndex() != null

    return listOf(
        createPictureInPictureRemoteAction(
            action = MediaPlayerPictureInPictureAction.Previous,
            iconRes = android.R.drawable.ic_media_previous,
            title = getString(R.string.media_player_pip_action_previous),
            enabled = canPlayPrevious
        ),
        createPictureInPictureRemoteAction(
            action = MediaPlayerPictureInPictureAction.PlayPause,
            iconRes = if (state.isPlaying && !state.isPlaybackEnded) {
                android.R.drawable.ic_media_pause
            } else {
                android.R.drawable.ic_media_play
            },
            title = if (state.isPlaying && !state.isPlaybackEnded) {
                getString(R.string.media_player_pip_action_pause)
            } else {
                getString(R.string.media_player_pip_action_play)
            },
            enabled = true
        ),
        createPictureInPictureRemoteAction(
            action = MediaPlayerPictureInPictureAction.Next,
            iconRes = android.R.drawable.ic_media_next,
            title = getString(R.string.media_player_pip_action_next),
            enabled = canPlayNext
        )
    )
}

@RequiresApi(Build.VERSION_CODES.O)
private fun Activity.createPictureInPictureRemoteAction(
    action: MediaPlayerPictureInPictureAction,
    iconRes: Int,
    title: String,
    enabled: Boolean,
): RemoteAction {
    val pendingIntent = PendingIntent.getBroadcast(
        this,
        action.toPendingIntentRequestCode(),
        Intent(action.toBroadcastAction()).setPackage(packageName),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    return RemoteAction(
        Icon.createWithResource(this, iconRes),
        title,
        title,
        pendingIntent
    ).apply {
        isEnabled = enabled
    }
}

@Composable
private fun MediaPlayerPictureInPictureActionEffect(
    activity: Activity?,
    onAction: (MediaPlayerPictureInPictureAction) -> Unit,
) {
    val currentOnAction = rememberUpdatedState(onAction)

    DisposableEffect(activity) {
        val currentActivity = activity

        if (currentActivity == null) {
            onDispose { }
        } else {
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(
                    context: Context?,
                    intent: Intent?,
                ) {
                    val action = intent?.action
                        ?.toPictureInPictureActionOrNull()
                        ?: return

                    currentOnAction.value(action)
                }
            }

            val filter = IntentFilter().apply {
                addAction(MediaPlayerPictureInPictureAction.PlayPause.toBroadcastAction())
                addAction(MediaPlayerPictureInPictureAction.Previous.toBroadcastAction())
                addAction(MediaPlayerPictureInPictureAction.Next.toBroadcastAction())
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                currentActivity.registerReceiver(
                    receiver,
                    filter,
                    Context.RECEIVER_NOT_EXPORTED
                )
            } else {
                @Suppress("DEPRECATION")
                currentActivity.registerReceiver(
                    receiver,
                    filter
                )
            }

            onDispose {
                runCatching {
                    currentActivity.unregisterReceiver(receiver)
                }
            }
        }
    }
}

private fun MediaPlayerPictureInPictureAction.toBroadcastAction(): String {
    return when (this) {
        MediaPlayerPictureInPictureAction.PlayPause -> ACTION_PIP_PLAY_PAUSE
        MediaPlayerPictureInPictureAction.Previous -> ACTION_PIP_PREVIOUS
        MediaPlayerPictureInPictureAction.Next -> ACTION_PIP_NEXT
    }
}

private fun String.toPictureInPictureActionOrNull(): MediaPlayerPictureInPictureAction? {
    return when (this) {
        ACTION_PIP_PLAY_PAUSE -> MediaPlayerPictureInPictureAction.PlayPause
        ACTION_PIP_PREVIOUS -> MediaPlayerPictureInPictureAction.Previous
        ACTION_PIP_NEXT -> MediaPlayerPictureInPictureAction.Next
        else -> null
    }
}

private fun MediaPlayerPictureInPictureAction.toPendingIntentRequestCode(): Int {
    return when (this) {
        MediaPlayerPictureInPictureAction.PlayPause -> PIP_REQUEST_CODE_PLAY_PAUSE
        MediaPlayerPictureInPictureAction.Previous -> PIP_REQUEST_CODE_PREVIOUS
        MediaPlayerPictureInPictureAction.Next -> PIP_REQUEST_CODE_NEXT
    }
}

private fun MediaPlayerState.peekNextPictureInPictureVideoIndex(): Int? {
    val nextIndex = if (isShuffleEnabled) {
        shuffleQueue.getOrNull(shuffleQueuePosition + 1)
    } else {
        currentIndex + 1
    }

    return nextIndex
        ?.takeIf { index -> index in mediaList.indices }
        ?.takeIf { index -> mediaList[index].isVideo }
}

private fun MediaPlayerState.peekPreviousPictureInPictureVideoIndex(): Int? {
    val previousIndex = if (isShuffleEnabled) {
        shuffleQueue.getOrNull(shuffleQueuePosition - 1)
    } else {
        currentIndex - 1
    }

    return previousIndex
        ?.takeIf { index -> index in mediaList.indices }
        ?.takeIf { index -> mediaList[index].isVideo }
}

private const val ACTION_PIP_PLAY_PAUSE =
    "com.all.video.downloader.fast.hd.secure.video.downloader.action.PIP_PLAY_PAUSE"

private const val ACTION_PIP_PREVIOUS =
    "com.all.video.downloader.fast.hd.secure.video.downloader.action.PIP_PREVIOUS"

private const val ACTION_PIP_NEXT =
    "com.all.video.downloader.fast.hd.secure.video.downloader.action.PIP_NEXT"

private const val PIP_REQUEST_CODE_PLAY_PAUSE = 5101
private const val PIP_REQUEST_CODE_PREVIOUS = 5102
private const val PIP_REQUEST_CODE_NEXT = 5103

private const val PIP_ASPECT_RATIO_WIDTH = 16
private const val PIP_ASPECT_RATIO_HEIGHT = 9
private val SUPPORTED_SUBTITLE_MIME_TYPES = arrayOf(
    "application/x-subrip",
    "text/srt",
    "text/plain",
    "text/vtt",
    "text/*",
    "application/ttml+xml",
    "application/octet-stream",
    "*/*"
)
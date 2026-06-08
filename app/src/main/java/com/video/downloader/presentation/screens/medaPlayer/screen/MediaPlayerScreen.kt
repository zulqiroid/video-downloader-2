package com.video.downloader.presentation.screens.medaPlayer.screen

import android.app.Activity
import android.content.ContentUris
import android.content.Intent
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
import com.video.downloader.presentation.screens.medaPlayer.componants.AudioPlayerItem
import com.video.downloader.presentation.screens.medaPlayer.componants.VideoPlayerItem
import com.video.downloader.presentation.screens.medaPlayer.events.MediaPlayerEvent
import com.video.downloader.presentation.screens.medaPlayer.events.MediaPlayerNavEvent
import com.video.downloader.presentation.screens.medaPlayer.events.VideoOptionsIntent
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
import com.video.downloader.domain.models.MediaFile
import com.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerState
import com.video.downloader.presentation.screens.medaPlayer.viewModel.MediaPlayerViewModel

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

    var isLandscapeMode by remember {
        mutableStateOf(false)
    }

    val configuration = LocalConfiguration.current
    val isDeviceLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isLandscape = isLandscapeMode || isDeviceLandscape

    fun setRequestedOrientation(orientation: Int) {
        activity?.requestedOrientation = orientation
    }

    fun enterLandscape() {
        isLandscapeMode = true
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }

    fun enterPortrait() {
        isLandscapeMode = false
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
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
                            IntentSenderRequest.Builder(intentSender).build()
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
                            IntentSenderRequest.Builder(intentSender).build()
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
        canShowAds && !isLandscape && !isFullPageNativeAdVisible


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
        if (isLandscape) {
            enterPortrait()
        } else {
            exitPlayer()
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
                enterPortrait = ::enterPortrait,
                enterLandscape = ::enterLandscape,
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
                    enterPortrait = ::enterPortrait,
                    enterLandscape = ::enterLandscape,
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
    enterPortrait: () -> Unit,
    enterLandscape: () -> Unit,
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
                                            enterPortrait()
                                        } else {
                                            exitPlayer()
                                        }
                                    },
                                    onRotateClick = {
                                        if (isLandscape) {
                                            enterPortrait()
                                        } else {
                                            enterLandscape()
                                        }
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


    }
}
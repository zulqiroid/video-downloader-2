package com.video.downloader.presentation.screens.videoSplitter.screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.video.downloader.presentation.screens.videoSplitter.events.VideoSplitterEvents
import com.video.downloader.presentation.screens.videoSplitter.events.VideoSplitterNavEvents
import com.video.downloader.presentation.screens.videoSplitter.viewModel.VideoSplitterViewModel

@Composable
fun VideoSplitterRootSRC(
    backStack: NavBackStack<NavKey>,
    viewModel: VideoSplitterViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsStateWithLifecycle()

    var pendingSaveClipId by remember {
        mutableStateOf<String?>(null)
    }

    val legacyStoragePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        val clipId = pendingSaveClipId
        pendingSaveClipId = null

        if (granted && clipId != null) {
            viewModel.onEvent(
                VideoSplitterEvents.SaveClipClicked(
                    clipId = clipId
                )
            )
        } else {
            viewModel.onEvent(VideoSplitterEvents.SavePermissionDenied)
        }
    }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        context.takePersistableVideoPermission(uri)

        viewModel.onEvent(
            VideoSplitterEvents.VideoSelected(
                uri = uri,
                fileName = context.queryDisplayName(uri),
                durationMs = context.queryVideoDurationMs(uri),
                sizeBytes = context.queryFileSize(uri)
            )
        )
    }

    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect { event ->
            when (event) {
                VideoSplitterNavEvents.NavigateBack -> {
                    backStack.removeLastOrNull()
                }

                VideoSplitterNavEvents.LaunchVideoPicker -> {
                    pickerLauncher.launch(arrayOf("video/*"))
                }
            }
        }
    }

    VideoSplitterSRC(
        state = state,
        onEvent = { event ->
            when (event) {
                is VideoSplitterEvents.SaveClipClicked -> {
                    if (context.needsLegacyWritePermission()) {
                        pendingSaveClipId = event.clipId
                        legacyStoragePermissionLauncher.launch(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                    } else {
                        viewModel.onEvent(event)
                    }
                }

                else -> {
                    viewModel.onEvent(event)
                }
            }
        }
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
    contentResolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME),
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

private fun Context.queryFileSize(uri: Uri): Long {
    contentResolver.query(
        uri,
        arrayOf(OpenableColumns.SIZE),
        null,
        null,
        null
    )?.use { cursor ->
        val index = cursor.getColumnIndex(OpenableColumns.SIZE)

        if (cursor.moveToFirst() && index != -1) {
            return cursor.getLong(index).coerceAtLeast(0L)
        }
    }

    return 0L
}

private fun Context.queryVideoDurationMs(uri: Uri): Long {
    return runCatching {
        val retriever = MediaMetadataRetriever()

        try {
            retriever.setDataSource(this, uri)

            retriever.extractMetadata(
                MediaMetadataRetriever.METADATA_KEY_DURATION
            )?.toLongOrNull() ?: 0L
        } finally {
            retriever.release()
        }
    }.getOrDefault(0L)
}

private fun Context.needsLegacyWritePermission(): Boolean {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) return false

    return ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) != PackageManager.PERMISSION_GRANTED
}
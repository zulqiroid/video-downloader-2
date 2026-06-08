package com.video.downloader.presentation.screens.player.screen

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.video.downloader.presentation.navigation.Screen
import com.video.downloader.presentation.navigation.Screen.*
import com.video.downloader.presentation.screens.main.states.MainStates
import com.video.downloader.presentation.screens.main.viewModel.MainViewModel
import com.video.downloader.presentation.screens.player.events.PlayerEvents
import com.video.downloader.presentation.screens.player.events.PlayerNavEvents
import com.video.downloader.presentation.screens.player.viewModel.PlayerViewModel


@Composable
fun PlayerRootSRC(
     mainViewModel: MainViewModel,
    mainPaddingValues : PaddingValues,
    backStack: NavBackStack<NavKey>,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val activity = LocalActivity.current
    val context = LocalContext.current
    val mediaPermissions = remember { requiredMediaPermissions() }
    val mediaPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val granted = result.values.all { it }

        val permanentlyDenied = activity?.isMediaPermissionPermanentlyDenied() == true

        viewModel.onEvent(
            PlayerEvents.OnMediaPermissionResult(
                granted = granted,
                permanentlyDenied = !granted && permanentlyDenied
            )
        )
    }

    LaunchedEffect(state.isMediaPermissionGranted) {
        if (state.isMediaPermissionGranted  ) {
            viewModel.onMediaPermissionGranted()
        } else {
            viewModel.onMediaPermissionDenied()
        }
    }

    LaunchedEffect(Unit) {
        val hasMediaPermission = context.hasMediaPermissions()

        viewModel.onEvent(
            PlayerEvents.OnMediaPermissionResult(
                granted = hasMediaPermission,
                permanentlyDenied = false
            )
        )

        if (!hasMediaPermission) {
            mediaPermissionLauncher.launch(mediaPermissions)
        }

    }

    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect {
            when(it) {
                is PlayerNavEvents.SendToMedia -> {
                    backStack.add(MediaPlayer(it.mediaList, it.startIndex))
                }

                PlayerNavEvents.NavigateToSettingScreen -> {
                    backStack.add(Screen.More)
                }
            }
        }
    }

    PlayerSRC(
         mainViewModel = mainViewModel,
        mainPaddingValues = mainPaddingValues,
         state = state,
        viewModel = viewModel
    )

}


private fun requiredMediaPermissions(): Array<String> {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
}

private fun Context.hasMediaPermissions(): Boolean {
    return requiredMediaPermissions().all { permission ->
        ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}

private fun Activity.isMediaPermissionPermanentlyDenied(): Boolean {
    return requiredMediaPermissions().any { permission ->
        !ActivityCompat.shouldShowRequestPermissionRationale(this, permission) &&
                ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED
    }
}
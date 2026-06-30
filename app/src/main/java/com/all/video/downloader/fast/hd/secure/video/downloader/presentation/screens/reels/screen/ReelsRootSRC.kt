package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.reels.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.navigation.Screen
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.main.viewModel.MainViewModel
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.reels.events.ReelsNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.reels.events.ReelsUiEffect
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.reels.viewModel.ReelsViewModel

@Composable
fun ReelsRootSRC(
    mainViewModel: MainViewModel,
    backStack: NavBackStack<NavKey>,
    viewModel: ReelsViewModel = hiltViewModel(),
    mainPaddingValues: PaddingValues,
    showPremiumLabel: Boolean,
    onPremiumClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect { event ->
            when (event) {
                ReelsNavEvents.NavigateToSettingScreen -> {
                    backStack.add(Screen.More)
                }
            }
        }
    }

    LaunchedEffect(viewModel.uiEffects, context) {
        viewModel.uiEffects.collect { effect ->
            when (effect) {
                is ReelsUiEffect.ShareReel -> {
                    context.shareText(
                        title = effect.title,
                        text = effect.text,
                        chooserTitle = effect.chooserTitle
                    )
                }

                is ReelsUiEffect.ShowMessage -> {
                    Toast.makeText(
                        context,
                        effect.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    ReelsSRC(
        mainPaddingValues = mainPaddingValues,
        state = state,
        viewModel = viewModel,
        showPremiumLabel = showPremiumLabel,
        onPremiumClick = onPremiumClick,
    )
}

private fun Context.shareText(
    title: String,
    text: String,
    chooserTitle: String
) {
    val sendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, text)
    }

    val chooserIntent = Intent.createChooser(
        sendIntent,
        chooserTitle
    )

    if (this !is Activity) {
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    startActivity(chooserIntent)
}
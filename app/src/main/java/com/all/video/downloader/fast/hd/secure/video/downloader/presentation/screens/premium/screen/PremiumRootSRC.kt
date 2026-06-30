package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.premium.screen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.premium.events.PremiumEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.premium.events.PremiumNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.premium.events.PremiumUiEffect
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.premium.viewModel.PremiumViewModel

@Composable
fun PremiumRootSRC(
    backStack: NavBackStack<NavKey>,
    viewModel: PremiumViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = LocalActivity.current

    LaunchedEffect(viewModel.navEvents) {
        viewModel.navEvents.collect { event ->
            when (event) {
                PremiumNavEvents.NavigateBack -> {
                    backStack.removeLastOrNull()
                }
            }
        }
    }

    LaunchedEffect(viewModel.uiEffects, context) {
        viewModel.uiEffects.collect { effect ->
            when (effect) {
                is PremiumUiEffect.ShowMessage -> {
                    Toast.makeText(
                        context,
                        effect.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is PremiumUiEffect.OpenUrl -> {
                    context.openPremiumUrl(
                        url = effect.url,
                        fallbackMessage = effect.fallbackMessage
                    )
                }
            }
        }
    }

    PremiumSRC(
        state = state,
        onEvent = { event ->
            when (event) {
                is PremiumEvents.ContinueClicked -> {
                    viewModel.onEvent(
                        PremiumEvents.ContinueClicked(
                            activity = activity
                        )
                    )
                }

                else -> viewModel.onEvent(event)
            }
        }
    )
}

private fun Context.openPremiumUrl(
    url: String,
    fallbackMessage: String
) {
    if (url.isBlank()) {
        Toast.makeText(this, fallbackMessage, Toast.LENGTH_SHORT).show()
        return
    }

    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

    if (this !is Activity) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    runCatching {
        startActivity(intent)
    }.getOrElse {
        Toast.makeText(this, fallbackMessage, Toast.LENGTH_SHORT).show()
    }
}
package com.all.video.downloader.fast.hd.secure.video.downloader.ads.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.core.ads.domain.manager.AdsManager
import org.koin.core.context.GlobalContext
import timber.log.Timber

@Composable
fun GlobalAdLoadingOverlay() {
    val adsManager = remember {
        runCatching {
            GlobalContext.get().get<AdsManager>()
        }.getOrElse { throwable ->
            Timber.w(throwable, "AdsManager unavailable. Ad loading overlay skipped.")
            null
        }
    } ?: return

    val state by adsManager.adLoadingUiState.collectAsStateWithLifecycle()

    if (!state.isVisible) return

    Dialog(
        onDismissRequest = {
            /**
             * Non-cancelable by design.
             * Duration is controlled by Remote Config and hard-capped in core:ads.
             */
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Card(
            modifier = Modifier
                .widthIn(min = 260.dp, max = 340.dp)
                .padding(horizontal = 24.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 12.dp
            )
        ) {
            Column(
                modifier = Modifier.padding(
                    horizontal = 24.dp,
                    vertical = 28.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = state.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
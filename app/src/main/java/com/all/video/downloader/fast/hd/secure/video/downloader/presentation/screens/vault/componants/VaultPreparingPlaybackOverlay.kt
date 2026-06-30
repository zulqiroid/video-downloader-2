package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun VaultPreparingPlaybackDialog() {
    Dialog(
        onDismissRequest = {
            // Non-dismissible while secure media is being prepared.
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppShapes.extraLarge)
                .background(AppColors.Background)
                .border(
                    width = 1.dp,
                    color = AppColors.BorderLight,
                    shape = AppShapes.extraLarge
                )
                .padding(horizontal = 26.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(82.dp)
                    .clip(CircleShape)
                    .background(AppGradients.HighlightVertical),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = AppColors.OnHighlight,
                    modifier = Modifier.size(42.dp)
                )
            }

            Spacer(modifier = Modifier.size(18.dp))

            Text(
                text = stringResource(R.string.opening_secure_media),
                style = AppTextStyles.titleLarge,
                color = AppColors.TextEnabled,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = stringResource(R.string.decrypting_your_vault_file_securely_and_preparing_playback),
                style = AppTextStyles.bodySmall.copy(
                    color = AppColors.TextDisabled
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(20.dp))

            CircularProgressIndicator(
                color = AppColors.HighlightGradientTop,
                strokeWidth = 3.dp,
                modifier = Modifier.size(42.dp)
            )

            Spacer(modifier = Modifier.size(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(AppShapes.large)
                    .background(AppColors.BackgroundDisabled)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.your_file_remains_protected_a_temporary_playback_copy_is_prepared_only_inside_app_cache),
                    style = AppTextStyles.bodySmall.copy(
                        color = AppColors.TextDisabled
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
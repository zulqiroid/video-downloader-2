package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.screenCasting.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppGradientButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppOutlinedButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenCastingNetworkWarningDialog(
    onOpenWifiClick: () -> Unit,
    onContinueAnywayClick: () -> Unit,
    onDismiss: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = AppColors.Background,
                    shape = AppShapes.extraLarge
                )
                .border(
                    width = 1.dp,
                    color = AppColors.BorderLight,
                    shape = AppShapes.extraLarge
                )
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .background(
                        brush = AppGradients.HighlightVertical,
                        shape = AppShapes.extraLarge
                    )
                    .padding(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WifiOff,
                    contentDescription = null,
                    tint = AppColors.OnHighlight,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.size(18.dp))

            Text(
                text = "Wi-Fi Not Detected",
                style = AppTextStyles.titleLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                ),
                color = AppColors.TextEnabled,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "For screen casting, your phone and TV should usually be connected to the same Wi-Fi network. You can open Wi-Fi settings or continue anyway.",
                style = AppTextStyles.bodySmall.copy(
                    color = AppColors.TextDisabled
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.size(22.dp))

            AppGradientButton(
                text = "Open Wi-Fi Settings",
                onClick = onOpenWifiClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.size(12.dp))

            AppOutlinedButton(
                text = "Continue Anyway",
                onClick = onContinueAnywayClick,
                modifier = Modifier.fillMaxWidth(),
                borderBrush = AppGradients.HighlightVertical,
                textBrush = AppGradients.HighlightVertical
            )
        }
    }
}
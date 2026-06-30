package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppGradientButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppOutlinedButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun VaultFirstTimeDialog(
    onSetPinClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    Dialog(
        onDismissRequest = onCancelClick,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.34f))
                .padding(horizontal = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = Color.Black.copy(alpha = 0.20f),
                        spotColor = Color.Black.copy(alpha = 0.20f)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppColors.Background)
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .shadow(
                            elevation = 22.dp,
                            shape = CircleShape,
                            ambientColor = AppColors.HighlightGradientTop.copy(alpha = 0.50f),
                            spotColor = AppColors.HighlightGradientBottom.copy(alpha = 0.50f)
                        )
                        .clip(CircleShape)
                        .background(com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients.HighlightVertical),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = AppColors.OnHighlight,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.size(26.dp))

                Text(
                    text = "Secure Your Vault",
                    style = AppTextStyles.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = AppColors.TextEnabled,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.size(10.dp))

                Text(
                    text = "To add files into Vault, set a PIN\nfirst. This ensures your videos and\naudios stay private.",
                    style = AppTextStyles.bodySmall.copy(
                        color = Color(0xFF667085)
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.size(28.dp))

                AppGradientButton(
                    text = "Set PIN",
                    onClick = onSetPinClick,
                    buttonHeight = 52.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(10.dp))

                AppOutlinedButton(
                    text = "Cancel",
                    onClick = onCancelClick,
                    buttonHeight = 52.dp,
                    borderColor = Color(0xFFD8DEE8),
                    textColor = AppColors.TextEnabled,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
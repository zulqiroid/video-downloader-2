package com.video.downloader.presentation.screens.vault.componants

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
import com.video.downloader.presentation.common.componants.AppGradientButton
import com.video.downloader.presentation.common.componants.AppOutlinedButton
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppGradients
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun VaultIncorrectPinDialog(
    onRetryClick: () -> Unit,
    onForgotPinClick: () -> Unit,
) {
    Dialog(
        onDismissRequest = onRetryClick,
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
                        elevation = 28.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = Color.Black.copy(alpha = 0.24f),
                        spotColor = Color.Black.copy(alpha = 0.24f)
                    )
                    .clip(RoundedCornerShape(24.dp))
                    .background(AppColors.Background)
                    .padding(horizontal = 24.dp, vertical = 30.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .shadow(
                            elevation = 24.dp,
                            shape = CircleShape,
                            ambientColor = AppColors.HighlightGradientTop.copy(alpha = 0.45f),
                            spotColor = AppColors.HighlightGradientBottom.copy(alpha = 0.45f)
                        )
                        .clip(CircleShape)
                        .background(AppGradients.HighlightVertical),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "!",
                        style = AppTextStyles.titleLarge.copy(
                            color = AppColors.OnHighlight,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }

                Spacer(modifier = Modifier.size(24.dp))

                Text(
                    text = "Incorrect PIN",
                    style = AppTextStyles.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = AppColors.TextEnabled,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.size(10.dp))

                Text(
                    text = "The PIN you entered is incorrect.\nPlease try again or reset your PIN.",
                    style = AppTextStyles.bodySmall.copy(
                        color = Color(0xFF667085)
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.size(26.dp))

                AppGradientButton(
                    text = "Retry",
                    onClick = onRetryClick,
                    buttonHeight = 52.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(10.dp))

                AppOutlinedButton(
                    text = "Forgot PIN?",
                    onClick = onForgotPinClick,
                    buttonHeight = 52.dp,
                    borderColor = Color(0xFFD8DEE8),
                    textColor = AppColors.TextEnabled,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
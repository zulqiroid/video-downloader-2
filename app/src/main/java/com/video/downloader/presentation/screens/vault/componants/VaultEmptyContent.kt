package com.video.downloader.presentation.screens.vault.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.video.downloader.R
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppShapes
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun VaultEmptyContent(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier.size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .shadow(
                        elevation = 28.dp,
                        shape = AppShapes.large,
                        ambientColor = AppColors.HighlightGradientTop.copy(alpha = 0.45f),
                        spotColor = AppColors.HighlightGradientBottom.copy(alpha = 0.45f)
                    )
                    .clip(AppShapes.large)
                    .background(Color(0xFFFFE6FB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_secure_net),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(42.dp)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-16).dp, y = 8.dp)
                    .size(34.dp)
                    .clip(AppShapes.medium)
                    .background(AppColors.Background)
                    .border(
                        width = 1.dp,
                        color = AppColors.BorderLight,
                        shape = AppShapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = AppColors.TextDisabled,
                    modifier = Modifier.size(22.dp)
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = 10.dp, y = (-6).dp)
                    .size(34.dp)
                    .clip(AppShapes.medium)
                    .background(AppColors.Background)
                    .border(
                        width = 1.dp,
                        color = AppColors.BorderLight,
                        shape = AppShapes.medium
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_info_filled),
                    contentDescription = null,
                    tint = AppColors.TextDisabled,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.size(12.dp))

        Text(
            text = stringResource(R.string.vault_is_secure_empty),
            style = AppTextStyles.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            color = AppColors.TextEnabled,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.size(6.dp))

        Text(
            text = stringResource(R.string.your_saved_files_will_appear_here_everything_is_encrypted_and_locked_away),
            style = AppTextStyles.bodySmall.copy(
                color = AppColors.TextDisabled
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}
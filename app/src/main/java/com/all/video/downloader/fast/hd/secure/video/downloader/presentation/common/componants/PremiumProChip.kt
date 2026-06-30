package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.AppHapticType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.hapticClickable
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun PremiumProChip(
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val hapticFeedback = rememberAppHapticFeedback()

    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(AppGradients.PremiumChipBG)
            .hapticClickable(
                hapticFeedback = hapticFeedback,
                hapticType = AppHapticType.Click,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 15.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_crown_with_star),
            contentDescription = null,
            tint = AppColors.OnHighlight,
            modifier = Modifier.size(18.dp)
        )

        Text(
            text = stringResource(R.string.pro),
            style = AppTextStyles.bodySmall.copy(
                color = AppColors.OnHighlight
            )
        )
    }
}
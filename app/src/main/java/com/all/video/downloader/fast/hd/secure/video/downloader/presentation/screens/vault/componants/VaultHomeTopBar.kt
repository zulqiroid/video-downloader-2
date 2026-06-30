package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.PremiumProChip
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.AppHapticType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.hapticClickable
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun VaultHomeTopBar(
    showPremiumLabel: Boolean,
    onPremiumClick: () -> Unit,
    modifier: Modifier = Modifier,
){
     Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = stringResource(R.string.vault),
            style = AppTextStyles.titleLarge
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            PremiumProChip(
                visible = showPremiumLabel,
                onClick = onPremiumClick
            )

        }

    }
}
package com.video.downloader.presentation.screens.onboarding.componants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.video.downloader.R
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppTextStyles


@Composable
fun OnboardingTopBar() {

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = stringResource(R.string.skip),
            style = AppTextStyles.bodySmall.copy(
                color = AppColors.TextDisabled
            )
        )

    }

}
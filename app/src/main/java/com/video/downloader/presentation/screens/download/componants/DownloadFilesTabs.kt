package com.video.downloader.presentation.screens.download.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.video.downloader.R
import com.video.downloader.presentation.screens.download.states.DownloadTab
import com.video.downloader.presentation.screens.player.states.PlayerTab
import com.video.downloader.presentation.theme.AppGradients
import com.video.downloader.presentation.theme.AppShapes
import com.video.downloader.presentation.theme.AppTextStyles


@Composable
fun DownloadFilesTabs(
    selectedTab: DownloadTab,
    onTabChange: (DownloadTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF1F3F5), RoundedCornerShape(16.dp))
            .padding(4.dp)
    ) {

        TabItem(
            title = stringResource(R.string.completed),
            selected = selectedTab == DownloadTab.COMPLETED
        ) {
            onTabChange(DownloadTab.COMPLETED)
        }

        TabItem(
            title = stringResource(R.string.in_progress),
            selected = selectedTab == DownloadTab.IN_PROGRESS
        ) {
            onTabChange(DownloadTab.IN_PROGRESS)
        }
    }
}

@Composable
private fun RowScope.TabItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .weight(1f)
            .then(
                if (selected) {
                    Modifier.shadow(
                        elevation = 8.dp,
                        shape = AppShapes.large,
                        clip = false
                    )
                } else {
                    Modifier
                }
            )
            .clip(AppShapes.large)
            .background(if (selected) Color.White else Color.Transparent)
            .clickable { onClick() }
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = title,
                style = AppTextStyles.bodyMedium.copy(
                    brush = if (selected) AppGradients.HighlightVertical else AppGradients.dummy2
                ),
            )
        }

    }
}
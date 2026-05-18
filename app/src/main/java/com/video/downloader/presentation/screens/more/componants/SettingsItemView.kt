package com.video.downloader.presentation.screens.more.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.screens.more.states.MoreStates
import com.video.downloader.presentation.screens.more.states.SettingsItem
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppGradients
import com.video.downloader.presentation.theme.AppShapes

@Composable
fun SettingsItemView(
    state: MoreStates,
    item: SettingsItem,
    onClick: () -> Unit = {},
    isNotificationEnabled: Boolean = false,
    onNotificationCheckedChange: (Boolean) -> Unit = {},
) {

    val detail = when (item) {
        SettingsItem.Language -> state.selectedLanguage.nameRes
        else -> item.detail
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(AppShapes.large)
                .background(AppGradients.HighlightVertical),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(item.icon),
                contentDescription = null,
                tint = AppColors.OnHighlight,
                modifier = Modifier.size(22.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(
                text = stringResource(item.title),
                style = MaterialTheme.typography.bodyMedium,
            )
            item.description?.let { detail ->
                Text(
                    text = stringResource(detail),
                    style = MaterialTheme.typography.labelSmall.copy(
                        AppColors.TextDisabled
                    ),
                )
            }
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            detail?.let { detail ->
                Text(
                    text = stringResource(detail),
                    style = MaterialTheme.typography.labelSmall.copy(
                        AppColors.TextDisabled
                    ),
                )
            }


            if (item == SettingsItem.Notification) {


                Switch(
                    checked = isNotificationEnabled,
                    onCheckedChange = onNotificationCheckedChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AppColors.OnHighlight,
                        checkedTrackColor = AppColors.HighlightGradientTop,
                        uncheckedThumbColor = AppColors.TextDisabled,
                        uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant,
                        uncheckedBorderColor = AppColors.TextDisabled,
                    ),
                )

            } else {

                item.openIcon?.let { icon ->
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AppColors.TextDisabled,
                    )
                }
            }
        }
    }
}
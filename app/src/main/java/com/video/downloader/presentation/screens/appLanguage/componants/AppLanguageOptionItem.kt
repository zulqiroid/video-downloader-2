package com.video.downloader.presentation.screens.appLanguage.componants

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.video.downloader.R
import com.video.downloader.presentation.screens.appLanguage.states.AppLanguageUiModel
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppGradients
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun AppLanguageOptionItem(
    language: AppLanguageUiModel,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = ShapeDefaults.Large

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                color = AppColors.Background,
                shape = shape
            )
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                brush = if (isSelected) {
                    AppGradients.HighlightVertical
                } else {
                    Brush.verticalGradient(
                        colors = listOf(
                            AppColors.BorderLight,
                            AppColors.BorderLight
                        )
                    )
                },
                shape = shape
            )
            .clickable(
                role = Role.Button,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(22.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LanguageFlag(
            flag = language.flagEmoji
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = stringResource(language.nameRes),
            style =  AppTextStyles.bodyLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = AppColors.TextEnabled,
            modifier = Modifier.weight(1f)
        )

        if (isSelected) {
            SelectedCheckMark()
        }
    }
}

@Composable
private fun LanguageFlag(
    flag: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(45.dp)
            .clip(CircleShape)
            .background(AppColors.DisabledContainer),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(flag),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun SelectedCheckMark(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(AppGradients.HighlightVertical),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_check),
            contentDescription = null,
            tint = AppColors.OnHighlight,
            modifier = Modifier.size(16.dp)
        )
    }
}
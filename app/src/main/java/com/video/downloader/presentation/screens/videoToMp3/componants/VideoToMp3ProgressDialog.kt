package com.video.downloader.presentation.screens.videoToMp3.componants

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.video.downloader.R
import com.video.downloader.domain.models.videoToMp3.VideoToMp3ConversionItem
import com.video.downloader.domain.models.videoToMp3.VideoToMp3Preset
import com.video.downloader.presentation.common.haptics.AppHapticType
import com.video.downloader.presentation.common.haptics.hapticClickable
import com.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppGradients
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun VideoToMp3ProgressDialog(
    item: VideoToMp3ConversionItem,
    selectedPreset: VideoToMp3Preset,
    onCancelClick: () -> Unit
) {
    val hapticFeedback = rememberAppHapticFeedback()

    val animatedProgress by animateFloatAsState(
        targetValue = item.progress.coerceIn(0, 100) / 100f,
        label = "video_to_mp3_dialog_progress"
    )

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.38f))
                .padding(horizontal = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 24.dp,
                        shape = RoundedCornerShape(30.dp),
                        ambientColor = AppColors.HighlightGradientTop.copy(alpha = 0.35f),
                        spotColor = AppColors.HighlightGradientBottom.copy(alpha = 0.35f)
                    )
                    .clip(RoundedCornerShape(30.dp))
                    .background(AppColors.Background)
                    .padding(horizontal = 22.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .dropShadow(
                            shape = RoundedCornerShape(26.dp),
                            shadow = Shadow(
                                radius = 30.dp,
                                spread = 0.05.dp,
                                brush = AppGradients.HighlightVertical,
                                offset = DpOffset(0.dp, 0.dp)
                            )
                        )
                        .clip(RoundedCornerShape(26.dp))
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_music_node_filled),
                        contentDescription = null,
                        tint = AppColors.HighlightGradientTop,
                        modifier = Modifier.size(50.dp)
                    )

                    Icon(
                        painter = painterResource(R.drawable.ic_single_music_node_filled),
                        contentDescription = null,
                        tint = AppColors.HighlightGradientTop,
                        modifier = Modifier
                            .padding(top = 5.dp, end = 5.dp)
                            .size(18.dp)
                            .align(Alignment.TopEnd)
                     )

                    Icon(
                        painter = painterResource(R.drawable.ic_music_nodes_filled),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .padding(bottom = 5.dp, start = 10.dp)
                            .size(18.dp)
                            .align(Alignment.BottomStart)
                     )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = item.dialogTitleText(),
                    style = AppTextStyles.titleLarge.copy(
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = AppColors.TextEnabled,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = selectedPreset.titleText(),
                    style = AppTextStyles.caption.copy(
                        color = AppColors.HighlightGradientTop,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = item.fileName.ifBlank { "Preparing selected video..." },
                    style = AppTextStyles.caption.copy(
                        color = Color(0xFF667085)
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                LinearProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(CircleShape),
                    color = AppColors.HighlightGradientTop,
                    trackColor = Color(0xFFEBDCF0),
                    strokeCap = ProgressIndicatorDefaults.LinearStrokeCap
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.statusText(),
                        style = AppTextStyles.caption.copy(
                            color = Color(0xFF667085),
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = "${item.progress.coerceIn(0, 100)}%",
                        style = AppTextStyles.bodySmall.copy(
                            color = AppColors.HighlightGradientTop,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFF8F5FB))
                        .border(
                            width = 1.dp,
                            color = Color(0xFFE8E5EF),
                            shape = RoundedCornerShape(18.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(AppGradients.HighlightVertical),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = AppColors.OnHighlight,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.size(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Extracting audio track",
                            style = AppTextStyles.titleSmall.copy(
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = AppColors.TextEnabled,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Text(
                            text = "Please keep the app open for best speed",
                            style = AppTextStyles.caption.copy(
                                color = Color(0xFFA3A0AD)
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(Color(0xFFF5F3F8))
                        .hapticClickable(
                            hapticFeedback = hapticFeedback,
                            hapticType = AppHapticType.Reject,
                            role = Role.Button,
                            onClick = onCancelClick
                        )
                        .padding(vertical = 15.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = AppColors.TextDisabled,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.size(8.dp))

                    Text(
                        text = "Cancel Conversion",
                        style = AppTextStyles.bodySmall.copy(
                            color = AppColors.TextDisabled,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                }
            }
        }
    }
}
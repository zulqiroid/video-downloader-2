package com.video.downloader.presentation.screens.download.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.video.downloader.R
import com.video.downloader.domain.models.DownloadStatus
import com.video.downloader.presentation.screens.download.events.DownloadEvents
import com.video.downloader.presentation.screens.download.states.DownloadStates
import com.video.downloader.presentation.screens.download.states.DownloadUiItem
import com.video.downloader.presentation.screens.download.viewModel.DownloadViewModel

@Composable
fun DownloadingCard(
    item: DownloadUiItem,
    state: DownloadStates,
    viewModel: DownloadViewModel
) {

    val isPaused = item.status == DownloadStatus.PAUSED
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(Color.White, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFFFE5E5), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_vd_cam_filled),
                    contentDescription = null,
                    tint = Color(0xFFE00004),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W700,
                    maxLines = 1,
                    color = Color(0xFF1F2937)
                )

                Text(
                  /*  text = stringResource(
                        id = R.string.download_status_downloading_with_size,
                        item.sizeText
                    ),*/
                    text = "abbcc",
                    fontSize = 10.sp,
                    color = Color(0xFF6B7280)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                progress = {
                    item.progress.coerceIn(0, 100) / 100f
                },
                modifier = Modifier
                    .weight(1f)
                    .height(12.dp)
                    .clip(RoundedCornerShape(50)),
                color = Color(0xFFE00004),
                trackColor = Color(0xFFF3F4F6),
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "${item.progress.coerceIn(0, 100)}%",
                fontSize = 12.sp,
                fontWeight = FontWeight.W700,
                color = Color(0xFFE00004)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(Color(0xFFE4E4E7), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text ="abc",
                    fontSize = 11.sp,
                    color = Color(0xFF52525B)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = item.timeText,
                fontSize = 12.sp,
                color = Color(0xFF71717A)
            )

            Spacer(modifier = Modifier.weight(1f))

            CircleIconButton(
                icon = /*if (isPaused) {*/
                    R.drawable.ic_play,
             /*   } else {
                    R.drawable.ic_pause
                },*/
                backgroundColor = Color.Transparent,
                tint = Color(0xFF52525B),
                borderColor = Color(0xFFE5E7EB),
                contentDescription = if (isPaused) {
                    "Resume download"
                } else {
                    "Pause download"
                },
                onClick = {
                    if (isPaused) {
                        viewModel.onEvents(
                            DownloadEvents.OnResumeDownloadingClicked(item.id)
                        )
                    } else {
                        viewModel.onEvents(
                            DownloadEvents.OnPauseDownloadingClicked(item.id)
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.width(10.dp))

            CircleIconButton(
                icon = R.drawable.ic_bin_filled,
                backgroundColor = Color(0xFFE00004).copy(alpha = 0.1f),
                tint = Color(0xFFE00004),
                borderColor = Color(0xFFE00004).copy(alpha = 0.25f),
                contentDescription =null,
                onClick = {
                    viewModel.onEvents(
                        DownloadEvents.OnDeleteDownloadingClicked(item.id)
                    )
                }
            )
        }
    }
}
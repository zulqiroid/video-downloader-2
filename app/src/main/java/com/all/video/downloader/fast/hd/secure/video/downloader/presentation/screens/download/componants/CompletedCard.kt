package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states.DownloadStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.states.DownloadUiItem
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.download.viewModel.DownloadViewModel

@Composable
fun CompletedCard(
    item: DownloadUiItem,
    state: DownloadStates,
    viewModel: DownloadViewModel,
    onItemCLicked: (DownloadUiItem) -> Unit,
    onMoreClicked: (DownloadUiItem) -> Unit,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp),
                clip = false
            )
            .background(Color.White, RoundedCornerShape(16.dp))
            .border(
                color = Color(0xFFE5E7EB),
                width = 1.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable {
                onItemCLicked(item)
            }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        VideoThumbnail(
            path = item.filePath,
            overlayIcon = {
                Icon(
                    painter = painterResource(R.drawable.ic_play),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = item.title,
                fontWeight = FontWeight.W700,
                fontSize = 14.sp,
                color = Color(0xFF1F2937)
            )

            Text(
                text = item.sizeText,
                fontSize = 10.sp,
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.W400
            )
        }

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = null,
            modifier = Modifier.clickable {
                onMoreClicked(item)
            }
        )
    }
}
package com.video.downloader.presentation.screens.vault.componants

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.screens.vault.states.VaultUiItem

@Composable
fun VaultHomeContent(
    items: List<VaultUiItem>,
    onAddClick: () -> Unit,
    onItemClick: (VaultUiItem) -> Unit,
    onRestoreClick: (VaultUiItem) -> Unit,
    onDeleteClick: (VaultUiItem) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(
                modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars)
            )

            VaultHomeTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(top = 8.dp)
            )

            if (items.isEmpty()) {
                VaultEmptyContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                )
            } else {
                VaultFilesGrid(
                    items = items,
                    onItemClick = onItemClick,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    onRestoreClick = onRestoreClick,
                    onDeleteClick = onDeleteClick
                )
            }
        }

        VaultAddFloatingButton(
            onClick = onAddClick,
            modifier = Modifier
                .padding(end = 24.dp, bottom = 30.dp)
                .align(androidx.compose.ui.Alignment.BottomEnd)
        )
    }
}
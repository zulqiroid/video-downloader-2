package com.video.downloader.presentation.screens.vault.componants

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.screens.vault.states.VaultUiItem

@Composable
fun VaultFilesGrid(
    items: List<VaultUiItem>,
    onItemClick: (VaultUiItem) -> Unit,
    modifier: Modifier = Modifier,
    onRestoreClick: (VaultUiItem) -> Unit,
    onDeleteClick: (VaultUiItem) -> Unit
) {
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 22.dp,
            bottom = 100.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = items,
            key = { item -> item.id }
        ) { item ->
            VaultFileCard(
                item = item,
                onClick = {
                    onItemClick(item)
                },
                onLongPress = {
                    onRestoreClick(item)
                },
                onDeleteClick = {
                    onDeleteClick(item)
                }
            )
        }
    }
}
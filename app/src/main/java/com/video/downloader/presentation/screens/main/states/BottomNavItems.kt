package com.video.downloader.presentation.screens.main.states

import androidx.annotation.StringRes
import com.video.downloader.R

sealed class BottomNavItems(
    @StringRes val title: Int,
    val iconSelected: Int,
    val iconUnselected: Int,
) {

    object Home : BottomNavItems(
        title = R.string.home,
        iconSelected = R.drawable.ic_home_filled,
        iconUnselected = R.drawable.ic_home_outlined
    )
    object Player : BottomNavItems(
        title = R.string.player,
        iconSelected = R.drawable.ic_player_filled,
        iconUnselected = R.drawable.ic_player_outlined
    )
    object Reels : BottomNavItems(
        title = R.string.reels,
        iconSelected = R.drawable.ic_vd_cam_filled,
        iconUnselected = R.drawable.ic_vd_cam_filled
    )
    object Files : BottomNavItems(
        title = R.string.files,
        iconSelected = R.drawable.ic_file_filled,
        iconUnselected = R.drawable.ic_files_outlined
    )
    object Vault : BottomNavItems(
        title = R.string.vault,
        iconSelected = R.drawable.ic_vault_filled,
        iconUnselected = R.drawable.ic_vault_outlined
    )

}

fun getBottomNavItems(): List<BottomNavItems> {
    return listOf(
        BottomNavItems.Home,
        BottomNavItems.Player,
        BottomNavItems.Reels,
        BottomNavItems.Files,
        BottomNavItems.Vault
    )
}
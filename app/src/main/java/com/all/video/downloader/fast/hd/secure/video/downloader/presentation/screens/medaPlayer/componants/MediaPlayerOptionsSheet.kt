package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.events.VideoOptionsIntent
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerEqualizerBandState
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerEqualizerPresetState
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerEqualizerState
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerState
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppCornerRadius
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.Subtitles
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerSubtitleSource
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerSubtitleState
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerSubtitleTrackState
import androidx.compose.material.icons.filled.FolderOpen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPlayerOptionsSheet(
    state: MediaPlayerState,
    media: MediaFile,
    onIntent: (VideoOptionsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = {
            onIntent(VideoOptionsIntent.OnDismiss)
        },
        containerColor = Color.White,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = AppColors.HighlightGradientTop.copy(alpha = 0.35f)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            MediaPlayerSheetHeader(
                title = stringResource(R.string.media_player_options_title),
                subtitle = media.fileName,
                onClose = {
                    onIntent(VideoOptionsIntent.OnDismiss)
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            MediaPlayerOptionRow(
                icon = Icons.Default.Speed,
                title = stringResource(R.string.media_player_playback_speed_title),
                subtitle = stringResource(
                    id = R.string.media_player_playback_speed_current,
                    formatPlaybackSpeed(state.playbackSpeed)
                ),
                badge = formatPlaybackSpeed(state.playbackSpeed),
                onClick = {
                    onIntent(VideoOptionsIntent.OnPlaybackSpeedClicked)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            MediaPlayerOptionRow(
                icon = Icons.Default.GraphicEq,
                title = stringResource(R.string.media_player_equalizer_title),
                subtitle = stringResource(R.string.media_player_equalizer_subtitle),
                badge = if (state.equalizerState.isEnabled) {
                    stringResource(R.string.media_player_equalizer_status_on)
                } else {
                    stringResource(R.string.media_player_equalizer_status_off)
                },
                onClick = {
                    onIntent(VideoOptionsIntent.OnEqualizerClicked)
                }
            )

            if (media.isVideo) {
                Spacer(modifier = Modifier.height(12.dp))

                val subtitleBadge = when {
                    state.subtitleState.isEnabled -> {
                        stringResource(R.string.media_player_subtitle_status_on)
                    }

                    state.subtitleState.hasTracks -> {
                        stringResource(
                            id = R.string.media_player_subtitle_status_available,
                            state.subtitleState.tracks.size
                        )
                    }

                    else -> {
                        stringResource(R.string.media_player_subtitle_status_off)
                    }
                }

                MediaPlayerOptionRow(
                    icon = Icons.Default.Subtitles,
                    title = stringResource(R.string.media_player_subtitle_title),
                    subtitle = stringResource(R.string.media_player_subtitle_subtitle),
                    badge = subtitleBadge,
                    onClick = {
                        onIntent(VideoOptionsIntent.OnSubtitleClicked)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPlayerPlaybackSpeedSheet(
    currentSpeed: Float,
    onSpeedSelected: (Float) -> Unit,
    onResetClicked: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val speedOptions = remember {
        MediaPlayerSpeedOption.defaultOptions
    }

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = AppColors.HighlightGradientTop.copy(alpha = 0.35f)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            MediaPlayerSheetHeader(
                title = stringResource(R.string.media_player_playback_speed_title),
                subtitle = stringResource(R.string.media_player_playback_speed_subtitle),
                onClose = onDismiss
            )

            Spacer(modifier = Modifier.height(18.dp))

            speedOptions.forEach { option ->
                PlaybackSpeedRow(
                    option = option,
                    isSelected = option.isSameSpeed(currentSpeed),
                    onClick = {
                        onSpeedSelected(option.speed)
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppCornerRadius.Large))
                    .clickable(onClick = onResetClicked),
                color = AppColors.DisabledContainer,
                shape = RoundedCornerShape(AppCornerRadius.Large)
            ) {
                Text(
                    text = stringResource(R.string.media_player_playback_speed_reset),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.TextEnabled
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPlayerEqualizerSheet(
    equalizerState: MediaPlayerEqualizerState,
    onIntent: (VideoOptionsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = {
            onIntent(VideoOptionsIntent.OnEqualizerDialogDismissed)
        },
        containerColor = Color.White,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = AppColors.HighlightGradientTop.copy(alpha = 0.35f)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            MediaPlayerSheetHeader(
                title = stringResource(R.string.media_player_equalizer_title),
                subtitle = stringResource(R.string.media_player_equalizer_subtitle),
                onClose = {
                    onIntent(VideoOptionsIntent.OnEqualizerDialogDismissed)
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            if (!equalizerState.isSupported) {
                EqualizerInfoCard(
                    message = equalizerState.errorMessage
                        ?: stringResource(R.string.media_player_equalizer_not_supported)
                )
                return@Column
            }

            EqualizerPowerRow(
                isEnabled = equalizerState.isEnabled,
                onCheckedChange = { enabled ->
                    onIntent(VideoOptionsIntent.OnEqualizerEnabledChanged(enabled))
                }
            )

            equalizerState.errorMessage?.let { message ->
                Spacer(modifier = Modifier.height(12.dp))
                EqualizerInfoCard(message = message)
            }

            if (equalizerState.hasPresets) {
                Spacer(modifier = Modifier.height(20.dp))

                EqualizerSectionTitle(
                    title = stringResource(R.string.media_player_equalizer_presets_title)
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = equalizerState.presets,
                        key = { preset -> preset.index }
                    ) { preset ->
                        EqualizerPresetChip(
                            preset = preset,
                            isSelected = equalizerState.selectedPresetIndex == preset.index,
                            isEnabled = equalizerState.isEnabled,
                            onClick = {
                                onIntent(
                                    VideoOptionsIntent.OnEqualizerPresetSelected(
                                        preset.index
                                    )
                                )
                            }
                        )
                    }
                }
            }

            if (equalizerState.hasBands) {
                Spacer(modifier = Modifier.height(22.dp))

                EqualizerSectionTitle(
                    title = stringResource(R.string.media_player_equalizer_bands_title)
                )

                Spacer(modifier = Modifier.height(8.dp))

                equalizerState.bands.forEach { band ->
                    EqualizerBandSlider(
                        band = band,
                        minLevel = equalizerState.minBandLevel,
                        maxLevel = equalizerState.maxBandLevel,
                        isEnabled = equalizerState.isEnabled,
                        onLevelCommitted = { level ->
                            onIntent(
                                VideoOptionsIntent.OnEqualizerBandLevelChanged(
                                    bandIndex = band.index,
                                    level = level
                                )
                            )
                        }
                    )
                }
            }

            if (equalizerState.bassBoostSupported) {
                Spacer(modifier = Modifier.height(18.dp))

                HorizontalDivider(
                    color = AppColors.Divider
                )

                Spacer(modifier = Modifier.height(18.dp))

                BassBoostSlider(
                    strength = equalizerState.bassBoostStrength,
                    isEnabled = equalizerState.isEnabled,
                    onStrengthCommitted = { strength ->
                        onIntent(VideoOptionsIntent.OnBassBoostChanged(strength))
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPlayerSubtitleSheet(
    subtitleState: MediaPlayerSubtitleState,
    onIntent: (VideoOptionsIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        modifier = modifier,
        sheetState = sheetState,
        onDismissRequest = {
            onIntent(VideoOptionsIntent.OnSubtitleDialogDismissed)
        },
        containerColor = Color.White,
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = AppColors.HighlightGradientTop.copy(alpha = 0.35f)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            MediaPlayerSheetHeader(
                title = stringResource(R.string.media_player_subtitle_title),
                subtitle = stringResource(R.string.media_player_subtitle_subtitle),
                onClose = {
                    onIntent(VideoOptionsIntent.OnSubtitleDialogDismissed)
                }
            )

            Spacer(modifier = Modifier.height(18.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 460.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item(
                    key = "subtitle_off"
                ) {
                    SubtitleOffRow(
                        isSelected = !subtitleState.isEnabled,
                        onClick = {
                            onIntent(VideoOptionsIntent.OnSubtitleDisabledClicked)
                        }
                    )
                }

                item(
                    key = "subtitle_picker"
                ) {
                    SubtitlePickerRow(
                        onClick = {
                            onIntent(VideoOptionsIntent.OnSubtitlePickerClicked)
                        }
                    )
                }

                if (!subtitleState.hasTracks) {
                    item(
                        key = "subtitle_empty"
                    ) {
                        EqualizerInfoCard(
                            message = subtitleState.errorMessage
                                ?: stringResource(R.string.media_player_subtitle_no_tracks)
                        )
                    }
                }

                items(
                    items = subtitleState.tracks,
                    key = { track -> track.id }
                ) { track ->
                    SubtitleTrackRow(
                        track = track,
                        isSelected = subtitleState.isEnabled &&
                                subtitleState.selectedTrackId == track.id,
                        onClick = {
                            onIntent(
                                VideoOptionsIntent.OnSubtitleTrackSelected(
                                    track.id
                                )
                            )
                        }
                    )
                }

                subtitleState.errorMessage?.let { message ->
                    if (subtitleState.hasTracks) {
                        item(
                            key = "subtitle_error"
                        ) {
                            EqualizerInfoCard(message = message)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SubtitlePickerRow(
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppCornerRadius.Large))
            .clickable(onClick = onClick),
        color = AppColors.DisabledContainer,
        shape = RoundedCornerShape(AppCornerRadius.Large)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(AppGradients.HighlightVertical),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FolderOpen,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.media_player_subtitle_pick_file_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextEnabled
                )

                Text(
                    text = stringResource(R.string.media_player_subtitle_pick_file_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextDisabled,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun MediaPlayerSheetHeader(
    title: String,
    subtitle: String,
    onClose: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AppColors.TextEnabled
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.TextDisabled,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        IconButton(
            onClick = onClose,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(AppColors.DisabledContainer)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = AppColors.TextEnabled
            )
        }
    }
}

@Composable
private fun MediaPlayerOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    badge: String,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppCornerRadius.Large))
            .clickable(onClick = onClick),
        color = AppColors.DisabledContainer,
        shape = RoundedCornerShape(AppCornerRadius.Large)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(AppGradients.HighlightVertical),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextEnabled
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextDisabled,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Text(
                text = badge,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = AppColors.HighlightGradientTop
            )
        }
    }
}

@Composable
private fun PlaybackSpeedRow(
    option: MediaPlayerSpeedOption,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val containerColor = if (isSelected) {
        AppColors.HighlightGradientTop.copy(alpha = 0.10f)
    } else {
        Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppCornerRadius.Medium))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = option.label,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) {
                AppColors.HighlightGradientTop
            } else {
                AppColors.TextEnabled
            }
        )

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = AppColors.HighlightGradientTop,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun EqualizerPowerRow(
    isEnabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = AppColors.DisabledContainer,
        shape = RoundedCornerShape(AppCornerRadius.Large)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.media_player_equalizer_enabled_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextEnabled
                )

                Text(
                    text = stringResource(R.string.media_player_equalizer_enabled_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextDisabled
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = onCheckedChange
            )
        }
    }
}

@Composable
private fun EqualizerSectionTitle(
    title: String,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = AppColors.TextEnabled
    )
}

@Composable
private fun EqualizerPresetChip(
    preset: MediaPlayerEqualizerPresetState,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(AppCornerRadius.Full)

    Box(
        modifier = Modifier
            .clip(shape)
            .then(
                if (isSelected) {
                    Modifier.background(AppGradients.HighlightVertical)
                } else {
                    Modifier.background(AppColors.DisabledContainer)
                }
            )
            .clickable(
                enabled = isEnabled,
                onClick = onClick
            )
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = preset.name,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) {
                Color.White
            } else if (isEnabled) {
                AppColors.TextEnabled
            } else {
                AppColors.TextDisabled
            }
        )
    }
}

@Composable
private fun SubtitleOffRow(
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppCornerRadius.Large))
            .clickable(onClick = onClick),
        color = if (isSelected) {
            AppColors.HighlightGradientTop.copy(alpha = 0.10f)
        } else {
            AppColors.DisabledContainer
        },
        shape = RoundedCornerShape(AppCornerRadius.Large)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) {
                            AppGradients.HighlightVertical
                        } else {
                            AppGradients.dummy
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Subtitles,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else AppColors.TextDisabled,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.media_player_subtitle_off_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextEnabled
                )

                Text(
                    text = stringResource(R.string.media_player_subtitle_off_subtitle),
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextDisabled
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = AppColors.HighlightGradientTop,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun SubtitleTrackRow(
    track: MediaPlayerSubtitleTrackState,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppCornerRadius.Large))
            .clickable(onClick = onClick),
        color = if (isSelected) {
            AppColors.HighlightGradientTop.copy(alpha = 0.10f)
        } else {
            AppColors.DisabledContainer
        },
        shape = RoundedCornerShape(AppCornerRadius.Large)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) {
                            AppGradients.HighlightVertical
                        } else {
                            AppGradients.dummy
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Subtitles,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else AppColors.TextDisabled,
                    modifier = Modifier.size(22.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = track.fileName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextEnabled,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = track.subtitleMetaText(),
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.TextDisabled,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = AppColors.HighlightGradientTop,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

private fun MediaPlayerSubtitleTrackState.subtitleMetaText(): String {
    return "$languageLabel • ${source.toLabel()}"
}

private fun MediaPlayerSubtitleSource.toLabel(): String {
    return when (this) {
        MediaPlayerSubtitleSource.LocalFile -> "Local file"
        MediaPlayerSubtitleSource.DocumentPicker -> "Picked file"
    }
}

@Composable
private fun EqualizerBandSlider(
    band: MediaPlayerEqualizerBandState,
    minLevel: Int,
    maxLevel: Int,
    isEnabled: Boolean,
    onLevelCommitted: (Int) -> Unit,
) {
    var sliderValue by remember(band.index) {
        mutableFloatStateOf(band.level.toFloat())
    }

    LaunchedEffect(band.level) {
        sliderValue = band.level.toFloat()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = band.frequencyLabel,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (isEnabled) AppColors.TextEnabled else AppColors.TextDisabled,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = formatMilliBelToDb(sliderValue.roundToInt()),
                style = MaterialTheme.typography.labelMedium,
                color = AppColors.HighlightGradientTop
            )
        }

        Slider(
            value = sliderValue.coerceIn(
                minimumValue = minLevel.toFloat(),
                maximumValue = maxLevel.toFloat()
            ),
            onValueChange = { value ->
                sliderValue = value
            },
            onValueChangeFinished = {
                onLevelCommitted(sliderValue.roundToInt())
            },
            valueRange = minLevel.toFloat()..maxLevel.toFloat(),
            enabled = isEnabled
        )
    }
}

@Composable
private fun BassBoostSlider(
    strength: Int,
    isEnabled: Boolean,
    onStrengthCommitted: (Int) -> Unit,
) {
    var sliderValue by remember {
        mutableFloatStateOf(strength.toFloat())
    }

    LaunchedEffect(strength) {
        sliderValue = strength.toFloat()
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.media_player_equalizer_bass_boost_title),
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = if (isEnabled) AppColors.TextEnabled else AppColors.TextDisabled
            )

            Text(
                text = "${(sliderValue / 10f).roundToInt()}%",
                style = MaterialTheme.typography.labelMedium,
                color = AppColors.HighlightGradientTop
            )
        }

        Slider(
            value = sliderValue.coerceIn(
                minimumValue = MediaPlayerEqualizerState.DEFAULT_BASS_BOOST_STRENGTH.toFloat(),
                maximumValue = MediaPlayerEqualizerState.MAX_BASS_BOOST_STRENGTH.toFloat()
            ),
            onValueChange = { value ->
                sliderValue = value
            },
            onValueChangeFinished = {
                onStrengthCommitted(sliderValue.roundToInt())
            },
            valueRange = MediaPlayerEqualizerState.DEFAULT_BASS_BOOST_STRENGTH.toFloat()..
                    MediaPlayerEqualizerState.MAX_BASS_BOOST_STRENGTH.toFloat(),
            enabled = isEnabled
        )
    }
}

@Composable
private fun EqualizerInfoCard(
    message: String,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = AppColors.Error.copy(alpha = 0.08f),
        shape = RoundedCornerShape(AppCornerRadius.Large)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(14.dp),
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.Error
        )
    }
}

private fun formatPlaybackSpeed(speed: Float): String {
    return when (speed) {
        1f -> "1x"
        speed.toInt().toFloat() -> "${speed.toInt()}x"
        else -> "${speed}x"
    }
}

private fun formatMilliBelToDb(level: Int): String {
    return if (level % MILLIBELS_IN_ONE_DB == 0) {
        String.format(
            Locale.US,
            "%+d dB",
            level / MILLIBELS_IN_ONE_DB
        )
    } else {
        String.format(
            Locale.US,
            "%+.1f dB",
            level / MILLIBELS_IN_ONE_DB.toFloat()
        )
    }
}

@Immutable
private data class MediaPlayerSpeedOption(
    val speed: Float,
    val label: String,
) {
    fun isSameSpeed(other: Float): Boolean {
        return abs(speed - other) < SPEED_COMPARE_EPSILON
    }

    companion object {
        val defaultOptions = listOf(
            MediaPlayerSpeedOption(0.25f, "0.25x"),
            MediaPlayerSpeedOption(0.5f, "0.5x"),
            MediaPlayerSpeedOption(0.75f, "0.75x"),
            MediaPlayerSpeedOption(1f, "Normal 1x"),
            MediaPlayerSpeedOption(1.25f, "1.25x"),
            MediaPlayerSpeedOption(1.5f, "1.5x"),
            MediaPlayerSpeedOption(1.75f, "1.75x"),
            MediaPlayerSpeedOption(2f, "2x"),
            MediaPlayerSpeedOption(3f, "3x"),
            MediaPlayerSpeedOption(4f, "4x"),
        )
    }
}

private const val SPEED_COMPARE_EPSILON = 0.001f
private const val MILLIBELS_IN_ONE_DB = 100
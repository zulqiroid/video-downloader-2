package com.video.downloader.presentation.screens.medaPlayer.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.video.downloader.presentation.screens.medaPlayer.events.MediaPlayerEvent
import com.video.downloader.presentation.screens.medaPlayer.events.MediaPlayerNavEvent
import com.video.downloader.presentation.screens.medaPlayer.events.VideoOptionsIntent
import com.video.downloader.presentation.screens.medaPlayer.screen.MediaPlayerManager
import com.video.downloader.domain.models.MediaFile
import com.video.downloader.domain.models.RingtoneTargetType
import com.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MediaPlayerViewModel @Inject constructor(
    application: Application,
) : AndroidViewModel(application) {

    private var playerManager = MediaPlayerManager(application)

    private val _state = MutableStateFlow(MediaPlayerState())
    val state = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<MediaPlayerNavEvent>()
    val navEvents = _navEvents.asSharedFlow()

    private var progressJob: Job? = null

    val player: ExoPlayer
        get() = playerManager.player

    private var playerListener: Player.Listener? = null

    init {
        observePlayerCompletion()
        startProgressObserver()
    }

    fun onEvent(event: MediaPlayerEvent) {
        when (event) {
            is MediaPlayerEvent.Load -> {
                load(
                    mediaList = event.mediaList,
                    startIndex = event.startIndex
                )
            }

            is MediaPlayerEvent.OnPageChanged -> {
                playIndex(event.index)
            }

            MediaPlayerEvent.OnPlayPauseClicked -> {
                togglePlayPause()
            }

            MediaPlayerEvent.OnNextClicked -> {
                playNext()
            }

            MediaPlayerEvent.OnPreviousClicked -> {
                playPrevious()
            }

            MediaPlayerEvent.OnForwardClicked -> {
                playerManager.forward()
            }

            MediaPlayerEvent.OnRewindClicked -> {
                playerManager.rewind()
            }

            is MediaPlayerEvent.OnSeek -> {
                playerManager.seekTo(event.position)

                _state.update {
                    it.copy(position = event.position)
                }
            }

            is MediaPlayerEvent.OnVolumeChanged -> {
                playerManager.setVolume(event.volume)

                _state.update {
                    it.copy(
                        volume = event.volume,
                        isMuted = event.volume == 0f
                    )
                }
            }

            MediaPlayerEvent.OnMuteToggleClicked -> {
                toggleMute()
            }

            MediaPlayerEvent.OnBackPressed -> {
                closePlayerSession()
            }

            MediaPlayerEvent.OnThreeDotsClick -> {
                _state.update {
                    it.copy(showBottomSheet = true)
                }
            }
            MediaPlayerEvent.OnNativeAdPageVisible -> {
//                onNativeAdPageVisible()
            }

            MediaPlayerEvent.OnShuffleClicked -> {
                toggleShuffle()
            }
            MediaPlayerEvent.OnAudioRepeatClicked -> {
                toggleAudioRepeat()
            }
        }
    }

    private fun toggleAudioRepeat() {
        val currentState = _state.value
        val currentMedia = currentState.mediaList.getOrNull(currentState.currentIndex)

        // Repeat button is audio-only.
        if (currentMedia?.isVideo == true) return

        _state.update {
            it.copy(
                isAudioRepeatEnabled = !it.isAudioRepeatEnabled
            )
        }
    }

    fun onBottomSheetIntent(intent: VideoOptionsIntent) {
        when (intent) {
            VideoOptionsIntent.OnAddToPlayingQueueClicked -> {
                // TODO: Add queue functionality later
            }

            VideoOptionsIntent.OnPlaybackSpeedClicked -> {
                _state.update {
                    it.copy(
                        showBottomSheet = false,
                        showPlaybackSpeedDialog = true
                    )
                }
            }

            is VideoOptionsIntent.OnPlaybackSpeedSelected -> {
                updatePlaybackSpeed(intent.speed)
            }

            VideoOptionsIntent.OnPlaybackSpeedResetClicked -> {
                updatePlaybackSpeed(MediaPlayerState.DEFAULT_PLAYBACK_SPEED)
            }

            VideoOptionsIntent.OnPlaybackSpeedDialogDismissed -> {
                _state.update {
                    it.copy(showPlaybackSpeedDialog = false)
                }
            }

            VideoOptionsIntent.OnFileInfoClicked -> {
                openFileInfoDialog()
            }

            VideoOptionsIntent.OnFileInfoDismissed -> {
                dismissFileInfoDialog()
            }

            VideoOptionsIntent.OnRenameClicked -> {
                openRenameDialog()
            }

            is VideoOptionsIntent.OnRenameValueChanged -> {
                onRenameValueChanged(intent.value)
            }

            VideoOptionsIntent.OnRenameDismissed -> {
                dismissRenameDialog()
            }

            VideoOptionsIntent.OnRenameConfirmClicked -> {
//                renameSelectedFile()
            }

            VideoOptionsIntent.OnRenamePermissionGranted -> {
//                renameSelectedFile()
            }

            VideoOptionsIntent.OnRenamePermissionDenied -> {
                _state.update {
                    it.copy(
                        isRenamingFile = false,
                        renameError = "Rename permission was denied"
                    )
                }
            }

            VideoOptionsIntent.OnShareClicked -> {
                shareCurrentFile()
            }

            VideoOptionsIntent.OnDeleteClicked -> {
                openDeleteDialog()
            }

            VideoOptionsIntent.OnDeleteDismissed -> {
                dismissDeleteDialog()
            }

            VideoOptionsIntent.OnDeleteConfirmClicked -> {
//                deleteSelectedFile()
            }

            VideoOptionsIntent.OnDeletePermissionGranted -> {
                onDeleteSuccess()
            }

            VideoOptionsIntent.OnDeletePermissionDenied -> {
                _state.update {
                    it.copy(
                        isDeletingFile = false,
                        deleteFileError = "Delete permission was denied"
                    )
                }
            }

            VideoOptionsIntent.OnDismiss -> {
                _state.update {
                    it.copy(showBottomSheet = false)
                }
            }
            VideoOptionsIntent.OnSetAsRingtoneClicked -> {
                openSetAsRingtoneDialog()
            }

            is VideoOptionsIntent.OnRingtoneTargetSelected -> {
                _state.update {
                    it.copy(
                        selectedRingtoneTargetType = intent.targetType,
                        setRingtoneError = null
                    )
                }
            }

            VideoOptionsIntent.OnSetAsRingtoneConfirmClicked -> {
//                setSelectedAudioAsRingtone()
            }

            VideoOptionsIntent.OnSetAsRingtoneDismissed -> {
                dismissSetAsRingtoneDialog()
            }

            VideoOptionsIntent.OnWriteSettingsPermissionReturned -> {
//                setSelectedAudioAsRingtone()
            }

        }
    }

    private fun load(
        mediaList: List<MediaFile>,
        startIndex: Int
    ) {
        if (mediaList.isEmpty()) return

        val safeIndex = startIndex.coerceIn(
            minimumValue = 0,
            maximumValue = mediaList.lastIndex
        )

        val selectedMedia = mediaList.getOrNull(safeIndex)

        resetCurrentPlayerOnly()

        _state.update {
            it.copy(
                mediaList = mediaList,
                currentIndex = safeIndex,
                screenBackgroundColor = resolveScreenBackgroundColor(selectedMedia),
                isPlaying = true,
                isLoading = false,
                position = 0L,
                duration = 0L,
                showBottomSheet = false,
                isAudioRepeatEnabled = false,
                showPlaybackSpeedDialog = false,
                showFileInfoDialog = false,
                fileInfoMediaItem = null,
                showRenameFileDialog = false,
                renameMediaItem = null,
                renameDraftName = "",
                renameError = null,
                isRenamingFile = false,
                showDeleteFileDialog = false,
                deleteMediaItem = null,
                isDeletingFile = false,
                deleteFileError = null,
                isShuffleEnabled = false,
                shuffleQueue = emptyList(),
                shuffleQueuePosition = 0,
            )
        }

        playIndex(safeIndex)

        ensureProgressObserverRunning()

     }

    private fun playIndex(index: Int) {
        val currentState = _state.value

        if (currentState.mediaList.isEmpty()) return
        if (index !in currentState.mediaList.indices) return

        val selectedMedia = currentState.mediaList[index]

        val shouldKeepAudioRepeat = !selectedMedia.isVideo && currentState.isAudioRepeatEnabled

        val shufflePosition = if (currentState.isShuffleEnabled) {
            currentState.shuffleQueue.indexOf(index).takeIf { it >= 0 }
                ?: currentState.shuffleQueuePosition
        } else {
            currentState.shuffleQueuePosition
        }

        _state.update {
            it.copy(
                currentIndex = index,
                shuffleQueuePosition = shufflePosition,
                screenBackgroundColor = resolveScreenBackgroundColor(selectedMedia),
                isPlaying = true,
                isLoading = true,
                position = 0L,
                duration = 0L,
                showBottomSheet = false,
                showPlaybackSpeedDialog = false,
                showFileInfoDialog = false,
                fileInfoMediaItem = null,
                showRenameFileDialog = false,
                renameMediaItem = null,
                renameDraftName = "",
                renameError = null,
                isRenamingFile = false,
                showDeleteFileDialog = false,
                deleteMediaItem = null,
                isDeletingFile = false,
                deleteFileError = null,
                isAudioRepeatEnabled = shouldKeepAudioRepeat,
            )
        }

        playerManager.play(selectedMedia)
        playerManager.setPlaybackSpeed(currentState.playbackSpeed)

        _state.update {
            it.copy(isLoading = false)
        }

        ensureProgressObserverRunning()
    }

    private fun playNext() {
        val currentState = _state.value
        val nextIndex = resolveNextIndex(currentState) ?: return

        playIndex(nextIndex)
    }

    private fun playPrevious() {
        val currentState = _state.value
        val previousIndex = resolvePreviousIndex(currentState) ?: return

        playIndex(previousIndex)
    }

    private fun togglePlayPause() {
        val isCurrentlyPlaying = player.isPlaying

        if (isCurrentlyPlaying) {
            playerManager.pause()
        } else {
            playerManager.play()
        }

        _state.update {
            it.copy(isPlaying = !isCurrentlyPlaying)
        }

        ensureProgressObserverRunning()
    }

    private fun toggleMute() {
        val currentState = _state.value

        if (currentState.isMuted) {
            val restoredVolume = currentState.volume.takeIf { it > 0f } ?: 1f

            playerManager.setVolume(restoredVolume)

            _state.update {
                it.copy(
                    volume = restoredVolume,
                    isMuted = false
                )
            }
        } else {
            playerManager.setVolume(0f)

            _state.update {
                it.copy(isMuted = true)
            }
        }
    }

    private fun updatePlaybackSpeed(speed: Float) {
        val safeSpeed = speed.coerceIn(
            minimumValue = MIN_PLAYBACK_SPEED,
            maximumValue = MAX_PLAYBACK_SPEED
        )

        playerManager.setPlaybackSpeed(safeSpeed)

        _state.update {
            it.copy(
                playbackSpeed = safeSpeed,
                showPlaybackSpeedDialog = false
            )
        }
    }

    private fun openFileInfoDialog() {
        val currentState = _state.value
        val selectedMedia = currentState.mediaList.getOrNull(currentState.currentIndex)

        _state.update {
            it.copy(
                showBottomSheet = false,
                showPlaybackSpeedDialog = false,
                showFileInfoDialog = selectedMedia != null,
                fileInfoMediaItem = selectedMedia
            )
        }
    }

    private fun dismissFileInfoDialog() {
        _state.update {
            it.copy(
                showFileInfoDialog = false,
                fileInfoMediaItem = null
            )
        }
    }

    private fun openRenameDialog() {
        val currentState = _state.value
        val selectedMedia = currentState.mediaList.getOrNull(currentState.currentIndex) ?: return
        val file = File(selectedMedia.filePath)

        _state.update {
            it.copy(
                showBottomSheet = false,
                showPlaybackSpeedDialog = false,
                showFileInfoDialog = false,
                fileInfoMediaItem = null,
                showDeleteFileDialog = false,
                deleteMediaItem = null,
                isDeletingFile = false,
                deleteFileError = null,

                showRenameFileDialog = true,
                renameMediaItem = selectedMedia,
                renameDraftName = file.nameWithoutExtension.ifBlank {
                    selectedMedia.fileName.substringBeforeLast(".")
                },
                renameError = null,
                isRenamingFile = false
            )
        }
    }

    private fun onRenameValueChanged(value: String) {
        _state.update {
            it.copy(
                renameDraftName = value,
                renameError = null
            )
        }
    }

    private fun dismissRenameDialog() {
        _state.update {
            it.copy(
                showRenameFileDialog = false,
                renameMediaItem = null,
                renameDraftName = "",
                renameError = null,
                isRenamingFile = false
            )
        }
    }

  /*  private fun renameSelectedFile() {
        viewModelScope.launch {
            val currentState = _state.value
            val selectedFile = currentState.renameMediaItem

            if (selectedFile == null) {
                dismissRenameDialog()
                return@launch
            }

            val newName = currentState.renameDraftName.trim()

            val validationError = validateRenameFileName(newName)
            if (validationError != null) {
                _state.update {
                    it.copy(renameError = validationError)
                }
                return@launch
            }

            _state.update {
                it.copy(
                    isRenamingFile = true,
                    renameError = null
                )
            }

            when (
                val result = renameMediaFileUseCase(
                    mediaFile = selectedFile,
                    newNameWithoutExtension = newName
                )
            ) {
                is RenameMediaFileResult.Success -> {
                    onRenameSuccess(result.mediaFile)
                }

                is RenameMediaFileResult.RequiresWritePermission -> {
                    _state.update {
                        it.copy(
                            isRenamingFile = false,
                            renameError = null
                        )
                    }

                    _navEvents.emit(
                        MediaPlayerNavEvent.RequestMediaWritePermission(
                            uri = result.uri,
                            pendingIntent = result.pendingIntent
                        )
                    )
                }

                is RenameMediaFileResult.Failure -> {
                    _state.update {
                        it.copy(
                            isRenamingFile = false,
                            renameError = result.message
                        )
                    }
                }
            }
        }
    }*/

    private fun onRenameSuccess(
        renamedFile: MediaFile,
    ) {
        _state.update { currentState ->
            val updatedMediaList = currentState.mediaList.map { media ->
                if (media.id == renamedFile.id) renamedFile else media
            }

            val selectedMedia = updatedMediaList.getOrNull(currentState.currentIndex)

            currentState.copy(
                mediaList = updatedMediaList,
                screenBackgroundColor = resolveScreenBackgroundColor(selectedMedia),
                showRenameFileDialog = false,
                renameMediaItem = null,
                renameDraftName = "",
                renameError = null,
                isRenamingFile = false,
                fileInfoMediaItem = if (currentState.fileInfoMediaItem?.id == renamedFile.id) {
                    renamedFile
                } else {
                    currentState.fileInfoMediaItem
                }
            )
        }
    }

    private fun validateRenameFileName(
        value: String,
    ): String? {
        if (value.isBlank()) {
            return "File name cannot be empty"
        }

        val invalidChars = listOf('/', '\\', ':', '*', '?', '"', '<', '>', '|')
        if (value.any { it in invalidChars }) {
            return "File name contains invalid characters"
        }

        return null
    }

    private fun shareCurrentFile() {
        val currentState = _state.value
        val selectedMedia = currentState.mediaList.getOrNull(currentState.currentIndex) ?: return

        _state.update {
            it.copy(
                showBottomSheet = false,
                showPlaybackSpeedDialog = false,
                showFileInfoDialog = false,
                fileInfoMediaItem = null,
                showRenameFileDialog = false,
                renameMediaItem = null,
                renameDraftName = "",
                renameError = null,
                isRenamingFile = false,
                showDeleteFileDialog = false,
                deleteMediaItem = null,
                isDeletingFile = false,
                deleteFileError = null
            )
        }

        viewModelScope.launch {
            _navEvents.emit(
                MediaPlayerNavEvent.ShareMediaFile(
                    mediaFile = selectedMedia
                )
            )
        }
    }

    private fun openDeleteDialog() {
        val currentState = _state.value
        val selectedMedia = currentState.mediaList.getOrNull(currentState.currentIndex) ?: return

        _state.update {
            it.copy(
                showBottomSheet = false,
                showPlaybackSpeedDialog = false,
                showFileInfoDialog = false,
                fileInfoMediaItem = null,
                showRenameFileDialog = false,
                renameMediaItem = null,
                renameDraftName = "",
                renameError = null,
                isRenamingFile = false,

                showDeleteFileDialog = true,
                deleteMediaItem = selectedMedia,
                isDeletingFile = false,
                deleteFileError = null
            )
        }
    }

    private fun dismissDeleteDialog() {
        _state.update {
            it.copy(
                showDeleteFileDialog = false,
                deleteMediaItem = null,
                isDeletingFile = false,
                deleteFileError = null
            )
        }
    }

  /*  private fun deleteSelectedFile() {
        viewModelScope.launch {
            val selectedFile = _state.value.deleteMediaItem

            if (selectedFile == null) {
                dismissDeleteDialog()
                return@launch
            }

            _state.update {
                it.copy(
                    isDeletingFile = true,
                    deleteFileError = null
                )
            }

            when (
                val result = deleteMediaFileUseCase(selectedFile)
            ) {
                DeleteMediaFileResult.Success -> {
                    onDeleteSuccess()
                }

                is DeleteMediaFileResult.RequiresDeletePermission -> {
                    _state.update {
                        it.copy(
                            isDeletingFile = false,
                            deleteFileError = null
                        )
                    }

                    _navEvents.emit(
                        MediaPlayerNavEvent.RequestMediaDeletePermission(
                            uri = result.uri,
                            pendingIntent = result.pendingIntent
                        )
                    )
                }

                is DeleteMediaFileResult.Failure -> {
                    _state.update {
                        it.copy(
                            isDeletingFile = false,
                            deleteFileError = result.message
                        )
                    }
                }
            }
        }
    }*/

    private fun onDeleteSuccess() {
        val currentState = _state.value
        val deletedFile = currentState.deleteMediaItem

        if (deletedFile == null) {
            dismissDeleteDialog()
            return
        }

        val updatedMediaList = currentState.mediaList.filterNot { media ->
            media.id == deletedFile.id
        }

        if (updatedMediaList.isEmpty()) {
            _state.update {
                it.copy(
                    mediaList = emptyList(),
                    currentIndex = 0,
                    screenBackgroundColor = MediaPlayerState.SCREEN_BACKGROUND_BLACK,
                    showDeleteFileDialog = false,
                    deleteMediaItem = null,
                    isDeletingFile = false,
                    deleteFileError = null,
                    showBottomSheet = false,
                    showPlaybackSpeedDialog = false,
                    showFileInfoDialog = false,
                    fileInfoMediaItem = null,
                    showRenameFileDialog = false,
                    renameMediaItem = null,
                    renameDraftName = "",
                    renameError = null,
                    isRenamingFile = false
                )
            }

            resetCurrentPlayerOnly()

            viewModelScope.launch {
                _navEvents.emit(MediaPlayerNavEvent.CloseMediaPlayer)
            }

            return
        }

        val nextIndex = currentState.currentIndex.coerceAtMost(updatedMediaList.lastIndex)
        val nextMedia = updatedMediaList.getOrNull(nextIndex)

        _state.update {
            it.copy(
                mediaList = updatedMediaList,
                currentIndex = nextIndex,
                screenBackgroundColor = resolveScreenBackgroundColor(nextMedia),
                showDeleteFileDialog = false,
                deleteMediaItem = null,
                isDeletingFile = false,
                deleteFileError = null,
                showBottomSheet = false,
                showPlaybackSpeedDialog = false,
                showFileInfoDialog = false,
                fileInfoMediaItem = null,
                showRenameFileDialog = false,
                renameMediaItem = null,
                renameDraftName = "",
                renameError = null,
                isRenamingFile = false,
                position = 0L,
                duration = 0L,
                isLoading = true
            )
        }

        playerManager.play(updatedMediaList[nextIndex])
        playerManager.setPlaybackSpeed(currentState.playbackSpeed)

        _state.update {
            it.copy(
                isLoading = false,
                isPlaying = true
            )
        }

        ensureProgressObserverRunning()
    }

    private fun startProgressObserver() {
        progressJob?.cancel()

        progressJob = viewModelScope.launch {
            while (isActive) {
                val duration = player.duration.takeIf { it > 0L } ?: 0L
                val position = player.currentPosition.coerceAtLeast(0L)

                _state.update {
                    it.copy(
                        position = position.coerceAtMost(
                            duration.takeIf { value -> value > 0L } ?: position
                        ),
                        duration = duration,
                        isPlaying = player.isPlaying
                    )
                }

                delay(PROGRESS_UPDATE_INTERVAL_MS)
            }
        }
    }

    private fun ensureProgressObserverRunning() {
        if (progressJob?.isActive != true) {
            startProgressObserver()
        }
    }

    private fun resetCurrentPlayerOnly() {
        runCatching {
            playerManager.pause()
            playerManager.seekTo(0L)
            playerManager.reset()
        }

        ensureProgressObserverRunning()
    }

    private fun closePlayerSession() {
        progressJob?.cancel()
        progressJob = null

        playerListener?.let { listener ->
            player.removeListener(listener)
        }
        playerListener = null

        runCatching {
            playerManager.pause()
            playerManager.seekTo(0L)
            playerManager.release()
        }

        playerManager = MediaPlayerManager(getApplication())

        _state.value = MediaPlayerState()

        observePlayerCompletion()
        startProgressObserver()
    }

    private fun openSetAsRingtoneDialog() {
        val currentState = _state.value
        val selectedMedia = currentState.mediaList.getOrNull(currentState.currentIndex) ?: return

        if (selectedMedia.isVideo) {
            return
        }

        _state.update {
            it.copy(
                showBottomSheet = false,
                showPlaybackSpeedDialog = false,
                showFileInfoDialog = false,
                fileInfoMediaItem = null,
                showRenameFileDialog = false,
                renameMediaItem = null,
                renameDraftName = "",
                renameError = null,
                isRenamingFile = false,
                showDeleteFileDialog = false,
                deleteMediaItem = null,
                isDeletingFile = false,
                deleteFileError = null,

                showSetAsRingtoneDialog = true,
                ringtoneMediaItem = selectedMedia,
                selectedRingtoneTargetType = RingtoneTargetType.DefaultRingtone,
                isSettingRingtone = false,
                setRingtoneError = null
            )
        }
    }

    private fun dismissSetAsRingtoneDialog() {
        _state.update {
            it.copy(
                showSetAsRingtoneDialog = false,
                ringtoneMediaItem = null,
                selectedRingtoneTargetType = RingtoneTargetType.DefaultRingtone,
                isSettingRingtone = false,
                setRingtoneError = null
            )
        }
    }



/*    private fun setSelectedAudioAsRingtone() {
        viewModelScope.launch {
            val currentState = _state.value
            val selectedMedia = currentState.ringtoneMediaItem

            if (selectedMedia == null) {
                dismissSetAsRingtoneDialog()
                return@launch
            }

            if (selectedMedia.isVideo) {
                _state.update {
                    it.copy(
                        isSettingRingtone = false,
                        setRingtoneError = "Only audio files can be set as ringtone"
                    )
                }
                return@launch
            }

            _state.update {
                it.copy(
                    isSettingRingtone = true,
                    setRingtoneError = null
                )
            }

            when (
                val result = setAudioAsRingtoneUseCase(
                    mediaFile = selectedMedia,
                    targetType = currentState.selectedRingtoneTargetType
                )
            ) {
                SetRingtoneResult.Success -> {
                    _state.update {
                        it.copy(
                            showSetAsRingtoneDialog = false,
                            ringtoneMediaItem = null,
                            selectedRingtoneTargetType = RingtoneTargetType.DefaultRingtone,
                            isSettingRingtone = false,
                            setRingtoneError = null
                        )
                    }
                }

                SetRingtoneResult.RequiresWriteSettingsPermission -> {
                    _state.update {
                        it.copy(
                            isSettingRingtone = false,
                            setRingtoneError = null
                        )
                    }

                    _navEvents.emit(
                        MediaPlayerNavEvent.RequestWriteSettingsPermission
                    )
                }

                is SetRingtoneResult.Failure -> {
                    _state.update {
                        it.copy(
                            isSettingRingtone = false,
                            setRingtoneError = result.message
                        )
                    }
                }
            }
        }
    }*/


    private fun resolveScreenBackgroundColor(
        mediaFile: MediaFile?
    ): Long {
        return if (mediaFile?.isActuallyVideo() == true) {
            MediaPlayerState.SCREEN_BACKGROUND_BLACK
        } else {
            MediaPlayerState.SCREEN_BACKGROUND_WHITE
        }
    }

    private fun MediaFile.isActuallyVideo(): Boolean {
        val lowerName = fileName.lowercase()
        val lowerPath = filePath.lowercase()

        return isVideo ||
                lowerName.endsWith(".mp4") ||
                lowerName.endsWith(".mkv") ||
                lowerName.endsWith(".mov") ||
                lowerName.endsWith(".webm") ||
                lowerName.endsWith(".avi") ||
                lowerName.endsWith(".3gp") ||
                lowerName.endsWith(".m4v") ||
                lowerPath.endsWith(".mp4") ||
                lowerPath.endsWith(".mkv") ||
                lowerPath.endsWith(".mov") ||
                lowerPath.endsWith(".webm") ||
                lowerPath.endsWith(".avi") ||
                lowerPath.endsWith(".3gp") ||
                lowerPath.endsWith(".m4v")
    }

    private fun observePlayerCompletion() {
        playerListener?.let { listener ->
            player.removeListener(listener)
        }

        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                if (playbackState != Player.STATE_ENDED) return

                val currentState = _state.value
                val currentMedia = currentState.mediaList.getOrNull(currentState.currentIndex)

                if (currentMedia?.isVideo == true) return

                playNextFromCompletion()
            }
        }

        playerListener = listener
        player.addListener(listener)
    }

    private fun playNextFromCompletion() {
        val currentState = _state.value

        if (currentState.mediaList.isEmpty()) return

        val currentMedia = currentState.mediaList.getOrNull(currentState.currentIndex)

        if (currentMedia?.isVideo == true) {
            return
        }

        if (currentState.isAudioRepeatEnabled) {
            playerManager.seekTo(0L)
            playerManager.play()

            _state.update {
                it.copy(
                    isPlaying = true,
                    position = 0L
                )
            }

            ensureProgressObserverRunning()
            return
        }

        val nextIndex = resolveNextIndex(currentState)

        if (nextIndex == null) {
            playerManager.pause()

            _state.update {
                it.copy(
                    isPlaying = false,
                    position = 0L
                )
            }
            return
        }

        playIndex(nextIndex)
    }
    private fun toggleShuffle() {
        val currentState = _state.value

        if (currentState.mediaList.isEmpty()) return

        if (currentState.isShuffleEnabled) {
            _state.update {
                it.copy(
                    isShuffleEnabled = false,
                    shuffleQueue = emptyList(),
                    shuffleQueuePosition = 0
                )
            }
            return
        }

        val currentIndex = currentState.currentIndex.coerceIn(
            minimumValue = 0,
            maximumValue = currentState.mediaList.lastIndex
        )

        val remainingIndexes = currentState.mediaList.indices
            .filterNot { index -> index == currentIndex }
            .shuffled()

        val shuffleQueue = listOf(currentIndex) + remainingIndexes

        _state.update {
            it.copy(
                isShuffleEnabled = true,
                shuffleQueue = shuffleQueue,
                shuffleQueuePosition = 0
            )
        }
    }

    private fun resolveNextIndex(
        currentState: MediaPlayerState
    ): Int? {
        if (currentState.mediaList.isEmpty()) return null

        if (currentState.isShuffleEnabled) {
            val nextShufflePosition = currentState.shuffleQueuePosition + 1
            val nextIndex = currentState.shuffleQueue.getOrNull(nextShufflePosition)

            if (nextIndex == null) return null
            if (nextIndex !in currentState.mediaList.indices) return null

            _state.update {
                it.copy(
                    shuffleQueuePosition = nextShufflePosition
                )
            }

            return nextIndex
        }

        val nextIndex = currentState.currentIndex + 1

        return if (nextIndex <= currentState.mediaList.lastIndex) {
            nextIndex
        } else {
            null
        }
    }

    private fun resolvePreviousIndex(
        currentState: MediaPlayerState
    ): Int? {
        if (currentState.mediaList.isEmpty()) return null

        if (currentState.isShuffleEnabled) {
            val previousShufflePosition = currentState.shuffleQueuePosition - 1
            val previousIndex = currentState.shuffleQueue.getOrNull(previousShufflePosition)

            if (previousIndex == null) return null
            if (previousIndex !in currentState.mediaList.indices) return null

            _state.update {
                it.copy(
                    shuffleQueuePosition = previousShufflePosition
                )
            }

            return previousIndex
        }

        val previousIndex = currentState.currentIndex - 1

        return if (previousIndex >= 0) {
            previousIndex
        } else {
            null
        }
    }

    override fun onCleared() {
        progressJob?.cancel()
        progressJob = null

        playerListener?.let { listener ->
            player.removeListener(listener)
        }
        playerListener = null

        runCatching {
            playerManager.release()
        }

        super.onCleared()
    }

    companion object {
        private const val PROGRESS_UPDATE_INTERVAL_MS = 300L
        private const val MIN_PLAYBACK_SPEED = 0.25f
        private const val MAX_PLAYBACK_SPEED = 4f
        private const val TAG = "MediaPlayerViewModel"
    }
}
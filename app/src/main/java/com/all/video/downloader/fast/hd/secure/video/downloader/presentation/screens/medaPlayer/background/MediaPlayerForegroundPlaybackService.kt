package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.background

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.core.MainActivity

class MediaPlayerForegroundPlaybackService : Service() {

    private lateinit var mediaSession: MediaSessionCompat

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        createMediaSession()

        // Critical watchdog safety:
        // startForegroundService ke baad service immediately foreground ban jaye.
        startForegroundSafely(
            title = getString(R.string.media_player_foreground_title),
            isPlaying = true,
            canPlayPrevious = false,
            canPlayNext = false
        )
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int,
    ): Int {
        if (intent?.action == ACTION_STOP_SERVICE) {
            stopForegroundPlayback()
            return START_NOT_STICKY
        }

        val title = intent
            ?.getStringExtra(EXTRA_MEDIA_TITLE)
            ?.takeIf { it.isNotBlank() }
            ?: getString(R.string.media_player_foreground_title)

        val isPlaying = intent?.getBooleanExtra(EXTRA_IS_PLAYING, true) ?: true
        val canPlayPrevious = intent?.getBooleanExtra(EXTRA_CAN_PLAY_PREVIOUS, false) ?: false
        val canPlayNext = intent?.getBooleanExtra(EXTRA_CAN_PLAY_NEXT, false) ?: false

        startForegroundSafely(
            title = title,
            isPlaying = isPlaying,
            canPlayPrevious = canPlayPrevious,
            canPlayNext = canPlayNext
        )

        return START_STICKY
    }

    override fun onDestroy() {
        runCatching {
            mediaSession.isActive = false
            mediaSession.release()
        }

        super.onDestroy()
    }

    private fun createMediaSession() {
        mediaSession = MediaSessionCompat(
            this,
            MEDIA_SESSION_TAG
        ).apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            isActive = true
        }
    }

    private fun startForegroundSafely(
        title: String,
        isPlaying: Boolean,
        canPlayPrevious: Boolean,
        canPlayNext: Boolean,
    ) {
        updateMediaSession(
            title = title,
            isPlaying = isPlaying,
            canPlayPrevious = canPlayPrevious,
            canPlayNext = canPlayNext
        )

        val notification = buildNotification(
            title = title,
            isPlaying = isPlaying,
            canPlayPrevious = canPlayPrevious,
            canPlayNext = canPlayNext
        )

        runCatching {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                startForeground(
                    NOTIFICATION_ID,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
                )
            } else {
                startForeground(
                    NOTIFICATION_ID,
                    notification
                )
            }
        }.onFailure {
            stopSelf()
        }
    }

    private fun updateMediaSession(
        title: String,
        isPlaying: Boolean,
        canPlayPrevious: Boolean,
        canPlayNext: Boolean,
    ) {
        mediaSession.setMetadata(
            MediaMetadataCompat.Builder()
                .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
                .putString(
                    MediaMetadataCompat.METADATA_KEY_ARTIST,
                    getString(R.string.app_name)
                )
                .build()
        )

        val availableActions = buildPlaybackStateActions(
            canPlayPrevious = canPlayPrevious,
            canPlayNext = canPlayNext
        )

        mediaSession.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(
                    if (isPlaying) {
                        PlaybackStateCompat.STATE_PLAYING
                    } else {
                        PlaybackStateCompat.STATE_PAUSED
                    },
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    DEFAULT_PLAYBACK_SPEED
                )
                .setActions(availableActions)
                .build()
        )
    }

    private fun buildPlaybackStateActions(
        canPlayPrevious: Boolean,
        canPlayNext: Boolean,
    ): Long {
        var actions = PlaybackStateCompat.ACTION_PLAY_PAUSE or
                PlaybackStateCompat.ACTION_PLAY or
                PlaybackStateCompat.ACTION_PAUSE or
                PlaybackStateCompat.ACTION_STOP

        if (canPlayPrevious) {
            actions = actions or PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        }

        if (canPlayNext) {
            actions = actions or PlaybackStateCompat.ACTION_SKIP_TO_NEXT
        }

        return actions
    }

    private fun stopForegroundPlayback() {
        runCatching {
            mediaSession.isActive = false
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }

        stopSelf()
    }

    private fun buildNotification(
        title: String,
        isPlaying: Boolean,
        canPlayPrevious: Boolean,
        canPlayNext: Boolean,
    ): Notification {
        val contentIntent = PendingIntent.getActivity(
            this,
            OPEN_PLAYER_REQUEST_CODE,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_music_node)
            .setContentTitle(title)
            .setContentText(getString(R.string.media_player_foreground_subtitle))
            .setSubText(getString(R.string.app_name))
            .setContentIntent(contentIntent)
            .setCategory(NotificationCompat.CATEGORY_TRANSPORT)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOnlyAlertOnce(true)
            .setSilent(true)
            .setShowWhen(false)
            .setOngoing(true)

        val compactActionIndexes = mutableListOf<Int>()
        var actionIndex = 0

        if (canPlayPrevious) {
            builder.addAction(
                createNotificationAction(
                    action = MediaPlayerForegroundPlaybackAction.Previous,
                    iconRes = android.R.drawable.ic_media_previous,
                    title = getString(R.string.media_player_foreground_action_previous)
                )
            )
            compactActionIndexes += actionIndex
            actionIndex++
        }

        builder.addAction(
            createNotificationAction(
                action = MediaPlayerForegroundPlaybackAction.PlayPause,
                iconRes = if (isPlaying) {
                    android.R.drawable.ic_media_pause
                } else {
                    android.R.drawable.ic_media_play
                },
                title = if (isPlaying) {
                    getString(R.string.media_player_foreground_action_pause)
                } else {
                    getString(R.string.media_player_foreground_action_play)
                }
            )
        )
        compactActionIndexes += actionIndex
        actionIndex++

        if (canPlayNext) {
            builder.addAction(
                createNotificationAction(
                    action = MediaPlayerForegroundPlaybackAction.Next,
                    iconRes = android.R.drawable.ic_media_next,
                    title = getString(R.string.media_player_foreground_action_next)
                )
            )
            compactActionIndexes += actionIndex
            actionIndex++
        }

        builder.addAction(
            createNotificationAction(
                action = MediaPlayerForegroundPlaybackAction.Stop,
                iconRes = android.R.drawable.ic_menu_close_clear_cancel,
                title = getString(R.string.media_player_foreground_action_stop)
            )
        )

        builder.setStyle(
            MediaStyle()
                .setMediaSession(mediaSession.sessionToken)
                .setShowActionsInCompactView(
                    *compactActionIndexes
                        .take(MAX_COMPACT_ACTIONS)
                        .toIntArray()
                )
                .setShowCancelButton(false)
        )

        return builder.build()
    }

    private fun createNotificationAction(
        action: MediaPlayerForegroundPlaybackAction,
        iconRes: Int,
        title: String,
    ): NotificationCompat.Action {
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            action.toRequestCode(),
            Intent(this, MediaPlayerForegroundPlaybackActionReceiver::class.java).apply {
                this.action = action.toBroadcastAction()
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Action.Builder(
            iconRes,
            title,
            pendingIntent
        ).build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.media_player_foreground_channel_name),
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = getString(R.string.media_player_foreground_channel_description)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            setShowBadge(false)
        }

        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "media_player_foreground_playback"
        private const val NOTIFICATION_ID = 9201
        private const val OPEN_PLAYER_REQUEST_CODE = 9202
        private const val MAX_COMPACT_ACTIONS = 3
        private const val DEFAULT_PLAYBACK_SPEED = 1f
        private const val MEDIA_SESSION_TAG = "VideoDownloaderAudioPlaybackSession"

        private const val ACTION_START_SERVICE =
            "com.all.video.downloader.fast.hd.secure.video.downloader.action.MEDIA_PLAYER_FOREGROUND_START_SERVICE"

        private const val ACTION_STOP_SERVICE =
            "com.all.video.downloader.fast.hd.secure.video.downloader.action.MEDIA_PLAYER_FOREGROUND_STOP_SERVICE"

        private const val EXTRA_MEDIA_TITLE = "extra_media_title"
        private const val EXTRA_IS_PLAYING = "extra_is_playing"
        private const val EXTRA_CAN_PLAY_PREVIOUS = "extra_can_play_previous"
        private const val EXTRA_CAN_PLAY_NEXT = "extra_can_play_next"

        fun startIntent(
            context: Context,
            title: String,
            isPlaying: Boolean,
            canPlayPrevious: Boolean,
            canPlayNext: Boolean,
        ): Intent {
            return Intent(context, MediaPlayerForegroundPlaybackService::class.java).apply {
                action = ACTION_START_SERVICE
                putExtra(EXTRA_MEDIA_TITLE, title)
                putExtra(EXTRA_IS_PLAYING, isPlaying)
                putExtra(EXTRA_CAN_PLAY_PREVIOUS, canPlayPrevious)
                putExtra(EXTRA_CAN_PLAY_NEXT, canPlayNext)
            }
        }

        fun stopIntent(
            context: Context,
        ): Intent {
            return Intent(context, MediaPlayerForegroundPlaybackService::class.java).apply {
                action = ACTION_STOP_SERVICE
            }
        }

        fun plainIntent(
            context: Context,
        ): Intent {
            return Intent(context, MediaPlayerForegroundPlaybackService::class.java)
        }
    }
}
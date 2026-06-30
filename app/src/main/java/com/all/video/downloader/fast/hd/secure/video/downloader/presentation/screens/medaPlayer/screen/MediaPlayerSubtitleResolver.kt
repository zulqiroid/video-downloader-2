package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.screen

import android.net.Uri
import androidx.media3.common.MimeTypes
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerSubtitleSource
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerSubtitleState
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.medaPlayer.states.MediaPlayerSubtitleTrackState
import java.io.File
import java.util.Locale
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.provider.OpenableColumns

class MediaPlayerSubtitleResolver {

    fun resolveLocalSubtitles(
        media: MediaFile?,
        previousState: MediaPlayerSubtitleState = MediaPlayerSubtitleState(),
    ): MediaPlayerSubtitleState {
        if (media == null || !media.isVideo) {
            return MediaPlayerSubtitleState()
        }

        val videoFile = File(media.filePath)
        val parentDirectory = videoFile.parentFile

        if (
            parentDirectory == null ||
            !parentDirectory.exists() ||
            !parentDirectory.isDirectory
        ) {
            return MediaPlayerSubtitleState(
                errorMessage = "Subtitle folder is not available."
            )
        }

        val videoBaseName = videoFile.nameWithoutExtension.normalizeFileName()

        val tracks = parentDirectory
            .listFiles()
            .orEmpty()
            .asSequence()
            .filter { file ->
                file.isFile && file.canRead()
            }
            .mapNotNull { file ->
                file.toSubtitleTrackOrNull(videoBaseName)
            }
            .sortedWith(
                compareByDescending<MediaPlayerSubtitleTrackState> { track ->
                    track.fileName
                        .substringBeforeLast(".")
                        .normalizeFileName() == videoBaseName
                }.thenBy { track ->
                    track.fileName.lowercase(Locale.US)
                }
            )
            .take(MAX_SUBTITLE_TRACKS)
            .toList()

        val selectedTrackId = when {
            tracks.isEmpty() -> null

            previousState.selectedTrackId != null &&
                tracks.any { it.id == previousState.selectedTrackId } -> {
                previousState.selectedTrackId
            }

            previousState.isEnabled -> {
                tracks.firstOrNull()?.id
            }

            else -> null
        }

        return MediaPlayerSubtitleState(
            tracks = tracks,
            selectedTrackId = selectedTrackId,
            isEnabled = previousState.isEnabled && selectedTrackId != null,
            errorMessage = null
        )
    }

    fun resolvePickedSubtitle(
        context: Context,
        uri: Uri,
        previousState: MediaPlayerSubtitleState,
    ): MediaPlayerSubtitleState {
        persistSubtitleReadPermission(
            context = context,
            uri = uri
        )

        val pickedTrack = uri.toPickedSubtitleTrackOrNull(context)

        if (pickedTrack == null) {
            return previousState.copy(
                errorMessage = "Selected file is not a supported subtitle format."
            )
        }

        val updatedTracks = buildList {
            add(pickedTrack)

            previousState.tracks.forEach { track ->
                if (track.id != pickedTrack.id) {
                    add(track)
                }
            }
        }.take(MAX_SUBTITLE_TRACKS)

        return previousState.copy(
            tracks = updatedTracks,
            selectedTrackId = pickedTrack.id,
            isEnabled = true,
            errorMessage = null
        )
    }

    private fun File.toSubtitleTrackOrNull(
        videoBaseName: String,
    ): MediaPlayerSubtitleTrackState? {
        val extension = extension.lowercase(Locale.US)
        val mimeType = extension.toSubtitleMimeType() ?: return null

        val subtitleBaseName = nameWithoutExtension.normalizeFileName()

        val isRelatedToCurrentVideo =
            subtitleBaseName == videoBaseName ||
                subtitleBaseName.startsWith("$videoBaseName.") ||
                subtitleBaseName.startsWith("$videoBaseName-") ||
                subtitleBaseName.startsWith("${videoBaseName}_")

        if (!isRelatedToCurrentVideo) return null

        return MediaPlayerSubtitleTrackState(
            id = "local:${absolutePath}",
            uriString = Uri.fromFile(this).toString(),
            fileName = name,
            mimeType = mimeType,
            languageLabel = resolveLanguageLabel(
                videoBaseName = videoBaseName,
                subtitleBaseName = subtitleBaseName
            ),
            source = MediaPlayerSubtitleSource.LocalFile
        )
    }

    private fun String.toSubtitleMimeType(): String? {
        return when (this) {
            "srt" -> MimeTypes.APPLICATION_SUBRIP
            "vtt" -> MimeTypes.TEXT_VTT
            "ssa", "ass" -> MimeTypes.TEXT_SSA
            "ttml", "xml", "dfxp" -> MimeTypes.APPLICATION_TTML
            else -> null
        }
    }

    private fun resolveLanguageLabel(
        videoBaseName: String,
        subtitleBaseName: String,
    ): String {
        val languageCode = subtitleBaseName
            .removePrefix(videoBaseName)
            .trim('.', '-', '_')
            .substringBefore('.')
            .takeIf { value ->
                value.length in MIN_LANGUAGE_CODE_LENGTH..MAX_LANGUAGE_CODE_LENGTH &&
                    value.all { char -> char.isLetter() }
            }

        if (languageCode.isNullOrBlank()) {
            return DEFAULT_SUBTITLE_LANGUAGE_LABEL
        }

        val locale = Locale(languageCode.lowercase(Locale.US))
        val displayLanguage = locale.getDisplayLanguage(Locale.getDefault())

        return displayLanguage
            .takeIf { it.isNotBlank() && it != languageCode }
            ?.replaceFirstChar { char ->
                if (char.isLowerCase()) {
                    char.titlecase(Locale.getDefault())
                } else {
                    char.toString()
                }
            }
            ?: languageCode.uppercase(Locale.US)
    }

    private fun String.normalizeFileName(): String {
        return trim()
            .lowercase(Locale.US)
    }

    private fun persistSubtitleReadPermission(
        context: Context,
        uri: Uri,
    ) {
        if (uri.scheme != ContentResolver.SCHEME_CONTENT) return

        runCatching {
            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
    }

    private fun Uri.toPickedSubtitleTrackOrNull(
        context: Context,
    ): MediaPlayerSubtitleTrackState? {
        val displayName = resolveDisplayName(context)
            ?.takeIf { it.isNotBlank() }
            ?: return null

        val extension = displayName
            .substringAfterLast(
                delimiter = ".",
                missingDelimiterValue = ""
            )
            .lowercase(Locale.US)

        val mimeType = extension.toSubtitleMimeType()
            ?: context.contentResolver
                .getType(this)
                ?.toSubtitleMimeTypeFromContentType()
            ?: return null

        return MediaPlayerSubtitleTrackState(
            id = "picked:$this",
            uriString = toString(),
            fileName = displayName,
            mimeType = mimeType,
            languageLabel = resolvePickedSubtitleLanguageLabel(displayName),
            source = MediaPlayerSubtitleSource.DocumentPicker
        )
    }

    private fun Uri.resolveDisplayName(
        context: Context,
    ): String? {
        if (scheme == ContentResolver.SCHEME_CONTENT) {
            runCatching {
                context.contentResolver.query(
                    this,
                    arrayOf(OpenableColumns.DISPLAY_NAME),
                    null,
                    null,
                    null
                )?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

                    if (nameIndex >= 0 && cursor.moveToFirst()) {
                        cursor.getString(nameIndex)
                    } else {
                        null
                    }
                }
            }.getOrNull()?.let { name ->
                return name
            }
        }

        return lastPathSegment
            ?.substringAfterLast("/")
            ?.substringAfterLast(":")
    }

    private fun String.toSubtitleMimeTypeFromContentType(): String? {
        return when (lowercase(Locale.US)) {
            MimeTypes.APPLICATION_SUBRIP,
            "application/x-subrip",
            "text/srt" -> MimeTypes.APPLICATION_SUBRIP

            MimeTypes.TEXT_VTT,
            "text/vtt" -> MimeTypes.TEXT_VTT

            MimeTypes.TEXT_SSA,
            "text/x-ssa",
            "text/x-ass",
            "application/ssa",
            "application/ass" -> MimeTypes.TEXT_SSA

            MimeTypes.APPLICATION_TTML,
            "application/ttml+xml",
            "text/ttml" -> MimeTypes.APPLICATION_TTML

            else -> null
        }
    }

    private fun resolvePickedSubtitleLanguageLabel(
        displayName: String,
    ): String {
        val baseName = displayName
            .substringBeforeLast(
                delimiter = ".",
                missingDelimiterValue = displayName
            )
            .lowercase(Locale.US)

        val possibleCode = baseName
            .substringAfterLast(".", "")
            .substringAfterLast("_")
            .substringAfterLast("-")
            .takeIf { value ->
                value.length in MIN_LANGUAGE_CODE_LENGTH..MAX_LANGUAGE_CODE_LENGTH &&
                        value.all { char -> char.isLetter() }
            }

        if (possibleCode.isNullOrBlank()) {
            return DEFAULT_SUBTITLE_LANGUAGE_LABEL
        }

        val locale = Locale(possibleCode.lowercase(Locale.US))
        val displayLanguage = locale.getDisplayLanguage(Locale.getDefault())

        return displayLanguage
            .takeIf { it.isNotBlank() && it != possibleCode }
            ?.replaceFirstChar { char ->
                if (char.isLowerCase()) {
                    char.titlecase(Locale.getDefault())
                } else {
                    char.toString()
                }
            }
            ?: possibleCode.uppercase(Locale.US)
    }

    private companion object {
        private const val MAX_SUBTITLE_TRACKS = 25
        private const val MIN_LANGUAGE_CODE_LENGTH = 2
        private const val MAX_LANGUAGE_CODE_LENGTH = 3
        private const val DEFAULT_SUBTITLE_LANGUAGE_LABEL = "Subtitle"
    }
}
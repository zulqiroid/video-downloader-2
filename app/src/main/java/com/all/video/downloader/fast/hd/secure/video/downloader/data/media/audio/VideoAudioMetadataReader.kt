package com.all.video.downloader.fast.hd.secure.video.downloader.data.media.audio

import android.content.Context
import android.media.MediaExtractor
import android.media.MediaFormat
import android.net.Uri
import android.provider.OpenableColumns
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.videoToMp3.VideoAudioMetadata
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class VideoAudioMetadataReader @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun read(uriString: String): VideoAudioMetadata {
        val uri = Uri.parse(uriString)
        val fileName = queryDisplayName(uri)

        val extractor = MediaExtractor()

        try {
            extractor.setDataSource(context, uri, null)

            for (index in 0 until extractor.trackCount) {
                val format = extractor.getTrackFormat(index)
                val mime = format.getString(MediaFormat.KEY_MIME).orEmpty()

                if (!mime.startsWith("audio/")) continue

                return VideoAudioMetadata(
                    fileName = fileName,
                    durationUs = format.getLongOrDefault(MediaFormat.KEY_DURATION),
                    audioMime = mime,
                    sampleRate = format.getIntOrDefault(MediaFormat.KEY_SAMPLE_RATE, 44_100),
                    channelCount = format.getIntOrDefault(MediaFormat.KEY_CHANNEL_COUNT, 2),
                    bitrate = format.getIntOrNull(MediaFormat.KEY_BIT_RATE)
                )
            }

            throw IllegalStateException("This video does not contain an audio track.")
        } finally {
            extractor.release()
        }
    }

    private fun queryDisplayName(uri: Uri): String {
        val projection = arrayOf(OpenableColumns.DISPLAY_NAME)

        context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            null
        )?.use { cursor ->
            val index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)

            if (cursor.moveToFirst() && index != -1) {
                return cursor.getString(index).orEmpty()
            }
        }

        return "video_${System.currentTimeMillis()}.mp4"
    }

    private fun MediaFormat.getIntOrDefault(
        key: String,
        defaultValue: Int
    ): Int {
        return if (containsKey(key)) getInteger(key) else defaultValue
    }

    private fun MediaFormat.getIntOrNull(key: String): Int? {
        return if (containsKey(key)) getInteger(key) else null
    }

    private fun MediaFormat.getLongOrDefault(key: String): Long {
        return if (containsKey(key)) getLong(key) else 0L
    }
}
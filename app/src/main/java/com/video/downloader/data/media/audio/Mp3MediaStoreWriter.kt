package com.video.downloader.data.media.audio

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.video.downloader.di.IoDispatcher
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class Mp3MediaStoreWriter @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) {

    suspend fun saveTempMp3ToMusic(
        tempFilePath: String,
        outputNameWithoutExtension: String
    ): String = withContext(ioDispatcher) {
        val tempFile = File(tempFilePath)

        require(tempFile.exists()) {
            "Converted MP3 file not found."
        }

        require(tempFile.length() > 0L) {
            "Converted MP3 file is empty."
        }

        val cleanName = outputNameWithoutExtension
            .ifBlank { "audio_${System.currentTimeMillis()}" }
            .substringBeforeLast(".")
            .replace(Regex("[\\\\/:*?\"<>|]"), "_")
            .trim()
            .ifBlank { "audio_${System.currentTimeMillis()}" }

        val finalFileName = "$cleanName.mp3"
        val resolver = context.contentResolver

        val values = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, finalFileName)
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Audio.Media.RELATIVE_PATH,
                    "${Environment.DIRECTORY_MUSIC}/VideoToAudio"
                )
                put(MediaStore.Audio.Media.IS_PENDING, 1)
            }
        }

        val collectionUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val outputUri = resolver.insert(collectionUri, values)
            ?: error("Unable to create MP3 file.")

        try {
            resolver.openOutputStream(outputUri)?.use { outputStream ->
                tempFile.inputStream().use { inputStream ->
                    inputStream.copyTo(outputStream)
                }
            } ?: error("Unable to open MP3 output stream.")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val publishValues = ContentValues().apply {
                    put(MediaStore.Audio.Media.IS_PENDING, 0)
                }

                resolver.update(
                    outputUri,
                    publishValues,
                    null,
                    null
                )
            }

            tempFile.delete()

            outputUri.toString()
        } catch (throwable: Throwable) {
            resolver.delete(outputUri, null, null)
            throw throwable
        }
    }
}
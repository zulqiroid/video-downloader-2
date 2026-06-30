package com.all.video.downloader.fast.hd.secure.video.downloader.data.media.videoSplitter

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class VideoSplitMediaStoreWriter @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun saveClipToMovies(
        tempFile: File,
        fileName: String
    ): Uri {
        require(tempFile.exists()) {
            "Split clip is no longer available."
        }

        require(tempFile.length() > 0L) {
            "Split clip is empty."
        }

        val cleanFileName = sanitizeFileName(
            name = fileName.ifBlank {
                "Split_Clip_${System.currentTimeMillis()}.mp4"
            }
        ).let { name ->
            if (name.endsWith(".mp4", ignoreCase = true)) {
                name
            } else {
                "$name.mp4"
            }
        }

        val resolver = context.contentResolver

        val values = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, cleanFileName)
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
            put(MediaStore.Video.Media.DATE_ADDED, System.currentTimeMillis() / 1000L)
            put(MediaStore.Video.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000L)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Video.Media.RELATIVE_PATH,
                    "${Environment.DIRECTORY_MOVIES}/VideoSplitter"
                )
                put(MediaStore.Video.Media.IS_PENDING, 1)
            } else {
                val moviesDir = File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MOVIES
                    ),
                    "VideoSplitter"
                ).apply {
                    if (!exists()) mkdirs()
                }

                val outputFile = File(moviesDir, cleanFileName)
                put(MediaStore.Video.Media.DATA, outputFile.absolutePath)
            }
        }

        val outputUri = resolver.insert(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            values
        ) ?: throw IllegalStateException("Unable to create output video file.")

        try {
            resolver.openOutputStream(outputUri)?.use { output ->
                tempFile.inputStream().use { input ->
                    input.copyTo(output)
                }
            } ?: throw IllegalStateException("Unable to write output video file.")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val publishValues = ContentValues().apply {
                    put(MediaStore.Video.Media.IS_PENDING, 0)
                }

                resolver.update(
                    outputUri,
                    publishValues,
                    null,
                    null
                )
            }

            return outputUri
        } catch (throwable: Throwable) {
            resolver.delete(outputUri, null, null)
            throw throwable
        }
    }

    private fun sanitizeFileName(name: String): String {
        return name
            .replace(Regex("[\\\\/:*?\"<>|]"), "_")
            .trim()
            .ifBlank {
                "Split_Clip_${System.currentTimeMillis()}.mp4"
            }
    }
}
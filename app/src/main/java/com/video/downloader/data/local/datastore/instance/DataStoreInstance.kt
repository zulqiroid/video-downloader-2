package com.video.downloader.data.local.datastore.instance

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.datastore by preferencesDataStore("video_downloader_datastore")
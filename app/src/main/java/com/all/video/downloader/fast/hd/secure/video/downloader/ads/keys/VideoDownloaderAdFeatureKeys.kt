package com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys

import com.core.ads.domain.screen.AdFeatureKey

object VideoDownloaderAdFeatureKeys {

    /**
     * Startup / onboarding.
     */
    val APP_LANGUAGE_CONTINUE = AdFeatureKey.require("app_language_continue")
    val ONBOARDING_COMPLETE = AdFeatureKey.require("onboarding_complete")

    /**
     * Downloader flow.
     */
    val PLATFORM_DETAIL_OPEN = AdFeatureKey.require("platform_detail_open")
    val VIDEO_FETCH_SUCCESS = AdFeatureKey.require("video_fetch_success")
    val DOWNLOAD_OPTIONS_OPEN = AdFeatureKey.require("download_options_open")
    /**
     * User opens a completed downloaded video/audio into media player.
     *
     * This is different from file_save_success.
     * file_save_success may happen in background, so we avoid showing fullscreen ads there.
     */
    val DOWNLOAD_MEDIA_OPEN = AdFeatureKey.require("download_media_open")

     val FILE_SAVE_SUCCESS = AdFeatureKey.require("file_save_success")

    /**
     * Tools.
     */
    val VIDEO_TO_MP3_ENTRY = AdFeatureKey.require("video_to_mp3_entry")
    val VIDEO_TO_MP3_RESULT = AdFeatureKey.require("video_to_mp3_result")

    val VIDEO_SPLITTER_ENTRY = AdFeatureKey.require("video_splitter_entry")
    val VIDEO_SPLITTER_RESULT = AdFeatureKey.require("video_splitter_result")

    val SCREEN_CASTING_ENTRY = AdFeatureKey.require("screen_casting_entry")

    /**
     * Premium.
     *
     * Use carefully. Premium screen par aggressive ads UX kharab kar sakti hain.
     */
    val PREMIUM_OPEN = AdFeatureKey.require("premium_open")
}
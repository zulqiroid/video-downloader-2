package com.all.video.downloader.fast.hd.secure.video.downloader.ads.keys

import com.core.ads.domain.screen.AdScreenKey

object VideoDownloaderAdScreenKeys {

    /**
     * Main bottom tabs.
     */
    val HOME = AdScreenKey.require("home")
    val PLAYER = AdScreenKey.require("player")
    val REELS = AdScreenKey.require("reels")
    val FILES = AdScreenKey.require("files")
    val VAULT = AdScreenKey.require("vault")
    val MORE = AdScreenKey.require("more")

    /**
     * Startup / onboarding flow.
     */
    val SPLASH = AdScreenKey.require("splash")
    val APP_LANGUAGE = AdScreenKey.require("app_language")
    val ONBOARDING = AdScreenKey.require("onboarding")
    val PREMIUM = AdScreenKey.require("premium")

    /**
     * Downloader / platform screens.
     */
    val DOWNLOAD_GUIDE = AdScreenKey.require("download_guide")
    val PLATFORM_DETAIL = AdScreenKey.require("platform_detail")

    /**
     * Tool screens.
     */
    val MEDIA_PLAYER = AdScreenKey.require("media_player")
    val VIDEO_TO_MP3 = AdScreenKey.require("video_to_mp3")
    val VIDEO_TO_MP3_RESULT = AdScreenKey.require("video_to_mp3_result")
    val VIDEO_SPLITTER = AdScreenKey.require("video_splitter")
    val VIDEO_SPLITTER_RESULT = AdScreenKey.require("video_splitter_result")
    val SCREEN_CASTING = AdScreenKey.require("screen_casting")

    /**
     * Native/full page placements.
     */
    val MEDIA_PLAYER_FULL_PAGE = AdScreenKey.require("media_player_full_page")
}
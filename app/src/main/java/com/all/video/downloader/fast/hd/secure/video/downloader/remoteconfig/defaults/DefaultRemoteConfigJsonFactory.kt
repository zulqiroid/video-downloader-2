package com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.defaults

object DefaultRemoteConfigJsonFactory {

    fun create(): String {
        return """
            {
            
              "app_global_config": {
                "maintenance_enabled": false,
                "maintenance_title": "Maintenance",
                "maintenance_message": "We are improving the app. Please try again later.",
                "force_update_enabled": false,
                "min_supported_version_code": 1,
                "force_update_title": "Update Required",
                "force_update_message": "Please update the app to continue.",
                "play_store_url": "",
                "support_email": "",
                "privacy_policy_url": "",
                "terms_url": ""
              },
              "app_feature_config": {
                "video_downloader_enabled": true,
                "facebook_downloader_enabled": true,
                "instagram_downloader_enabled": true,
                "tiktok_downloader_enabled": true,
                "threads_downloader_enabled": true,
                "likee_downloader_enabled": true,
                "snack_downloader_enabled": true,
                "video_to_mp3_enabled": true,
                "video_splitter_enabled": true,
                "screen_casting_enabled": true,
                "vault_enabled": true,
                "reels_enabled": true,
                "media_player_enabled": true
              },
              "app_premium_config": {
                "enabled": true,
                "show_premium_screen": true,
                "default_selected_plan": "yearly",
                "weekly_product_id": "",
                "monthly_product_id": "",
                "yearly_product_id": "",
                "lifetime_product_id": "",
                "show_lifetime_plan": true,
                "show_weekly_plan": true,
                "show_monthly_plan": true,
                "show_yearly_plan": true,
                "headline": "Go Premium",
                "subtitle": "Remove ads and enjoy a cleaner experience."
              },
              "app_api_config": {
                "downloader_enabled": false,
                "downloader_base_url": "",
                "downloader_secret_key": "",
                "downloader_secret_header": "X-Secret-Key"
              },
              "ads_global_config": {
                "ads_enabled": false,
                "can_request_ads": false,
                "is_debug": true
              },
              "ads_loading_dialog_config": {
                "enabled": true,
                "show_for_interstitial": false,
                "show_for_rewarded": false,
                "duration_ms": 2500,
                "title": "Loading Ad",
                "message": "Please wait while we prepare your ad"
              },
              "ads_app_open_config": {
                "enabled": false,
                "show_on_resume": false,
                "show_on_splash": false,
                "app_open_ad_id": "",
                "splash_open_ad_unit_id": "",
                "max_ad_cache_duration_ms": 14400000,
                "min_interval_between_shows_ms": 120000,
                "min_background_duration_before_show_ms": 3000,
                "max_load_retry_count": 3,
                "initial_retry_delay_ms": 2000,
                "max_retry_delay_ms": 60000,
                "show_automatically_on_cold_start": false
              },
              "ads_banner_config": {
                "enabled": false,
                "banner_ad_id": "",
                "show_placeholder": true,
                "placeholder_height_dp": 60,
                "keep_placeholder_on_failure": false,
                "screens": {}
              },
              "ads_interstitial_config": {
                "enabled": false,
                "interstitial_ad_id": "",
                "splash_unit_id": "",
                "language_unit_id": "",
                "intro_unit_id": "",
                "premium_unit_id": "",
                "min_interval_between_shows_ms": 60000,
                "max_ad_cache_duration_ms": 14400000,
                "max_load_retry_count": 3,
                "initial_retry_delay_ms": 2000,
                "max_retry_delay_ms": 60000,
                "show_on_tab_switch": false,
                "tab_switch_trigger_count": 3,
                "show_on_premium": false,
                "premium_trigger_count": 2,
                "show_on_onboarding": false,
                "onboarding_trigger_count": 2,
                "show_on_language": false,
                "language_trigger_count": 2,
                "show_on_image_result": false,
                "image_result_trigger_count": 1,
                "show_on_back_navigation": false,
                "back_navigation_trigger_count": 1,
                "screens": {},
                "features": {}
              },
              "ads_rewarded_config": {
                "enabled": false,
                "rewarded_ad_id": "",
                "min_interval_between_shows_ms": 30000,
                "max_ad_cache_duration_ms": 14400000,
                "max_load_retry_count": 3,
                "initial_retry_delay_ms": 2000,
                "max_retry_delay_ms": 60000,
                "placements": {}
              },
              "ads_native_config": {
                "enabled": false,
                "native_ad_id": "",
                "max_ad_cache_duration_ms": 3600000,
                "max_load_retry_count": 3,
                "initial_retry_delay_ms": 2000,
                "max_retry_delay_ms": 60000,
                "container_background_color": "#FFFFFF",
                "container_border_color": "#E00004",
                "container_border_width_dp": 1,
                "media_background_color": "#F8FAFC",
                "headline_text_color": "#0F172A",
                "body_text_color": "#475569",
                "cta_background_color": "#E00004",
                "cta_text_color": "#FFFFFF",
                "ad_attribution_text_color": "#0F172A",
                "ad_attribution_background_color": "#FFFFFF",
                "ad_attribution_border_color": "#E00004",
                "star_rating_color": "#E00004",
                "corner_radius_dp": 16,
                "cta_corner_radius_dp": 16,
                "ad_badge_corner_radius_dp": 5,
                "media_corner_radius_dp": 16,
                "placements": {}
              }
            }
        """.trimIndent()
    }
}
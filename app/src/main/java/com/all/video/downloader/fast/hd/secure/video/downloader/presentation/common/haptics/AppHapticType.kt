package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics

enum class AppHapticType {

    /**
     * Light tap for normal UI clicks:
     * buttons, cards, list items, tabs.
     */
    Click,

    /**
     * Stronger confirmation:
     * apply, save, download started, success.
     */
    Confirm,

    /**
     * Error / rejected action:
     * invalid URL, failed validation, disabled action warning.
     */
    Reject,

    /**
     * Tiny tick:
     * pager change, stepper, slider small movement.
     */
    Tick,

    /**
     * Very light frequent tick:
     * scroll index, progress thinking pulse, repeated but controlled feedback.
     */
    FrequentTick,

    /**
     * Toggle on/off:
     * switches, selected/unselected states.
     */
    ToggleOn,
    ToggleOff,

    /**
     * Gesture / swipe / drag.
     */
    GestureStart,
    GestureEnd,
    DragStart,

    /**
     * Loading-thinking style.
     * Should be used carefully and throttled.
     */
    LoadingStart,
    LoadingTick
}
package com.core.ads.domain

/**
 * AdsInitializer ads SDK initialization ka contract hai.
 *
 * Is interface ka benefit:
 * - App module direct Google Mobile Ads SDK ko touch nahi karega
 * - Future me agar ads provider change ho, contract same reh sakta hai
 * - Testing me fake initializer use kar sakte hain
 */
interface AdsInitializer {

    /**
     * Google Mobile Ads SDK initialize karega.
     *
     * Ye method safe hona chahiye:
     * - multiple calls par SDK dobara initialize na ho
     * - ads disabled hon to initialize na ho
     * - consent allowed na ho to initialize na ho
     */
    fun initialize(
        onReady: () -> Unit = {}
    )
}
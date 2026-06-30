package com.core.ads.domain.nativead

import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.ads.nativead.MediaView

/**
 * NativeAdBinding native ad ko custom views ke saath bind karne ka interface hai.
 * Ye Google SDK ki class ko chhupata hai.
 */
interface NativeAdBinding {
    fun setHeadline(textView: TextView)
    fun setBody(textView: TextView)
    fun setCallToAction(button: Button)
    fun setIcon(imageView: ImageView)
    fun setMediaView(mediaView: MediaView)
    fun setAdvertiser(textView: TextView)
    fun setStarRating(textView: TextView)
    fun setStore(textView: TextView)
    fun setPrice(textView: TextView)

    /**
     * Pura populated ad view return karta hai jo UI me add kiya ja sakta hai.
     */
    fun getAdView(): ViewGroup

    /**
     * Ad ko destroy karna hai jab use na ho.
     */
    fun destroy()
}
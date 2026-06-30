package com.core.ads.data.nativead

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.core.ads.R
import com.core.ads.domain.NativeAdSize
import com.core.ads.domain.NativeAdStyleConfig
import com.core.ads.domain.ResolvedNativeAdConfig
import com.core.ads.domain.nativead.NativeAdBinding
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

class GoogleNativeAdBinding(
    private val activity: Activity,
    private val nativeAd: NativeAd,
    private val config: ResolvedNativeAdConfig
) : NativeAdBinding {

    private val style: NativeAdStyleConfig = config.styleConfig

    private val adView: NativeAdView = LayoutInflater.from(activity).inflate(
        resolveLayoutResId(config.size),
        null
    ) as NativeAdView

    private var destroyed: Boolean = false

    init {
        applyRemoteStyle()
        bindDefaultLayout()
    }

    override fun getAdView(): ViewGroup = adView

    override fun setHeadline(textView: TextView) {
        if (destroyed) return

        adView.headlineView = textView
        textView.text = nativeAd.headline.orEmpty()
        textView.visibility = if (nativeAd.headline.isNullOrBlank()) View.GONE else View.VISIBLE

        textView.setTextColor(parseColor(style.headlineTextColor, "#0F172A"))
        textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD)

        registerNativeAd()
    }

    override fun setBody(textView: TextView) {
        if (destroyed) return

        adView.bodyView = textView
        textView.text = nativeAd.body.orEmpty()
        textView.visibility = if (nativeAd.body.isNullOrBlank()) View.GONE else View.VISIBLE

        textView.setTextColor(parseColor(style.bodyTextColor, "#475569"))

        registerNativeAd()
    }

    override fun setCallToAction(button: Button) {
        if (destroyed) return

        adView.callToActionView = button
        button.text = nativeAd.callToAction.orEmpty()
        button.visibility = if (nativeAd.callToAction.isNullOrBlank()) View.GONE else View.VISIBLE

        button.setTextColor(parseColor(style.ctaTextColor, "#FFFFFF"))
        button.background = roundedBackground(
            color = parseColor(style.ctaBackgroundColor, "#2563EB"),
            cornerRadiusDp = style.ctaCornerRadiusDp
        )

        registerNativeAd()
    }

    override fun setIcon(imageView: ImageView) {
        if (destroyed) return

        adView.iconView = imageView

        val drawable = nativeAd.icon?.drawable
        if (drawable != null) {
            imageView.setImageDrawable(drawable)
            imageView.visibility = View.VISIBLE
        } else {
            imageView.visibility = View.GONE
        }

        registerNativeAd()
    }

    override fun setMediaView(mediaView: MediaView) {
        if (destroyed) return

        adView.mediaView = mediaView
        mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
        mediaView.background = roundedBackground(
            color = parseColor(style.mediaBackgroundColor, "#FFFFFF"),
            cornerRadiusDp = style.mediaCornerRadiusDp
        )

        registerNativeAd()
    }

    override fun setAdvertiser(textView: TextView) {
        if (destroyed) return

        adView.advertiserView = textView
        textView.text = nativeAd.advertiser.orEmpty()
        textView.visibility = if (nativeAd.advertiser.isNullOrBlank()) View.GONE else View.VISIBLE
        textView.setTextColor(parseColor(style.bodyTextColor, "#475569"))

        registerNativeAd()
    }

    override fun setStarRating(textView: TextView) {
        if (destroyed) return

        adView.starRatingView = textView

        val rating = nativeAd.starRating
        if (rating != null) {
            textView.text = rating.toString()
            textView.visibility = View.VISIBLE
            textView.setTextColor(parseColor(style.starRatingColor, "#F59E0B"))
        } else {
            textView.visibility = View.GONE
        }

        registerNativeAd()
    }

    override fun setStore(textView: TextView) {
        if (destroyed) return

        adView.storeView = textView
        textView.text = nativeAd.store.orEmpty()
        textView.visibility = if (nativeAd.store.isNullOrBlank()) View.GONE else View.VISIBLE
        textView.setTextColor(parseColor(style.bodyTextColor, "#475569"))

        registerNativeAd()
    }

    override fun setPrice(textView: TextView) {
        if (destroyed) return

        adView.priceView = textView
        textView.text = nativeAd.price.orEmpty()
        textView.visibility = if (nativeAd.price.isNullOrBlank()) View.GONE else View.VISIBLE
        textView.setTextColor(parseColor(style.bodyTextColor, "#475569"))

        registerNativeAd()
    }

    override fun destroy() {
        if (destroyed) return
        destroyed = true

        runCatching {
            (adView.parent as? ViewGroup)?.removeView(adView)
        }

        runCatching {
            nativeAd.destroy()
        }
    }

    private fun bindDefaultLayout() {
        adView.findViewById<TextView?>(R.id.ad_headline)?.let { setHeadline(it) }
        adView.findViewById<TextView?>(R.id.ad_body)?.let { setBody(it) }
        adView.findViewById<Button?>(R.id.ad_call_to_action)?.let { setCallToAction(it) }
        adView.findViewById<ImageView?>(R.id.ad_icon)?.let { setIcon(it) }
        adView.findViewById<MediaView?>(R.id.ad_media)?.let { setMediaView(it) }

        adView.findViewById<TextView?>(R.id.ad_advertiser)?.let { setAdvertiser(it) }
        adView.findViewById<TextView?>(R.id.ad_star_rating)?.let { setStarRating(it) }
        adView.findViewById<TextView?>(R.id.ad_store)?.let { setStore(it) }
        adView.findViewById<TextView?>(R.id.ad_price)?.let { setPrice(it) }

        registerNativeAd()
    }

    private fun applyRemoteStyle() {
        adView.background = roundedBackground(
            color = parseColor(style.containerBackgroundColor, "#FFFFFF"),
            strokeColor = parseColor(style.containerBorderColor, "#E5E7EB"),
            strokeWidthDp = style.containerBorderWidthDp,
            cornerRadiusDp = style.cornerRadiusDp
        )

        adView.findViewById<TextView?>(R.id.ad_badge)?.apply {
            setTextColor(parseColor(style.adAttributionTextColor, "#0F172A"))
            background = roundedBackground(
                color = parseColor(style.adAttributionBackgroundColor, "#FFFFFF"),
                strokeColor = parseColor(style.adAttributionBorderColor, "#E5E7EB"),
                strokeWidthDp = 1,
                cornerRadiusDp = style.adBadgeCornerRadiusDp
            )
        }
    }

    private fun registerNativeAd() {
        if (destroyed) return

        runCatching {
            adView.setNativeAd(nativeAd)
        }
    }

    private fun resolveLayoutResId(size: NativeAdSize): Int {
        return when (size) {
            NativeAdSize.SMALL -> R.layout.ad_native_small
            NativeAdSize.MEDIUM -> R.layout.ad_native_medium
            NativeAdSize.LARGE -> R.layout.ad_native_full_page
        }
    }

    private fun roundedBackground(
        color: Int,
        strokeColor: Int? = null,
        strokeWidthDp: Int = 0,
        cornerRadiusDp: Int = 0
    ): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(color)
            cornerRadius = dp(cornerRadiusDp).toFloat()

            if (strokeColor != null && strokeWidthDp > 0) {
                setStroke(dp(strokeWidthDp), strokeColor)
            }
        }
    }

    private fun parseColor(
        value: String,
        fallback: String
    ): Int {
        return runCatching {
            Color.parseColor(value)
        }.getOrElse {
            Color.parseColor(fallback)
        }
    }

    private fun dp(value: Int): Int {
        return (value * activity.resources.displayMetrics.density).toInt()
    }
}
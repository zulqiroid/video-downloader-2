package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.premium.events

import android.app.Activity
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.premium.PremiumPlanType

sealed interface PremiumEvents {

    data class PlanSelected(
        val type: PremiumPlanType
    ) : PremiumEvents

    data class ContinueClicked(
        val activity: Activity?
    ) : PremiumEvents

    data object RestoreClicked : PremiumEvents

    data object CloseClicked : PremiumEvents

    data object PrivacyPolicyClicked : PremiumEvents
    data object TermsClicked : PremiumEvents
    data object CancelSubscriptionClicked : PremiumEvents
}
package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.premium.states

import com.all.video.downloader.fast.hd.secure.video.downloader.domain.premium.PremiumPlanType

data class PremiumStates(
    val isLoading: Boolean = true,
    val isPurchasing: Boolean = false,
    val isPremiumUser: Boolean = false,

    val headline: String = "Upgrade to Premium",
    val subtitle: String = "Unlock all features and enjoy seamless downloads.",

    val selectedPlan: PremiumPlanType = PremiumPlanType.YEARLY,
    val plans: List<PremiumPlanUi> = emptyList(),

    val showTrialText: Boolean = true,
    val errorMessage: String? = null
)

data class PremiumPlanUi(
    val type: PremiumPlanType,
    val productId: String,
    val title: String,
    val price: String,
    val periodLabel: String
)
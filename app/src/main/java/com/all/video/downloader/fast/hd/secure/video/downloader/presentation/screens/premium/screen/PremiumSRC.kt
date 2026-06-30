package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.premium.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.R
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.premium.PremiumPlanType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppGradientButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.AppHapticType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.hapticClickable
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.premium.events.PremiumEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.premium.states.PremiumPlanUi
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.premium.states.PremiumStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppGradients
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppShapes
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun PremiumSRC(
    state: PremiumStates,
    onEvent: (PremiumEvents) -> Unit,
    modifier: Modifier = Modifier
) {
    val hapticFeedback = rememberAppHapticFeedback()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppGradients.HighlightVertical)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.06f),
                            Color.Black.copy(alpha = 0.5f),
                            Color.Black.copy(alpha = 0.92f)
                        )
                    )
                )
        )
        Image(
            painter = painterResource(id = R.drawable.premium_bg_img),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.04f),
                            Color.Black.copy(alpha = 0.30f),
                            Color.Black.copy(alpha = 0.92f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_cross_filled),
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(28.dp)
                        .hapticClickable(
                            hapticFeedback = hapticFeedback,
                            hapticType = AppHapticType.Click,
                            onClick = {
                                onEvent(PremiumEvents.CloseClicked)
                            }
                        )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            PremiumContentCard(
                state = state,
                onEvent = onEvent
            )

            Spacer(modifier = Modifier.height(10.dp))

            PremiumFooterLinks(
                onTermsClick = {
                    onEvent(PremiumEvents.TermsClicked)
                },
                onPrivacyClick = {
                    onEvent(PremiumEvents.PrivacyPolicyClicked)
                },
                onCancelSubscriptionClick = {
                    onEvent(PremiumEvents.CancelSubscriptionClicked)
                }
            )

            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun PremiumContentCard(
    state: PremiumStates,
    onEvent: (PremiumEvents) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                colors = listOf(
                    Color.Black.copy(alpha = 0f),
                    Color.Black.copy(alpha = 0.2f),
                    Color.Black.copy(alpha = 0.5f),
                    Color.Black.copy(alpha = 0.7f),
                    Color.Black.copy(alpha = 0.9f),
                )
            )
            )
            .padding(horizontal = 16.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = state.headline.ifBlank { "Upgrade to Premium" },
            style = AppTextStyles.screenTitle,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Text(
            text = state.subtitle.ifBlank { "Unlock all features and enjoy seamless downloads." },
            style = AppTextStyles.caption,
            color = Color.White.copy(alpha = 0.68f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 5.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))

        PremiumFeatureGrid()

        Spacer(modifier = Modifier.height(18.dp))

        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    color = AppColors.HighlightGradientTop,
                    modifier = Modifier
                        .padding(vertical = 30.dp)
                        .size(30.dp)
                )
            }

            state.plans.isEmpty() -> {
                Text(
                    text = state.errorMessage ?: "Premium plans are not available right now.",
                    style = AppTextStyles.bodySmall,
                    color = Color.White.copy(alpha = 0.75f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 22.dp)
                )
            }

            else -> {
                PremiumPlansRow(
                    plans = state.plans,
                    selectedPlan = state.selectedPlan,
                    onPlanSelected = { planType ->
                        onEvent(PremiumEvents.PlanSelected(planType))
                    }
                )
            }
        }

        if (state.showTrialText) {
            Text(
                text = "Start 3-day free trial",
                style = AppTextStyles.caption,
                color = Color.White.copy(alpha = 0.58f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 14.dp)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        AppGradientButton(
            text = if (state.isPremiumUser) "Premium Active" else "Continue",
            onClick = {
                onEvent(PremiumEvents.ContinueClicked(activity = null))
            },
            enabled = !state.isPremiumUser && !state.isLoading && state.plans.isNotEmpty(),
            isLoading = state.isPurchasing,
            buttonHeight = 54.dp,
            modifier = Modifier.fillMaxWidth()
        )

      /*  Text(
            text = "Restore Purchase",
            style = AppTextStyles.buttonSmall,
            color = Color.White.copy(alpha = 0.78f),
            modifier = Modifier
                .padding(top = 12.dp)
                .clickable {
                    onEvent(PremiumEvents.RestoreClicked)
                }
        )*/
    }
}

@Composable
private fun PremiumFeatureGrid() {
    val features = listOf(
        "Ads-Free Experience",
        "HD Quality",
        "Unlimited Downloads",
        "Private Vault"
    )

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        maxItemsInEachRow = 2
    ) {
        features.forEach { title ->
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(AppShapes.extraSmall)
                        .background(AppGradients.HighlightVertical),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = title,
                    style = AppTextStyles.caption,
                    color = Color.White,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun PremiumPlansRow(
    plans: List<PremiumPlanUi>,
    selectedPlan: PremiumPlanType,
    onPlanSelected: (PremiumPlanType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        plans.forEach { plan ->
            PremiumPlanCard(
                plan = plan,
                selected = plan.type == selectedPlan,
                onClick = {
                    onPlanSelected(plan.type)
                }
            )
        }
    }
}

@Composable
private fun PremiumPlanCard(
    plan: PremiumPlanUi,
    selected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(15.dp)

    Column(
        modifier = Modifier
            .size(width = 120.dp, height = 126.dp)
            .clip(shape)
            .background(
                brush = if (selected) {
                    AppGradients.HighlightVertical
                } else {
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF4C344F),
                            Color(0xFF2A1E2C)
                        )
                    )
                }
            )
            .border(
                width = if (selected) 1.5.dp else 0.dp,
                color = if (selected) Color.White.copy(alpha = 0.18f) else Color.Transparent,
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = plan.title,
            style = AppTextStyles.overline,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = plan.price,
            style = AppTextStyles.titleLarge,
            color = Color.White,
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        Text(
            text = plan.periodLabel,
            style = AppTextStyles.caption,
            color = Color.White.copy(alpha = 0.78f),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun PremiumFooterLinks(
    onTermsClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onCancelSubscriptionClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PremiumFooterText("Terms of Use", onTermsClick)
        PremiumFooterDivider()
        PremiumFooterText("Privacy Policy", onPrivacyClick)
        PremiumFooterDivider()
        PremiumFooterText("Cancel Subscription", onCancelSubscriptionClick)
    }
}

@Composable
private fun PremiumFooterText(
    text: String,
    onClick: () -> Unit
) {
    Text(
        text = text,
        style = AppTextStyles.caption,
        color = Color.White.copy(alpha = 0.54f),
        modifier = Modifier.clickable(onClick = onClick)
    )
}

@Composable
private fun PremiumFooterDivider() {
    Text(
        text = "|",
        style = AppTextStyles.caption,
        color = Color.White.copy(alpha = 0.28f)
    )
}
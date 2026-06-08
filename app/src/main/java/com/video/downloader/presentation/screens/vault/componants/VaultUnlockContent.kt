package com.video.downloader.presentation.screens.vault.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.video.downloader.presentation.common.componants.AppGradientButton
import com.video.downloader.presentation.common.componants.AppOutlinedButton
import com.video.downloader.presentation.common.haptics.AppHapticType
import com.video.downloader.presentation.common.haptics.hapticClickable
import com.video.downloader.presentation.common.haptics.rememberAppHapticFeedback
import com.video.downloader.presentation.screens.vault.events.VaultEvents
import com.video.downloader.presentation.screens.vault.states.VaultStates
import com.video.downloader.presentation.theme.AppColors
import com.video.downloader.presentation.theme.AppGradients
import com.video.downloader.presentation.theme.AppTextStyles

@Composable
fun VaultUnlockContent(
    state: VaultStates,
    onEvent: (VaultEvents) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppColors.Background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .padding(bottom = 14.dp)
            ) {
                Spacer(
                    modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars)
                )

                VaultTopBar(
                    title = "Unlock Vault",
                    onBackClick = {
                        onEvent(VaultEvents.BackClicked)
                    }
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 22.dp)
                    .padding(top = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AppGradientButton(
                    text = if (state.isPinLockedOut) {
                        "Try again in ${state.lockoutRemainingSeconds}s"
                    } else {
                        "Unlock Vault"
                    },
                    onClick = {
                        onEvent(VaultEvents.UnlockPinClicked)
                    },
                    enabled = state.canUnlockWithPin,
                    isLoading = state.isVerifying,
                    buttonHeight = 52.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(12.dp))

                Text(
                    text = "Your private files are protected by your Vault PIN.",
                    style = AppTextStyles.caption.copy(
                        color = AppColors.TextDisabled,
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(
                    modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)
                )
            }
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(48.dp))

            VaultUnlockHero()

            Spacer(modifier = Modifier.size(34.dp))

            Text(
                text = "Enter PIN",
                style = AppTextStyles.caption.copy(
                    color = AppColors.TextEnabled,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(modifier = Modifier.size(12.dp))

            VaultPinInput(
                value = state.unlockPin,
                onValueChange = {
                    onEvent(VaultEvents.UnlockPinChanged(it))
                },
                autoFocus = true
            )

            state.unlockErrorMessage?.let { message ->
                Spacer(modifier = Modifier.size(18.dp))

                Text(
                    text = message,
                    style = AppTextStyles.caption.copy(
                        color = AppColors.Error,
                        fontWeight = FontWeight.SemiBold
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.size(24.dp))

            if (state.canUseBiometricUnlock) {
                VaultBiometricUnlockButton(
                    onClick = {
                        onEvent(VaultEvents.BiometricUnlockClicked)
                    }
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun VaultUnlockHero() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(92.dp)
                .shadow(
                    elevation = 24.dp,
                    shape = CircleShape,
                    ambientColor = AppColors.HighlightGradientTop.copy(alpha = 0.45f),
                    spotColor = AppColors.HighlightGradientBottom.copy(alpha = 0.45f)
                )
                .clip(CircleShape)
                .background(AppGradients.HighlightVertical),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = null,
                tint = AppColors.OnHighlight,
                modifier = Modifier.size(42.dp)
            )
        }

        Spacer(modifier = Modifier.size(24.dp))

        Text(
            text = "Vault Locked",
            style = AppTextStyles.titleLarge.copy(
                fontWeight = FontWeight.ExtraBold
            ),
            color = AppColors.TextEnabled,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = "Enter your 4-digit PIN or use biometric unlock\nto access your private files.",
            style = AppTextStyles.bodySmall.copy(
                color = Color(0xFF667085)
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun VaultBiometricUnlockButton(
    onClick: () -> Unit
) {
    val hapticFeedback = rememberAppHapticFeedback()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFFAFBFF))
            .border(
                width = 1.dp,
                color = Color(0xFFE5E7EB),
                shape = RoundedCornerShape(18.dp)
            )
            .hapticClickable(
                hapticFeedback = hapticFeedback,
                hapticType = AppHapticType.Confirm,
                role = Role.Button,
                onClick = onClick
            )
            .padding(vertical = 15.dp),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.layout.Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = null,
                tint = AppColors.HighlightGradientTop,
                modifier = Modifier.size(22.dp)
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "Unlock with Biometric",
                style = AppTextStyles.bodySmall.copy(
                    color = AppColors.TextEnabled,
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }
    }
}
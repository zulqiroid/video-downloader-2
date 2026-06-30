package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.componants

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.common.componants.AppGradientButton
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.events.VaultEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.states.VaultStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppColors
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.AppTextStyles

@Composable
fun VaultPinSetupContent(
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

                VaultTopBar(
                    title = "Set Vault PIN",
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
                    text = "Save PIN",
                    onClick = {
                        onEvent(VaultEvents.SavePinClicked)
                    },
                    enabled = state.canSavePin,
                    isLoading = state.isSaving,
                    buttonHeight = 52.dp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.size(12.dp))

                Text(
                    text = "You’ll need this PIN to access your Vault.",
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
            Spacer(modifier = Modifier.size(28.dp))

            Text(
                text = "Enter a 4-digit PIN to secure your Vault.",
                style = AppTextStyles.bodySmall.copy(
                    color = Color_667085
                ),
                textAlign = TextAlign.Center
            )

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
                value = state.pin,
                onValueChange = {
                    onEvent(VaultEvents.PinChanged(it))
                }
            )

            Spacer(modifier = Modifier.size(28.dp))

            Text(
                text = "Confirm PIN",
                style = AppTextStyles.caption.copy(
                    color = AppColors.TextDisabled,
                    fontWeight = FontWeight.ExtraBold
                )
            )

            Spacer(modifier = Modifier.size(12.dp))

            VaultPinInput(
                value = state.confirmPin,
                onValueChange = {
                    onEvent(VaultEvents.ConfirmPinChanged(it))
                }
            )

            state.errorMessage?.let { message ->
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

            Spacer(modifier = Modifier.weight(1f))

            VaultBiometricCard(
                enabled = state.biometricEnabled,
                available = state.biometricAvailable,
                onCheckedChange = {
                    onEvent(VaultEvents.BiometricToggleChanged(it))
                }
            )

            Spacer(modifier = Modifier.size(18.dp))
        }
    }
}

private val Color_667085 = androidx.compose.ui.graphics.Color(0xFF667085)
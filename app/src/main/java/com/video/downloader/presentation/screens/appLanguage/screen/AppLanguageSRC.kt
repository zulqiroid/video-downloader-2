package com.video.downloader.presentation.screens.appLanguage.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemGestures
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.video.downloader.R
import com.video.downloader.presentation.common.componants.AppGradientButton
import com.video.downloader.presentation.screens.appLanguage.componants.AppLanguageOptionItem
import com.video.downloader.presentation.screens.appLanguage.componants.AppLanguageSearchBar
import com.video.downloader.presentation.screens.appLanguage.componants.AppLanguageTopBar
import com.video.downloader.presentation.screens.appLanguage.events.AppLanguageEvents
import com.video.downloader.presentation.screens.appLanguage.states.AppLanguageStates
import com.video.downloader.presentation.theme.AppColors

@Composable
fun AppLanguageSRC(
    state: AppLanguageStates,
    onEvent: (AppLanguageEvents) -> Unit,
    modifier: Modifier = Modifier
) {

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = AppColors.DisabledContainer,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(
                    modifier = Modifier.windowInsetsTopHeight(WindowInsets.statusBars)
                )
                AppLanguageTopBar(
                    title = stringResource(R.string.select_language),
                    onBackClick = {
                        onEvent(AppLanguageEvents.BackClicked)
                    }
                )

                AppLanguageSearchBar(
                    value = state.searchQuery,
                    onValueChange = { value ->
                        onEvent(AppLanguageEvents.SearchQueryChanged(value))
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
              ) {
                AppGradientButton(
                    text = stringResource(R.string.apply_language),
                    enabled = !state.isLoading,
                    isLoading = state.isLoading,
                    onClick = {
                        onEvent(AppLanguageEvents.ApplyLanguageClicked)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(
                    modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars)
                )
            }

        }
    ) { paddingValues ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {


                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(
                        items = state.filteredLanguages,
                        key = { language -> language.code }
                    ) { language ->
                        AppLanguageOptionItem(
                            language = language,
                            isSelected = language.code == state.selectedLanguageCode,
                            onClick = {
                                onEvent(
                                    AppLanguageEvents.LanguageSelected(
                                        languageCode = language.code
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
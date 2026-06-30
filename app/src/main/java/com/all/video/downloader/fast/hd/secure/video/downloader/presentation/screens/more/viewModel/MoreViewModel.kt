package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.all.video.downloader.fast.hd.secure.video.downloader.BuildConfig
import com.all.video.downloader.fast.hd.secure.video.downloader.data.feedback.FeedbackDiagnosticsProvider
import com.all.video.downloader.fast.hd.secure.video.downloader.data.local.datastore.repository.LocalDataStoreRepository
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.settings.DownloadLocationType
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.appLanguage.states.defaultAppLanguages
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.events.MoreEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.events.MoreNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.events.MoreUiEffect
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.states.FeedbackCategory
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.states.MoreStates
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.more.states.SettingsItem
import com.all.video.downloader.fast.hd.secure.video.downloader.remoteconfig.domain.repository.VideoDownloaderRemoteConfigRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val localDataStoreRepository: LocalDataStoreRepository,
    private val remoteConfigRepository: VideoDownloaderRemoteConfigRepository,
    private val feedbackDiagnosticsProvider: FeedbackDiagnosticsProvider
) : ViewModel() {

    private val _state = MutableStateFlow(MoreStates())
    val state = _state.asStateFlow()

    private val _navEvents = MutableSharedFlow<MoreNavEvents>()
    val navEvents = _navEvents.asSharedFlow()

    private val _uiEffects = MutableSharedFlow<MoreUiEffect>(
        extraBufferCapacity = 1
    )
    val uiEffects = _uiEffects.asSharedFlow()

    init {
        observeSelectedLanguage()
        observeNotificationPreference()
        observeDownloadLocation()
    }

    fun onEvent(event: MoreEvents) {
        when (event) {
            MoreEvents.BackClicked -> {
                emitNav(MoreNavEvents.NavigateBack)
            }

            is MoreEvents.OnNotificationCheckedChange -> {
                onNotificationChanged(event.isChecked)
            }

            is MoreEvents.OnSettingItemClicked -> {
                onSettingItemClicked(event.item)
            }

            is MoreEvents.OnSdCardAvailabilityChanged -> {
                _state.update { currentState ->
                    currentState.copy(isSdCardAvailable = event.isAvailable)
                }
            }

            MoreEvents.RateNowClicked -> {
                _state.update { it.copy(showRateDialog = false) }
                emitEffect(MoreUiEffect.OpenRatePage)
            }

            MoreEvents.RateLaterClicked,
            MoreEvents.DismissRateDialog -> {
                _state.update { it.copy(showRateDialog = false) }
            }

            MoreEvents.DismissDownloadLocationDialog -> {
                _state.update { it.copy(showDownloadLocationDialog = false) }
            }

            is MoreEvents.OnDownloadLocationOptionSelected -> {
                onDownloadLocationOptionSelected(event.type)
            }

            is MoreEvents.OnCustomFolderPicked -> {
                _state.update { currentState ->
                    currentState.copy(
                        pendingDownloadLocationType = DownloadLocationType.CUSTOM_FOLDER,
                        pendingCustomFolderUri = event.treeUri
                    )
                }
            }

            MoreEvents.SaveDownloadLocationClicked -> {
                saveDownloadLocation()
            }

            MoreEvents.DismissFeedbackDialog -> {
                dismissFeedbackDialog()
            }

            is MoreEvents.OnFeedbackCategorySelected -> {
                _state.update { currentState ->
                    currentState.copy(
                        selectedFeedbackCategory = event.category,
                        feedbackError = null
                    )
                }
            }

            is MoreEvents.OnFeedbackMessageChanged -> {
                _state.update { currentState ->
                    currentState.copy(
                        feedbackMessage = event.message,
                        feedbackError = null
                    )
                }
            }

            MoreEvents.SubmitFeedbackClicked -> {
                submitFeedback()
            }
        }
    }

    private fun observeSelectedLanguage() {
        viewModelScope.launch {
            localDataStoreRepository.getSelectedLanguageCode().collect { code ->
                val language = defaultAppLanguages.firstOrNull { language ->
                    language.code == code
                } ?: defaultAppLanguages.first()

                _state.update { currentState ->
                    currentState.copy(selectedLanguage = language)
                }
            }
        }
    }

    private fun observeNotificationPreference() {
        viewModelScope.launch {
            localDataStoreRepository.isNotificationsEnabled().collect { enabled ->
                _state.update { currentState ->
                    currentState.copy(isNotificationEnabled = enabled)
                }
            }
        }
    }

    private fun observeDownloadLocation() {
        viewModelScope.launch {
            combine(
                localDataStoreRepository.getDownloadLocationType(),
                localDataStoreRepository.getDownloadLocationTreeUri()
            ) { typeValue, treeUri ->
                DownloadLocationType.fromStorageValue(typeValue) to treeUri
            }.collect { (type, treeUri) ->
                _state.update { currentState ->
                    currentState.copy(
                        selectedDownloadLocationType = type,
                        selectedCustomFolderUri = treeUri,
                        pendingDownloadLocationType = type,
                        pendingCustomFolderUri = treeUri
                    )
                }
            }
        }
    }

    private fun onNotificationChanged(enabled: Boolean) {
        viewModelScope.launch {
            localDataStoreRepository.setNotificationsEnabled(enabled)

            emitEffect(
                MoreUiEffect.ShowMessage(
                    if (enabled) {
                        "Notifications enabled."
                    } else {
                        "Notifications disabled."
                    }
                )
            )
        }
    }

    private fun onSettingItemClicked(item: SettingsItem) {
        when (item) {
            SettingsItem.DownloadGuide -> {
                emitNav(MoreNavEvents.NavigateToDownloadGuide)
            }

            SettingsItem.DownloadLocation -> {
                _state.update { currentState ->
                    currentState.copy(
                        showDownloadLocationDialog = true,
                        pendingDownloadLocationType = currentState.selectedDownloadLocationType,
                        pendingCustomFolderUri = currentState.selectedCustomFolderUri
                    )
                }
            }

            SettingsItem.Language -> {
                emitNav(MoreNavEvents.NavigateToLanguage)
            }

            SettingsItem.Notification -> {
                /**
                 * Switch itself handles this.
                 */
            }

            SettingsItem.RateApp -> {
                _state.update { currentState ->
                    currentState.copy(showRateDialog = true)
                }
            }

            SettingsItem.ShareApp -> {
                emitEffect(MoreUiEffect.ShareApp)
            }

            SettingsItem.Feedback -> {
                _state.update { currentState ->
                    currentState.copy(
                        showFeedbackDialog = true,
                        selectedFeedbackCategory = FeedbackCategory.APP_CRASHES,
                        feedbackMessage = "",
                        feedbackError = null,
                        isSubmittingFeedback = false
                    )
                }
            }

            SettingsItem.PrivacyPolicy -> {
                val privacyUrl = resolvePrivacyPolicyUrl()

                if (privacyUrl.isBlank()) {
                    emitEffect(
                        MoreUiEffect.ShowMessage(
                            "Privacy Policy is not available right now."
                        )
                    )
                    return
                }

                emitEffect(
                    MoreUiEffect.OpenPrivacyPolicy(
                        url = privacyUrl
                    )
                )
            }
        }
    }

    private fun onDownloadLocationOptionSelected(type: DownloadLocationType) {
        if (type == DownloadLocationType.SD_CARD && !_state.value.isSdCardAvailable) {
            emitEffect(MoreUiEffect.ShowMessage("SD card is not available."))
            return
        }

        if (type == DownloadLocationType.CUSTOM_FOLDER) {
            emitEffect(MoreUiEffect.OpenCustomFolderPicker)
        }

        _state.update { currentState ->
            currentState.copy(pendingDownloadLocationType = type)
        }
    }

    private fun saveDownloadLocation() {
        val currentState = _state.value

        if (
            currentState.pendingDownloadLocationType == DownloadLocationType.CUSTOM_FOLDER &&
            currentState.pendingCustomFolderUri.isNullOrBlank()
        ) {
            emitEffect(MoreUiEffect.ShowMessage("Please select a custom folder first."))
            return
        }

        viewModelScope.launch {
            localDataStoreRepository.setDownloadLocationType(
                currentState.pendingDownloadLocationType.storageValue
            )

            localDataStoreRepository.setDownloadLocationTreeUri(
                if (currentState.pendingDownloadLocationType == DownloadLocationType.CUSTOM_FOLDER) {
                    currentState.pendingCustomFolderUri
                } else {
                    null
                }
            )

            _state.update { state ->
                state.copy(showDownloadLocationDialog = false)
            }

            emitEffect(MoreUiEffect.ShowMessage("Download location saved."))
        }
    }

    private fun submitFeedback() {
        val currentState = _state.value
        val feedbackMessage = currentState.feedbackMessage.trim()

        if (feedbackMessage.length < MIN_FEEDBACK_MESSAGE_LENGTH) {
            _state.update { state ->
                state.copy(
                    feedbackError = "Please describe your feedback in at least $MIN_FEEDBACK_MESSAGE_LENGTH characters."
                )
            }
            return
        }

        val feedbackEmail = resolveFeedbackEmail()

        if (feedbackEmail.isBlank()) {
            _state.update { state ->
                state.copy(
                    feedbackError = "Feedback email is not configured."
                )
            }
            return
        }

        val category = currentState.selectedFeedbackCategory

        val subject = buildFeedbackSubject(
            categoryTitle = category.title
        )

        val body = buildFeedbackEmailBody(
            categoryTitle = category.title,
            userMessage = feedbackMessage,
            currentState = currentState
        )

        _state.update { state ->
            state.copy(
                showFeedbackDialog = false,
                feedbackMessage = "",
                feedbackError = null,
                isSubmittingFeedback = false
            )
        }

        emitEffect(
            MoreUiEffect.SendFeedbackEmail(
                toEmail = feedbackEmail,
                subject = subject,
                body = body
            )
        )
    }

    private fun dismissFeedbackDialog() {
        _state.update { state ->
            state.copy(
                showFeedbackDialog = false,
                feedbackMessage = "",
                feedbackError = null,
                isSubmittingFeedback = false
            )
        }
    }

    private fun resolveFeedbackEmail(): String {
        return remoteConfigRepository
            .current()
            .appConfig
            .global
            .supportEmail
            .cleanOrEmpty()
            .ifBlank { DEFAULT_FEEDBACK_EMAIL }
    }

    private fun resolvePrivacyPolicyUrl(): String {
        return remoteConfigRepository
            .current()
            .appConfig
            .global
            .privacyPolicyUrl
            .cleanOrEmpty()
            .ifBlank { DEFAULT_PRIVACY_POLICY_URL }
    }

    private fun emitNav(navEvent: MoreNavEvents) {
        viewModelScope.launch {
            _navEvents.emit(navEvent)
        }
    }

    private fun emitEffect(effect: MoreUiEffect) {
        viewModelScope.launch {
            _uiEffects.emit(effect)
        }
    }

    private fun String?.cleanOrEmpty(): String {
        return this?.trim().orEmpty()
    }

    private fun buildFeedbackSubject(
        categoryTitle: String
    ): String {
        return "[Video Downloader Feedback] $categoryTitle - v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    }

    private fun buildFeedbackEmailBody(
        categoryTitle: String,
        userMessage: String,
        currentState: MoreStates
    ): String {
        val selectedLanguageName = currentState.selectedLanguage
            .nameRes
            .let { nameRes ->
                /**
                 * ViewModel mein stringResource available nahi hota.
                 * Isliye support email ke liye language code/name fallback simple rakha gaya hai.
                 * UI mein display name already proper localized show hota hai.
                 */
                currentState.selectedLanguage.code
            }

        val diagnosticsBlock = feedbackDiagnosticsProvider.buildDiagnosticsBlock(
            selectedLanguageName = selectedLanguageName,
            notificationsEnabled = currentState.isNotificationEnabled,
            downloadLocationLabel = currentState.downloadLocationLabel
        )

        return buildString {
            appendLine("Hello DeepVision Studio Team,")
            appendLine()
            appendLine("I would like to share feedback about the Video Downloader app.")
            appendLine()
            appendLine("---- Feedback Details ----")
            appendLine("Category: $categoryTitle")
            appendLine()
            appendLine("Message:")
            appendLine(userMessage)
            appendLine()
            appendLine(diagnosticsBlock)
            appendLine()
            appendLine("---- End of Feedback ----")
        }
    }

    private companion object {
        private const val MIN_FEEDBACK_MESSAGE_LENGTH = 8
        private const val DEFAULT_FEEDBACK_EMAIL = "www.deepvision.studio@gmail.com"
        private const val DEFAULT_PRIVACY_POLICY_URL =
            "https://deepvisionstudio.blogspot.com/2026/05/privacy-policy-of-deepvision-studio.html"
    }
}
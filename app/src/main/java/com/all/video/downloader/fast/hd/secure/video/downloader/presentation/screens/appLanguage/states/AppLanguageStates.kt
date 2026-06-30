package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.appLanguage.states

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import com.all.video.downloader.fast.hd.secure.video.downloader.R

@Immutable
data class AppLanguageStates(
    val searchQuery: String = "",
    val selectedLanguageCode: String = "en",
    val languages: List<AppLanguageUiModel> = defaultAppLanguages,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val filteredLanguages: List<AppLanguageUiModel>
        get() {
            if (searchQuery.isBlank()) return languages

            return languages.filter { language ->
                language.searchName.contains(searchQuery, ignoreCase = true) ||
                        language.code.contains(searchQuery, ignoreCase = true)
            }
        }
}

@Immutable
data class AppLanguageUiModel(
    val code: String,
    @StringRes val nameRes: Int,
    val searchName: String,
   @DrawableRes val flagEmoji: Int
)

val defaultAppLanguages = listOf(
    AppLanguageUiModel(
        code = "en",
        nameRes = R.string.english,
        searchName = "English",
        flagEmoji = R.drawable.england_flag_round
    ),
    AppLanguageUiModel(
        code = "ur",
        nameRes = R.string.urdu,
        searchName = "Urdu",
        flagEmoji = R.drawable.pakistan_flag_round
    ),
    AppLanguageUiModel(
        code = "de",
        nameRes = R.string.german,
        searchName = "German",
        flagEmoji =R.drawable.germany_flag_round
    ),
    AppLanguageUiModel(
        code = "hi",
        nameRes = R.string.hindi,
        searchName = "Hindi",
        flagEmoji =R.drawable.india_flag_round
    ),
    AppLanguageUiModel(
        code = "fr",
        nameRes = R.string.french,
        searchName = "French",
        flagEmoji = R.drawable.france_flag_round
    ),
    AppLanguageUiModel(
        code = "es",
        nameRes = R.string.spanish,
        searchName = "Spanish",
        flagEmoji = R.drawable.spain_flag_round
    ),
    AppLanguageUiModel(
        code = "ru",
        nameRes = R.string.russian,
        searchName = "Russian",
        flagEmoji = R.drawable.russia_flag_round
    ),
    AppLanguageUiModel(
        code = "za",
        nameRes = R.string.south_africa,
        searchName = "South Africa",
        flagEmoji = R.drawable.africa_flag_round
    ),
    AppLanguageUiModel(
        code = "fa",
        nameRes = R.string.persian,
        searchName = "Persian",
        flagEmoji = R.drawable.iran_flag_round
    ),
    AppLanguageUiModel(
        code = "it",
        nameRes = R.string.italic,
        searchName = "Italic",
        flagEmoji =R.drawable.italic_flag_round
    )
)
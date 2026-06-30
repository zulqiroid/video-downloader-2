package com.all.video.downloader.fast.hd.secure.video.downloader.localization

import java.util.Locale

enum class AppLocale(
    val languageCode: String,
    val languageTag: String
) {
    ENGLISH(
        languageCode = "en",
        languageTag = "en"
    ),

    URDU(
        languageCode = "ur",
        languageTag = "ur"
    ),

    GERMAN(
        languageCode = "de",
        languageTag = "de"
    ),

    HINDI(
        languageCode = "hi",
        languageTag = "hi"
    ),

    FRENCH(
        languageCode = "fr",
        languageTag = "fr"
    ),

    SPANISH(
        languageCode = "es",
        languageTag = "es"
    ),

    RUSSIAN(
        languageCode = "ru",
        languageTag = "ru"
    ),

    SOUTH_AFRICA(
        languageCode = "za",
        languageTag = "en-ZA"
    ),

    PERSIAN(
        languageCode = "fa",
        languageTag = "fa"
    ),

    ITALIAN(
        languageCode = "it",
        languageTag = "it"
    );

    fun toJavaLocale(): Locale {
        return Locale.forLanguageTag(languageTag)
    }

    companion object {

        val DEFAULT: AppLocale = ENGLISH

        fun fromLanguageCode(code: String?): AppLocale {
            val normalizedCode = code
                ?.trim()
                ?.lowercase(Locale.US)
                .orEmpty()

            return entries.firstOrNull { locale ->
                locale.languageCode == normalizedCode ||
                        locale.languageTag.lowercase(Locale.US) == normalizedCode
            } ?: DEFAULT
        }
    }
}
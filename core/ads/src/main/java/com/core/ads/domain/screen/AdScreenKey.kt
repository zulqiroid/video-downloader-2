package com.core.ads.domain.screen

import java.util.Locale

@JvmInline
value class AdScreenKey private constructor(
    val value: String
) {

    override fun toString(): String {
        return value
    }

    companion object {

        fun from(rawValue: String): AdScreenKey? {
            return rawValue
                .normalize()
                ?.let(::AdScreenKey)
        }

        fun require(rawValue: String): AdScreenKey {
            return from(rawValue)
                ?: error("AdScreenKey cannot be blank or invalid. rawValue=$rawValue")
        }

        private fun String.normalize(): String? {
            return trim()
                .lowercase(Locale.US)
                .replace(INVALID_CHARACTERS_REGEX, "_")
                .replace(MULTIPLE_UNDERSCORES_REGEX, "_")
                .trim('_')
                .takeIf { it.isNotBlank() }
        }

        private val INVALID_CHARACTERS_REGEX =
            Regex(pattern = "[^a-z0-9_]+")

        private val MULTIPLE_UNDERSCORES_REGEX =
            Regex(pattern = "_+")
    }
}
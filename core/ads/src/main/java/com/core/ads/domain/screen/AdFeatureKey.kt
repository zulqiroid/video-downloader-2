package com.core.ads.domain.screen

import java.util.Locale

@JvmInline
value class AdFeatureKey private constructor(
    val value: String
) {

    override fun toString(): String {
        return value
    }

    companion object {

        fun from(rawValue: String): AdFeatureKey? {
            return rawValue
                .normalize()
                ?.let(::AdFeatureKey)
        }

        fun require(rawValue: String): AdFeatureKey {
            return from(rawValue)
                ?: error("AdFeatureKey cannot be blank or invalid. rawValue=$rawValue")
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
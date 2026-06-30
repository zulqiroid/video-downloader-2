package com.core.ads.domain.appopen

/**
 * AppOpenAdState AppOpen ad ki current UI/business state represent karta hai.
 *
 * MVI mindset:
 * - State immutable hoti hai
 * - Manager state ko update karta hai
 * - App state observe kar sakti hai
 *
 * Ye class Google SDK ko directly touch nahi karti.
 * Sirf clean state model hai.
 */
data class AppOpenAdState(
    /**
     * true ka matlab:
     * AppOpen ad currently load ho rahi hai.
     *
     * Edge case:
     * Agar ye true hai to manager duplicate load request block karega.
     */
    val isLoading: Boolean = false,

    /**
     * true ka matlab:
     * AppOpen ad memory me available hai aur show hone ke liye ready hai.
     *
     * Important:
     * Available hone ke baad bhi manager expiry check karega.
     */
    val isAvailable: Boolean = false,

    /**
     * true ka matlab:
     * AppOpen ad currently screen par show ho rahi hai.
     *
     * Edge case:
     * Jab ye true ho, to current Activity tracking pause karni hoti hai
     * taake AdActivity current app Activity ke طور par save na ho.
     */
    val isShowing: Boolean = false,

    /**
     * Last successful load ka time.
     *
     * Isse hum check karenge ke loaded ad expired to nahi.
     */
    val lastLoadedAtMillis: Long? = null,

    /**
     * Last successful show ka time.
     *
     * Isse hum cooldown apply karenge taake user ko frequently AppOpen ad na dikhe.
     */
    val lastShownAtMillis: Long? = null,

    /**
     * Last failure ka readable reason.
     *
     * Ye debug logs, QA aur future analytics ke liye useful hai.
     */
    val lastErrorMessage: String? = null
)
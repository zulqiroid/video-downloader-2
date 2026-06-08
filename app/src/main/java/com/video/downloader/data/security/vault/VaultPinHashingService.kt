package com.video.downloader.data.security.vault

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaultPinHashingService @Inject constructor() {

    private val secureRandom = SecureRandom()

    fun generateSalt(): String {
        val bytes = ByteArray(SALT_BYTES)
        secureRandom.nextBytes(bytes)

        return Base64.encodeToString(
            bytes,
            Base64.NO_WRAP
        )
    }

    fun hashPin(
        pin: String,
        salt: String
    ): String {
        val digest = MessageDigest.getInstance("SHA-256")

        val payload = "$salt:$pin:$APP_PEPPER"
            .toByteArray(Charsets.UTF_8)

        val hashBytes = digest.digest(payload)

        return Base64.encodeToString(
            hashBytes,
            Base64.NO_WRAP
        )
    }

    fun matchesPin(
        pin: String,
        salt: String,
        expectedHash: String
    ): Boolean {
        val actualHash = hashPin(
            pin = pin,
            salt = salt
        )

        return MessageDigest.isEqual(
            actualHash.toByteArray(Charsets.UTF_8),
            expectedHash.toByteArray(Charsets.UTF_8)
        )
    }

    companion object {
        private const val SALT_BYTES = 32
        private const val APP_PEPPER = "video_downloader_vault_security_v1"
    }
}
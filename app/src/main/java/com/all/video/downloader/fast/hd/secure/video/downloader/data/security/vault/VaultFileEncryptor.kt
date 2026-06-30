package com.all.video.downloader.fast.hd.secure.video.downloader.data.security.vault

import android.content.Context
import android.net.Uri
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.CipherOutputStream
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaultFileEncryptor @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun encryptToFile(
        sourceUri: Uri,
        encryptedOutputFile: File
    ): VaultEncryptionResult {
        val cipher = Cipher.getInstance(TRANSFORMATION)

        /*
         * Important:
         * Do NOT provide your own IV for encryption when using Android Keystore
         * with randomized encryption enabled.
         *
         * Android Keystore will generate a secure random IV automatically.
         */
        cipher.init(
            Cipher.ENCRYPT_MODE,
            getOrCreateSecretKey()
        )

        val generatedIv = cipher.iv
            ?: throw IllegalStateException("Unable to generate secure Vault IV.")

        encryptedOutputFile.parentFile?.mkdirs()

        try {
            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                encryptedOutputFile.outputStream().use { fileOutput ->
                    CipherOutputStream(fileOutput, cipher).use { cipherOutput ->
                        input.copyTo(
                            out = cipherOutput,
                            bufferSize = BUFFER_SIZE
                        )
                    }
                }
            } ?: throw IllegalStateException("Unable to read selected file.")

            return VaultEncryptionResult(
                encryptedFilePath = encryptedOutputFile.absolutePath,
                ivBase64 = Base64.encodeToString(
                    generatedIv,
                    Base64.NO_WRAP
                )
            )
        } catch (throwable: Throwable) {
            encryptedOutputFile.delete()
            throw throwable
        }
    }

    fun createDecryptCipher(
        ivBase64: String
    ): Cipher {
        val iv = Base64.decode(
            ivBase64,
            Base64.NO_WRAP
        )

        return Cipher.getInstance(TRANSFORMATION).apply {
            init(
                Cipher.DECRYPT_MODE,
                getOrCreateSecretKey(),
                GCMParameterSpec(
                    GCM_TAG_SIZE_BITS,
                    iv
                )
            )
        }
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply {
            load(null)
        }

        val existingKey = keyStore.getKey(KEY_ALIAS, null)

        if (existingKey is SecretKey) {
            return existingKey
        }

        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE
        )

        val keySpec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(KEY_SIZE_BITS)

            /*
             * Keep this true.
             * It protects encryption from unsafe IV reuse.
             */
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(keySpec)

        return keyGenerator.generateKey()
    }

    companion object {
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "video_downloader_vault_file_key"

        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val KEY_SIZE_BITS = 256
        private const val GCM_TAG_SIZE_BITS = 128

        private const val BUFFER_SIZE = 64 * 1024
    }
}

data class VaultEncryptionResult(
    val encryptedFilePath: String,
    val ivBase64: String
)
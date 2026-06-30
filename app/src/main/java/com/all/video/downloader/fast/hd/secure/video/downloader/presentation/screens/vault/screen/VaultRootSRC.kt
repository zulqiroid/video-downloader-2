package com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.screen

import android.app.Activity
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.FragmentActivity
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.all.video.downloader.fast.hd.secure.video.downloader.domain.models.MediaFile
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.navigation.Screen
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.events.VaultEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.events.VaultEvents.*
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.events.VaultNavEvents
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.screens.vault.viewModel.VaultViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun VaultRootSRC(
    onNavigateHome: () -> Unit,
    viewModel: VaultViewModel = hiltViewModel(),
    backStack: NavBackStack<NavKey>,
    mainPaddingValues: PaddingValues,
    showPremiumLabel: Boolean,
    onPremiumClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = LocalActivity.current as? FragmentActivity

    var pendingDeleteRequest by remember {
        mutableStateOf<PendingOriginalDeleteRequest?>(null)
    }

    val originalDeleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        val pending = pendingDeleteRequest ?: return@rememberLauncherForActivityResult

        val deleted = result.resultCode == Activity.RESULT_OK

        viewModel.onEvent(
            VaultEvents.OriginalFileDeleteResult(
                vaultFileId = pending.vaultFileId,
                deleted = deleted,
                message = if (deleted) {
                    null
                } else {
                    "File secured in Vault, but original file was not removed from Gallery."
                }
            )
        )

        pendingDeleteRequest = null
    }

    val vaultFilePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        context.takePersistableVaultFilePermission(uri)

        viewModel.onEvent(
            VaultEvents.VaultFileSelected(
                uri = uri
            )
        )
    }

    LaunchedEffect(Unit) {
        val biometricAvailable = BiometricManager
            .from(context)
            .canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) ==
                BiometricManager.BIOMETRIC_SUCCESS

        viewModel.onEvent(
            VaultEvents.BiometricAvailabilityChanged(
                available = biometricAvailable
            )
        )
    }

    LaunchedEffect(viewModel.navEvents, activity) {
        viewModel.navEvents.collect { event ->
            when (event) {
                VaultNavEvents.NavigateHome -> {
                    onNavigateHome()
                }

                VaultNavEvents.LaunchVaultFilePicker -> {
                    vaultFilePickerLauncher.launch(
                        arrayOf(
                            "video/*",
                            "audio/*"
                        )
                    )
                }

                is VaultNavEvents.RequestOriginalFileDelete -> {
                    when (
                        val action = context.createOriginalFileDeleteAction(
                            originalUriString = event.originalUri
                        )
                    ) {
                        OriginalFileDeleteAction.Deleted -> {
                            viewModel.onEvent(
                                OriginalFileDeleteResult(
                                    vaultFileId = event.vaultFileId,
                                    deleted = true
                                )
                            )
                        }

                        is OriginalFileDeleteAction.NeedsUserApproval -> {
                            pendingDeleteRequest = PendingOriginalDeleteRequest(
                                vaultFileId = event.vaultFileId,
                                originalUri = event.originalUri
                            )

                            originalDeleteLauncher.launch(action.request)
                        }

                        is OriginalFileDeleteAction.Failed -> {
                            viewModel.onEvent(
                                OriginalFileDeleteResult(
                                    vaultFileId = event.vaultFileId,
                                    deleted = false,
                                    message = action.message
                                )
                            )
                        }

                    }
                }

                VaultNavEvents.LaunchBiometricUnlock -> {
                    if (activity == null) {
                        viewModel.onEvent(
                            BiometricAuthFailed(
                                message = "Biometric unlock is not available right now."
                            )
                        )
                    } else {
                        launchVaultBiometricPrompt(
                            activity = activity,
                            onSuccess = {
                                viewModel.onEvent(VaultEvents.BiometricAuthSucceeded)
                            },
                            onFailed = { message ->
                                viewModel.onEvent(
                                    BiometricAuthFailed(message)
                                )
                            },
                            onCancelled = {
                                viewModel.onEvent(VaultEvents.BiometricAuthCancelled)
                            }
                        )
                    }
                }

                is VaultNavEvents.OpenVaultMedia -> {
                    backStack.add(
                        Screen.MediaPlayer(
                            mediaList = event.mediaList,
                            startIndex = event.startIndex
                        )
                    )
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.onEvent(VaultEvents.ScreenLeft)
        }
    }

    VaultSRC(
        mainPaddingValues = mainPaddingValues,
        state = state,
        onEvent = viewModel::onEvent,
        showPremiumLabel = showPremiumLabel,
        onPremiumClick = onPremiumClick,
    )
}

private data class PendingOriginalDeleteRequest(
    val vaultFileId: String,
    val originalUri: String
)

private sealed interface OriginalFileDeleteAction {
    data object Deleted : OriginalFileDeleteAction

    data class NeedsUserApproval(
        val request: IntentSenderRequest
    ) : OriginalFileDeleteAction

    data class Failed(
        val message: String
    ) : OriginalFileDeleteAction
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun Context.createOriginalFileDeleteAction(
    originalUriString: String
): OriginalFileDeleteAction {
    val originalUri = Uri.parse(originalUriString)

    val deleteUri = originalUri.toMediaStoreUri(context = this)
        ?: originalUri

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        return runCatching {
            val pendingIntent = MediaStore.createDeleteRequest(
                contentResolver,
                listOf(deleteUri)
            )

            OriginalFileDeleteAction.NeedsUserApproval(
                IntentSenderRequest.Builder(
                    pendingIntent.intentSender
                ).build()
            )
        }.getOrElse {
            tryDirectDelete(
                deleteUri = deleteUri,
                fallbackUri = originalUri
            )
        }
    }

    return tryDirectDelete(
        deleteUri = deleteUri,
        fallbackUri = originalUri
    )
}

@RequiresApi(Build.VERSION_CODES.Q)
private fun Context.tryDirectDelete(
    deleteUri: Uri,
    fallbackUri: Uri
): OriginalFileDeleteAction {
    return try {
        val rows = contentResolver.delete(
            deleteUri,
            null,
            null
        )

        if (rows > 0) {
            OriginalFileDeleteAction.Deleted
        } else {
            tryDocumentFileDelete(fallbackUri)
        }
    } catch (exception: RecoverableSecurityException) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            OriginalFileDeleteAction.NeedsUserApproval(
                IntentSenderRequest.Builder(
                    exception.userAction.actionIntent.intentSender
                ).build()
            )
        } else {
            OriginalFileDeleteAction.Failed(
                "File secured in Vault, but Android did not allow removing original file."
            )
        }
    } catch (_: Throwable) {
        tryDocumentFileDelete(fallbackUri)
    }
}

private fun Context.tryDocumentFileDelete(
    uri: Uri
): OriginalFileDeleteAction {
    val deleted = runCatching {
        DocumentFile.fromSingleUri(this, uri)?.delete() == true
    }.getOrDefault(false)

    return if (deleted) {
        OriginalFileDeleteAction.Deleted
    } else {
        OriginalFileDeleteAction.Failed(
            "File secured in Vault, but original file was not removed from Gallery."
        )
    }
}

private fun Uri.toMediaStoreUri(
    context: Context
): Uri? {
    if (authority == "media") {
        return this
    }

    if (!DocumentsContract.isDocumentUri(context, this)) {
        return null
    }

    if (authority != "com.android.providers.media.documents") {
        return null
    }

    val documentId = DocumentsContract.getDocumentId(this)
    val split = documentId.split(":")

    if (split.size != 2) return null

    val type = split[0]
    val id = split[1].toLongOrNull() ?: return null

    val baseUri = when (type) {
        "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        else -> return null
    }

    return ContentUris.withAppendedId(
        baseUri,
        id
    )
}

private fun Context.takePersistableVaultFilePermission(uri: Uri) {
    runCatching {
        contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION
        )
    }
}

private fun launchVaultBiometricPrompt(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onFailed: (String) -> Unit,
    onCancelled: () -> Unit
) {
    val executor = ContextCompat.getMainExecutor(activity)

    val biometricPrompt = BiometricPrompt(
        activity,
        executor,
        object : BiometricPrompt.AuthenticationCallback() {

            override fun onAuthenticationSucceeded(
                result: BiometricPrompt.AuthenticationResult
            ) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onFailed("Biometric not recognized. Try again or use PIN.")
            }

            override fun onAuthenticationError(
                errorCode: Int,
                errString: CharSequence
            ) {
                super.onAuthenticationError(errorCode, errString)

                when (errorCode) {
                    BiometricPrompt.ERROR_NEGATIVE_BUTTON,
                    BiometricPrompt.ERROR_USER_CANCELED,
                    BiometricPrompt.ERROR_CANCELED -> {
                        onCancelled()
                    }

                    else -> {
                        onFailed(errString.toString())
                    }
                }
            }
        }
    )

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Unlock Vault")
        .setSubtitle("Use biometric unlock to access your private files")
        .setNegativeButtonText("Use PIN")
        .setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_WEAK
        )
        .build()

    biometricPrompt.authenticate(promptInfo)
}
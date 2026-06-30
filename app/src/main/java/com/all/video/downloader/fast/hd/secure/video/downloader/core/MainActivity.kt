package com.all.video.downloader.fast.hd.secure.video.downloader.core

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.fragment.app.FragmentActivity
import com.all.video.downloader.fast.hd.secure.video.downloader.data.worker.DownloadNotificationContract
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.navigation.AppNavigationGraph
import com.all.video.downloader.fast.hd.secure.video.downloader.presentation.theme.VideoDownloaderTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.all.video.downloader.fast.hd.secure.video.downloader.ads.ui.GlobalAdLoadingOverlay
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.all.video.downloader.fast.hd.secure.video.downloader.localization.AppLocale
import com.all.video.downloader.fast.hd.secure.video.downloader.localization.AppLocaleController
import com.all.video.downloader.fast.hd.secure.video.downloader.localization.LocalizedAppProvider
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

@AndroidEntryPoint
class MainActivity : FragmentActivity() {

    @Inject
    lateinit var appLocaleController: AppLocaleController

    private var notificationOpenRequestId by mutableLongStateOf(0L)

    private val _userLeaveHints = MutableSharedFlow<Unit>(
        extraBufferCapacity = 1
    )
    val userLeaveHints = _userLeaveHints.asSharedFlow()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        updateNotificationOpenRequest(intent)

        setContent {
            val appLocale by appLocaleController
                .observeAppLocale()
                .collectAsStateWithLifecycle(
                    initialValue = AppLocale.DEFAULT
                )

     /*       LaunchedEffect(appLocale) {
                appLocaleController.applyPlatformLocale(
                    context = this@MainActivity,
                    appLocale = appLocale
                )
            }
*/
            AppStartupPermissionRequester()

            LocalizedAppProvider(
                appLocale = appLocale,
                appLocaleController = appLocaleController
            ) {
                VideoDownloaderTheme {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        AppNavigationGraph(
                            notificationOpenRequestId = notificationOpenRequestId
                        )

                        GlobalAdLoadingOverlay()
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setIntent(intent)
        updateNotificationOpenRequest(intent)
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        _userLeaveHints.tryEmit(Unit)
    }

    private fun updateNotificationOpenRequest(intent: Intent?) {
        if (    DownloadNotificationContract.isOpenDownloadProgressIntent(intent)) {
            notificationOpenRequestId = System.currentTimeMillis()
        }
    }

    @Composable
    private fun AppStartupPermissionRequester() {
        val context = LocalContext.current

        val permissions = remember {
            buildList {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(Manifest.permission.POST_NOTIFICATIONS)
                    add(Manifest.permission.READ_MEDIA_VIDEO)
                    add(Manifest.permission.READ_MEDIA_AUDIO)
                } else {
                    add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.distinct()
        }

        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) {
            /**
             * Result intentionally ignored.
             * Individual features already handle denied permission gracefully.
             */
        }

        LaunchedEffect(permissions) {
            val missingPermissions = permissions.filter { permission ->
                ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            }

            if (missingPermissions.isNotEmpty()) {
                permissionLauncher.launch(
                    missingPermissions.toTypedArray()
                )
            }
        }
    }
}
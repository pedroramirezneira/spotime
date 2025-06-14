package com.mediaverse.spotime

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.firebase.FirebaseApp
import com.mediaverse.spotime.authentication.BiometricAuthManager
import com.mediaverse.spotime.authentication.Constants
import com.mediaverse.spotime.notification.notificationChannelID
import com.mediaverse.spotime.ui.components.AppBar
import com.mediaverse.spotime.ui.navigation.BottomBar
import com.mediaverse.spotime.ui.navigation.Navigation
import com.mediaverse.spotime.ui.screens.LoadingScreen
import com.mediaverse.spotime.ui.screens.login.LoginScreen
import com.mediaverse.spotime.ui.screens.login.SpotifyViewModel
import com.mediaverse.spotime.ui.theme.SpotiMeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    @Inject lateinit var biometricAuthManager: BiometricAuthManager
    private var redirectIntent: Intent? = null

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()

        if (intent?.data?.toString()?.startsWith(Constants.REDIRECT_URI) == true) {
            redirectIntent = intent
        }

        val shouldAuthenticate = intent?.data == null

        setContent {
            var isAuthenticated by remember { mutableStateOf(!shouldAuthenticate) }
            val postNotificationPermission = rememberPermissionState(permission = Manifest.permission.POST_NOTIFICATIONS)

            LaunchedEffect(Unit) {
                if (shouldAuthenticate) {
                    biometricAuthManager.authenticate(
                        context = this@MainActivity,
                        onError = { finish() },
                        onFail = { finish() },
                        onSuccess = { isAuthenticated = true }
                    )
                }
                if (!postNotificationPermission.status.isGranted) {
                    postNotificationPermission.launchPermissionRequest()
                }
            }

            if (!isAuthenticated) {
                SpotiMeTheme { LoadingScreen() }
                return@setContent
            }

            val spotifyViewModel: SpotifyViewModel = hiltViewModel()
            val isLoggedIn by spotifyViewModel.isLoggedIn.collectAsState()
            val isLoading by spotifyViewModel.isLoading.collectAsState()
            val navController = rememberNavController()

            LaunchedEffect(redirectIntent) {
                redirectIntent?.let {
                    spotifyViewModel.handleAuthResponse(it)
                    redirectIntent = null
                }
            }

            SpotiMeTheme {
                when {
                    isLoading -> LoadingScreen()
                    !isLoggedIn -> LoginScreen(
                        redirectIntent = redirectIntent,
                        clearIntent = { redirectIntent = null }
                    )
                    else -> Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = { AppBar(navController) },
                        bottomBar = { BottomBar { navController.navigate(it) } }
                    ) { innerPadding ->
                        Navigation(innerPadding, navController)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if (intent.data?.toString()?.startsWith(Constants.REDIRECT_URI) == true) {
            redirectIntent = intent
        }
    }

    private fun createNotificationChannel() {
        val notificationChannel = NotificationChannel(
            notificationChannelID,
            "SpotiMe!",
            NotificationManager.IMPORTANCE_HIGH
        )
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.createNotificationChannel(notificationChannel)
    }
}

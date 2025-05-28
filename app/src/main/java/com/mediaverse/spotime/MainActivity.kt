package com.mediaverse.spotime

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
import com.google.firebase.FirebaseApp
import com.mediaverse.spotime.authentication.BiometricAuthManager
import com.mediaverse.spotime.authentication.Constants
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
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        if (intent?.data?.toString()?.startsWith(Constants.REDIRECT_URI) == true) {
            redirectIntent = intent
        }

        // Compose state to control UI after biometric success
        var isAuthenticated by mutableStateOf(false)

        biometricAuthManager.authenticate(
            context = this,
            onError = {
                finish() // Close app or show alternative screen
            },
            onFail = {
                finish() // Optionally show retry screen
            },
            onSuccess = {
                isAuthenticated = true
            }
        )

        setContent {
            if (!isAuthenticated) {
                // Optional loading screen while waiting for auth
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
                    isLoading -> {
                        LoadingScreen()
                    }

                    !isLoggedIn -> {
                        LoginScreen(
                            redirectIntent = redirectIntent,
                            clearIntent = { redirectIntent = null }
                        )
                    }

                    else -> {
                        Scaffold(
                            modifier = Modifier
                                .fillMaxSize(),
                            topBar = {
                                AppBar(navController)
                            },
                            bottomBar = {
                                BottomBar { navController.navigate(it) }
                            },
                        ) { innerPadding ->
                            Navigation(innerPadding, navController)
                        }
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
}

package com.mediaverse.spotime

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.mediaverse.spotime.authentication.Constants
import com.mediaverse.spotime.ui.screens.LoginScreen
import com.mediaverse.spotime.ui.screens.SpotifyViewModel
import com.mediaverse.spotime.ui.navigation.Navigation
import com.mediaverse.spotime.ui.theme.SpotiMeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val spotifyViewModel: SpotifyViewModel by viewModels()
    private var redirectIntent: Intent? = null

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent?.data?.toString()?.startsWith(Constants.REDIRECT_URI) == true) {
            redirectIntent = intent
        }

        setContent {
            val isLoggedIn by spotifyViewModel.isLoggedIn.collectAsState()
            val userName by spotifyViewModel.userName.collectAsState()
            val isLoading by spotifyViewModel.isLoading.collectAsState()

            val navController = rememberNavController()

            LaunchedEffect(redirectIntent) {
                redirectIntent?.let {
                    spotifyViewModel.handleAuthResponse(it)
                    redirectIntent = null
                }
            }

            SpotiMeTheme(dynamicColor = false) {
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    !isLoggedIn || userName == null -> {
                        LoginScreen(
                            viewModel = spotifyViewModel,
                            redirectIntent = redirectIntent,
                            clearIntent = { redirectIntent = null }
                        )
                    }

                    else -> {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            topBar = {
                                TopAppBar(title = { Text("Welcome $userName") })
                            },
                            bottomBar = {
                                NavigationBar {
                                    NavigationBarItem(
                                        selected = true,
                                        onClick = { /* TODO */ },
                                        icon = {
                                            Icon(
                                                Icons.Default.Home,
                                                contentDescription = null
                                            )
                                        },
                                        label = { Text("Home") }
                                    )
                                }
                            }
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

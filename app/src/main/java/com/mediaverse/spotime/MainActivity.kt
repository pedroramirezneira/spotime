package com.mediaverse.spotime

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.rememberNavController
import com.mediaverse.spotime.authentication.Constants
import com.mediaverse.spotime.screens.SpotifyLoginScreen
import com.mediaverse.spotime.screens.SpotifyViewModel
import com.mediaverse.spotime.ui.theme.SpotiMeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val spotifyViewModel: SpotifyViewModel by viewModels()
    private var redirectIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent?.data?.toString()?.startsWith(Constants.REDIRECT_URI) == true) {
            redirectIntent = intent
        }

        setContent {
            val navController = rememberNavController()

            SpotiMeTheme {
                SpotifyLoginScreen(
                    viewModel = spotifyViewModel,
                    redirectIntent = redirectIntent,
                    clearIntent = { redirectIntent = null },
                )
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

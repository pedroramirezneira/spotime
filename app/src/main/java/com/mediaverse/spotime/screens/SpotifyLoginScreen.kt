package com.mediaverse.spotime.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mediaverse.spotime.authentication.Constants

@Composable
fun SpotifyLoginScreen(
    viewModel: SpotifyViewModel,
    redirectIntent: Intent?,
    clearIntent: () -> Unit
) {
    val context = LocalContext.current
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val userName by viewModel.userName.collectAsState()

    val launcher = rememberLauncherForActivityResult(StartActivityForResult()) { }

    LaunchedEffect(redirectIntent) {
        redirectIntent?.data?.let { data ->
            if (data.toString().startsWith(Constants.REDIRECT_URI)) {
                viewModel.handleAuthResponse(redirectIntent)
                clearIntent()
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isLoggedIn) {
                Button(onClick = {
                    val authIntent = viewModel.getAuthIntent()
                    launcher.launch(authIntent)
                }) {
                    Text("Login with Spotify")
                }
            } else {
                Text("Hola, ${userName ?: "..."}", style = MaterialTheme.typography.headlineMedium)
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = { viewModel.logout() }) {
                    Text("Logout")
                }
            }
        }
    }
}

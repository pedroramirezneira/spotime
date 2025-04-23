package com.mediaverse.spotime.ui.screens

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.mediaverse.spotime.R
import com.mediaverse.spotime.authentication.Constants
import com.mediaverse.spotime.ui.theme.WelcomeGap
import com.mediaverse.spotime.ui.theme.WelcomeWidth

@Composable
fun LoginScreen(
    viewModel: SpotifyViewModel,
    redirectIntent: Intent?,
    clearIntent: () -> Unit,
) {
    val launcher = rememberLauncherForActivityResult(StartActivityForResult()) {}

    LaunchedEffect(redirectIntent) {
        redirectIntent?.data?.let { uri ->
            if (uri.toString().startsWith(Constants.REDIRECT_URI)) {
                println("🎯 Processing Spotify redirect URI")
                viewModel.handleAuthResponse(redirectIntent)
                clearIntent()
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(WelcomeGap))

            Image(
                painter = painterResource(id = R.drawable.isologo),
                contentDescription = "SpotiMe! Logo",
                modifier = Modifier.width(WelcomeWidth)
            )

            Spacer(modifier = Modifier.height(WelcomeGap))

            Text(
                text = stringResource(R.string.welcome_text),
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Start,
                modifier = Modifier.width(WelcomeWidth)
            )

            Spacer(modifier = Modifier.height(WelcomeGap))

            Button(
                onClick = {
                    val authIntent = viewModel.getAuthIntent()
                    launcher.launch(authIntent)
                },
                modifier = Modifier.width(WelcomeWidth)
            ) {
                Text("Login with Spotify")
            }

            Spacer(modifier = Modifier.height(WelcomeGap))
        }
    }
}

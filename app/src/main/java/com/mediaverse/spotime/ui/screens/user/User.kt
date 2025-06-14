package com.mediaverse.spotime.ui.screens.user

import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.google.gson.Gson
import com.mediaverse.spotime.R
import com.mediaverse.spotime.ui.components.TrackRow
import com.mediaverse.spotime.ui.navigation.Screens
import com.mediaverse.spotime.ui.theme.*

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun User(navController: NavController) {
    val viewModel = hiltViewModel<UserViewModel>()
    val userData by viewModel.userData.collectAsStateWithLifecycle()
    val historyTracks by viewModel.historyTracks.collectAsStateWithLifecycle()
    val firebaseReady by viewModel.firebaseReady.collectAsStateWithLifecycle()
    val isLoadingHistory by viewModel.isLoadingTracks.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val googleSignInLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.handleGoogleSignInIntent(result.data)
        }

    LaunchedEffect(Unit) {
        if (historyTracks.isEmpty()) {
            viewModel.fetchListenedTracks()
        }
    }

    LaunchedEffect(firebaseReady, userData) {
        if (firebaseReady && userData == null) {
            val manufacturer = Build.MANUFACTURER.lowercase()
            if (manufacturer == "samsung") {
                // Dispositivo Samsung – usar flujo legacy
                val legacyIntent = viewModel.getLegacyGoogleSignInIntent(context)
                googleSignInLauncher.launch(legacyIntent)
            } else {
                // Dispositivos compatibles con Credential Manager
                viewModel.launchCredentialManager(context)
            }
        }
    }

    if (!firebaseReady || userData == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = BottomBarHeight),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(ListPadding)
    ) {
        item {
            Column(modifier = Modifier.padding(horizontal = ViewPadding)) {
                AsyncImage(
                    model = userData?.photoUrl,
                    contentDescription = null,
                    modifier = Modifier.size(ImageSize).clip(RoundedCornerShape(BorderRadius))
                )
                Spacer(Modifier.height(RowGap))

                Text(
                    userData?.displayName ?: "",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    userData?.email ?: "",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(ColumnGap))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(RowGap),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.signOut() }
                    ) {
                        Text(stringResource(R.string.sign_out))
                    }

                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = { viewModel.clearHistory() }
                    ) {
                        Text(stringResource(R.string.clear_history))
                    }
                }

                Spacer(modifier = Modifier.height(ColumnGap))
                Text(stringResource(R.string.tracks_history), style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(RowGap))
            }
        }

        when {
            isLoadingHistory -> {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            historyTracks.isEmpty() -> {
                item {
                    Text(
                        stringResource(R.string.empty_tracks_history),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = ViewPadding),
                    )
                }
            }

            else -> {
                itemsIndexed(historyTracks.asReversed()) { index, track ->
                    TrackRow(index = index, track = track, onClick = {
                        val trackJson = Gson().toJson(track)
                        val encoded = Uri.encode(trackJson)
                        navController.navigate(Screens.TrackDetails.withArgs(encoded))
                    })
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(BottomBarHeight))
        }
    }
}

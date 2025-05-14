package com.mediaverse.spotime.ui.screens.tracks

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.gson.Gson
import com.mediaverse.spotime.ui.components.TrackRow
import com.mediaverse.spotime.ui.navigation.Screens
import com.mediaverse.spotime.ui.theme.BottomBarHeight

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TracksScreen(navController: NavController) {
    val viewModel: TracksViewModel = hiltViewModel()
    val tracks by viewModel.tracks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchTopTracks()
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = BottomBarHeight),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                itemsIndexed(tracks) { index, track ->
                    TrackRow(index = index, track = track) {
                        val trackJson = Gson().toJson(track)
                        val encoded = Uri.encode(trackJson)
                        navController.navigate(Screens.TrackDetails.withArgs(encoded))
                    }
                }
                item {
                    Spacer(Modifier.height(BottomBarHeight))
                }
            }
        }
}

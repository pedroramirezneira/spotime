package com.mediaverse.spotime.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediaverse.spotime.ui.components.TrackRow
import com.mediaverse.spotime.ui.theme.BottomBarHeight
import com.mediaverse.spotime.ui.theme.ViewPadding

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TracksScreen() {
    val viewModel: TracksViewModel = hiltViewModel()
    val tracks by viewModel.tracks.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchTopTracks()
    }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = ViewPadding)
            ) {
                itemsIndexed(tracks) { index, track ->
                    TrackRow(index = index, track = track)
                }
                item {
                    Spacer(Modifier.height(BottomBarHeight))
                }
            }
        }
}

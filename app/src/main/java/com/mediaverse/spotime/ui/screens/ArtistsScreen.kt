package com.mediaverse.spotime.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.mediaverse.spotime.ui.components.ArtistRow
import com.mediaverse.spotime.ui.theme.BottomBarHeight
import com.mediaverse.spotime.ui.theme.ViewPadding

@Composable
fun ArtistsScreen() {
    val viewModel: ArtistsViewModel = hiltViewModel()
    val artists by viewModel.artists.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchTopArtists()
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = ViewPadding)
        ) {
            itemsIndexed(
                items = artists,
                key = { _, artist -> artist.id }
            ) { index, artist ->
                ArtistRow(index = index, artist = artist)
            }
            item {
                Spacer(Modifier.height(BottomBarHeight))
            }
        }
    }
}

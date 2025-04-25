package com.mediaverse.spotime.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.gson.Gson
import com.mediaverse.spotime.model.ArtistData
import com.mediaverse.spotime.model.TrackData
import com.mediaverse.spotime.ui.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(navController: NavController) {
    val backStackEntry = navController.currentBackStackEntryAsState().value
    val route = backStackEntry?.destination?.route
    val title = when {
        route == Screens.Artists.route -> "Your Artists"
        route == Screens.Tracks.route -> "Your Tracks"
        route?.startsWith(Screens.TrackDetails.route) == true -> {
            val trackJson = backStackEntry.arguments?.getString("trackJson")
            if (trackJson != null) {
                val track =
                    remember(trackJson) { Gson().fromJson(trackJson, TrackData::class.java) }
                track.name
            } else {
                "Track Details"
            }
        }

        route?.startsWith(Screens.ArtistDetails.route) == true -> {
            val artistJson = backStackEntry.arguments?.getString("artistJson")
            if (artistJson != null) {
                val artist =
                    remember(artistJson) { Gson().fromJson(artistJson, ArtistData::class.java) }
                artist.name
            } else {
                "Artist Details"
            }
        }

        else -> "SpotiMe!"
    }
    TopAppBar(
        title = {
            Text(title)
        }
    )
}
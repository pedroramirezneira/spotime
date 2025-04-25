package com.mediaverse.spotime.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.gson.Gson
import com.mediaverse.spotime.model.ArtistData
import com.mediaverse.spotime.model.TrackData
import com.mediaverse.spotime.ui.screens.ArtistDetailsScreen
import com.mediaverse.spotime.ui.screens.ArtistsScreen
import com.mediaverse.spotime.ui.screens.TrackDetailsScreen
import com.mediaverse.spotime.ui.screens.TracksScreen

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Navigation(
    innerPadding: PaddingValues,
    navController: NavHostController,
) {
    val padding = remember(innerPadding) {
        PaddingValues(
            start = innerPadding.calculateStartPadding(layoutDirection = LayoutDirection.Ltr),
            top = innerPadding.calculateTopPadding(),
            end = innerPadding.calculateEndPadding(layoutDirection = LayoutDirection.Ltr),
            bottom = 0.dp
        )
    }
    NavHost(
        navController = navController,
        startDestination = Screens.Artists.route,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        enterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        },
    ) {
        composable(Screens.Artists.route) {
            ArtistsScreen(navController)
        }
        composable(Screens.Tracks.route) {
            TracksScreen(navController)
        }
        composable(
            route = "${Screens.TrackDetails.route}/{trackJson}",
            arguments = listOf(navArgument("trackJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val trackJson = backStackEntry.arguments?.getString("trackJson")
            val track = remember { Gson().fromJson(trackJson, TrackData::class.java) }
            TrackDetailsScreen(track)
        }

        composable(
            route = "${Screens.ArtistDetails.route}/{artistJson}",
            arguments = listOf(navArgument("artistJson") { type = NavType.StringType })
        ) { backStackEntry ->
            val artistJson = backStackEntry.arguments?.getString("artistJson") ?: ""
            val artist = Gson().fromJson(artistJson, ArtistData::class.java)
            ArtistDetailsScreen(artist)
        }
    }
}

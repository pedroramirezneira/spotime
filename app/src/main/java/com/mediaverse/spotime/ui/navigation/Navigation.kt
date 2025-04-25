package com.mediaverse.spotime.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mediaverse.spotime.ui.screens.ArtistsScreen
import com.mediaverse.spotime.ui.screens.HomeScreen
import com.mediaverse.spotime.ui.screens.TracksScreen

@Composable
fun Navigation(
    innerPadding: PaddingValues,
    navController: NavHostController,
) {
    val topPadding = remember { innerPadding.calculateTopPadding() }

    NavHost(
        navController = navController,
        startDestination = Screens.Artists.name,
        modifier = Modifier.fillMaxSize().padding(top = topPadding),
        enterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        },
    ) {
        composable(Screens.Artists.name) {
            ArtistsScreen()
        }
        composable(Screens.Tracks.name) {
            TracksScreen()
        }
    }
}

package com.mediaverse.spotime.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mediaverse.spotime.ui.screens.HomeScreen
import com.mediaverse.spotime.ui.screens.TracksScreen

@Composable
fun Navigation(
    innerPadding: PaddingValues,
    navController: NavHostController,
) {
    val modifier = Modifier.padding(innerPadding)
    NavHost(
        navController = navController,
        startDestination = Screens.Home.name,
        modifier = modifier,
        enterTransition = {
            EnterTransition.None
        },
        exitTransition = {
            ExitTransition.None
        },
    ) {
        composable(Screens.Home.name) {
            HomeScreen()
        }
        composable(Screens.Tracks.name) {
            TracksScreen()
        }
    }
}

package com.mediaverse.spotime.ui.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.mediaverse.spotime.ui.navigation.Screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(navController: NavController) {
    val route = navController.currentBackStackEntryAsState().value?.destination?.route
    val title = when (route) {
        Screens.Home.name -> "Home"
        Screens.Tracks.name -> "Your Tracks"
        else -> "SpotiMe!"
    }
    TopAppBar(
        title = {
            Text(title)
        }
    )
}
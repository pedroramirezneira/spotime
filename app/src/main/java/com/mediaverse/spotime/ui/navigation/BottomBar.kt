package com.mediaverse.spotime.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember

@Composable
fun BottomBar(onNavigate: (String) -> Unit) {
    val navIndex = remember { mutableIntStateOf(0) }

    NavigationBar() {
        NavigationBarItem(
            selected = navIndex.intValue == 0,
            onClick = {
                onNavigate(Screens.Home.name)
                navIndex.intValue = 0
            },
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = null
                )
            },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = navIndex.intValue == 1,
            onClick = {
                onNavigate(Screens.Tracks.name)
                navIndex.intValue = 1
            },
            icon = {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = null
                )
            },
            label = { Text("Tracks") }
        )
    }
}

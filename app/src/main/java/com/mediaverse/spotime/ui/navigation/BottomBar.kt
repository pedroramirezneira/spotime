package com.mediaverse.spotime.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
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
                if (navIndex.intValue == 0) return@NavigationBarItem
                onNavigate(Screens.Artists.name)
                navIndex.intValue = 0
            },
            icon = {
                Icon(
                    Icons.Rounded.Person,
                    contentDescription = null
                )
            },
            label = { Text("Artists") }
        )
        NavigationBarItem(
            selected = navIndex.intValue == 1,
            onClick = {
                if (navIndex.intValue == 1) return@NavigationBarItem
                onNavigate(Screens.Tracks.name)
                navIndex.intValue = 1
            },
            icon = {
                Icon(
                    Icons.Rounded.PlayArrow,
                    contentDescription = null
                )
            },
            label = { Text("Tracks") }
        )
    }
}

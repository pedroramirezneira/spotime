package com.mediaverse.spotime.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.mediaverse.spotime.ui.theme.BottomBarHeight
import com.mediaverse.spotime.ui.theme.ViewPadding

@Composable
fun BottomBar(onNavigate: (String) -> Unit) {
    val navIndex = remember { mutableIntStateOf(0) }

    val items = listOf(
        BottomBarItemData("Artists", Icons.Rounded.Person, Screens.Artists.route),
        BottomBarItemData("Tracks", Icons.Rounded.PlayArrow, Screens.Tracks.route),
        BottomBarItemData("User", Icons.Rounded.Person, Screens.User.route)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(BottomBarHeight)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Black,
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = ViewPadding),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            if (navIndex.intValue != index) {
                                navIndex.intValue = index
                                onNavigate(item.route)
                            }
                        }
                ) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (navIndex.intValue == index) Color.White else Color.Gray
                    )
                    Text(
                        text = item.label,
                        color = if (navIndex.intValue == index) Color.White else Color.Gray
                    )
                }
            }
        }
    }
}

private data class BottomBarItemData(
    val label: String,
    val icon: ImageVector,
    val route: String,
)

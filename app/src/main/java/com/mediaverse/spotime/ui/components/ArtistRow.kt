package com.mediaverse.spotime.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mediaverse.spotime.model.ArtistData

@Composable
fun ArtistRow(index: Int, artist: ArtistData) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "${index + 1}",
            modifier = Modifier.width(30.dp),
            style = MaterialTheme.typography.bodyLarge
        )
        AsyncImage(
            model = artist.images.firstOrNull()?.url,
            contentDescription = artist.name,
            modifier = Modifier
                .size(50.dp)
                .padding(end = 12.dp)
        )
        Text(
            text = artist.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

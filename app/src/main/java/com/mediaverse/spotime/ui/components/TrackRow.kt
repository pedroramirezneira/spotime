package com.mediaverse.spotime.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import com.mediaverse.spotime.model.TrackData

@Composable
fun TrackRow(index: Int, track: TrackData) {
    val imageUrl = track.album.images.firstOrNull()?.url.orEmpty()
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
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                .listener(
                    onStart = { },
                    onSuccess = { _, _ -> println("Image loaded successfully") },
                    onError = { _, result -> println(result) }
                )
                .build(),
            contentDescription = track.name,
            modifier = Modifier
                .size(50.dp)
                .padding(end = 12.dp)
        )
        Text(
            text = track.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

package com.mediaverse.spotime.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import coil3.compose.AsyncImage
import com.mediaverse.spotime.model.TrackData
import androidx.core.net.toUri
import com.mediaverse.spotime.authentication.Constants
import com.mediaverse.spotime.ui.theme.BorderRadius
import com.mediaverse.spotime.ui.theme.ColumnGap
import com.mediaverse.spotime.ui.theme.ViewPadding
import com.mediaverse.spotime.R

@Composable
fun TrackDetailsScreen(track: TrackData) {
    val artistNames = track.artists.joinToString(", ") { it.name }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = ViewPadding),
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = track.album.images.firstOrNull()?.url,
            contentDescription = track.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(BorderRadius))
        )
        Spacer(Modifier.height(ColumnGap))
        Text(artistNames, style = MaterialTheme.typography.bodyLarge)
        Text("Album: ${track.album.name}", style = MaterialTheme.typography.bodyLarge)
        Text(
            "Duration: ${track.duration_ms / 1000}s",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(ColumnGap))

        Button(
            onClick = {
                val spotifyUrl = "${Constants.TRACK_ENDPOINT}/${track.id}"
                val intent = Intent(Intent.ACTION_VIEW, spotifyUrl.toUri())
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.listen_on_spotify))
        }
    }
}

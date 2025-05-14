package com.mediaverse.spotime.ui.screens.artists

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
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import com.mediaverse.spotime.R
import com.mediaverse.spotime.authentication.Constants
import com.mediaverse.spotime.model.ArtistData
import com.mediaverse.spotime.ui.theme.BorderRadius
import com.mediaverse.spotime.ui.theme.ColumnGap
import com.mediaverse.spotime.ui.theme.ViewPadding

@Composable
fun ArtistDetailsScreen(artist: ArtistData) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = ViewPadding),
        verticalArrangement = Arrangement.Center
    ) {
        AsyncImage(
            model = artist.images.firstOrNull()?.url,
            contentDescription = artist.name,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(BorderRadius))
        )
        Spacer(Modifier.height(ColumnGap))
        Text(
            "Genres: ${
                artist.genres.joinToString(", ")
            }", style = MaterialTheme.typography.bodyLarge
        )
        Text(
            "Popularity: ${artist.popularity}",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(ColumnGap))

        Button(
            onClick = {
                val spotifyUrl = "${Constants.ARTIST_ENDPOINT}/${artist.id}"
                val intent = Intent(Intent.ACTION_VIEW, spotifyUrl.toUri())
                context.startActivity(intent)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.listen_on_spotify))
        }
    }
}

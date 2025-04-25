package com.mediaverse.spotime.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.mediaverse.spotime.model.ArtistData
import com.mediaverse.spotime.ui.theme.BorderRadius
import com.mediaverse.spotime.ui.theme.ImageSize
import com.mediaverse.spotime.ui.theme.ListPadding
import com.mediaverse.spotime.ui.theme.NumberWidth
import com.mediaverse.spotime.ui.theme.RowGap
import com.mediaverse.spotime.ui.theme.ViewPadding

@Composable
fun ArtistRow(
    index: Int,
    artist: ArtistData,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = ListPadding)
            .padding(horizontal = ViewPadding)
    ) {
        Text(
            text = "${index + 1}",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.width(NumberWidth)
        )
        AsyncImage(
            model = remember { artist.images.firstOrNull()?.url },
            contentDescription = artist.name,
            modifier = Modifier
                .size(ImageSize)
                .clip(RoundedCornerShape(BorderRadius))
        )
        Spacer(Modifier.width(RowGap))
        Text(
            text = artist.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
    }
}

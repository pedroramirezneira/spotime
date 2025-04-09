package com.mediaverse.spotime.album.model

import com.mediaverse.spotime.model.ImageData
import com.mediaverse.spotime.artists.model.SimplifiedArtistData

@Suppress("PropertyName")
data class Album(
    val album_type: String,
    val total_tracks: Int,
    val available_markets: List<String>,
    val images: List<ImageData>,
    val name: String,
    val release_date: String,
    val artists: List<SimplifiedArtistData>,
)

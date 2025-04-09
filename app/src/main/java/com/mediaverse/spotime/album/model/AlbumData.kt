package com.mediaverse.spotime.album.model

import com.mediaverse.spotime.artists.model.SimplifiedArtistData
import com.mediaverse.spotime.model.ImageData

@Suppress("PropertyName")
data class AlbumData(
    val album_type: String,
    val total_tracks: Int,
    val available_markets: List<String>,
    val images: List<ImageData>,
    val name: String,
    val release_date: String,
    val artists: List<SimplifiedArtistData>,
)

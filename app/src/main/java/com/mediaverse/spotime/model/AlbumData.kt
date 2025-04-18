package com.mediaverse.spotime.model

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

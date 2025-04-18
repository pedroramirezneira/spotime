package com.mediaverse.spotime.tracks.model

import com.mediaverse.spotime.model.AlbumData
import com.mediaverse.spotime.model.SimplifiedArtistData

@Suppress("PropertyName")
data class TrackData(
    val album: AlbumData,
    val artists: List<SimplifiedArtistData>,
    val available_markets: List<String>,
    val disc_number: Int,
    val duration_ms: Int,
    val explicit: Boolean,
    val id: String,
    val name: String,
    val popularity: Int,
    val track_number: Int,
)

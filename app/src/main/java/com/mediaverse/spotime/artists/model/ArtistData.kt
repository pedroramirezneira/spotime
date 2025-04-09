package com.mediaverse.spotime.artists.model

import com.mediaverse.spotime.model.ImageData

data class ArtistData(
    val genres: List<String>,
    val id: String,
    val images: List<ImageData>,
    val name: String,
    val popularity: Int,
)

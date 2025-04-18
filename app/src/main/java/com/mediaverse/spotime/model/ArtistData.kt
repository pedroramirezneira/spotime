package com.mediaverse.spotime.model

data class ArtistData(
    val genres: List<String>,
    val id: String,
    val images: List<ImageData>,
    val name: String,
    val popularity: Int,
)

package com.mediaverse.spotime.config

import com.mediaverse.spotime.model.AlbumData
import com.mediaverse.spotime.model.ImageData
import com.mediaverse.spotime.model.SimplifiedArtistData

val albumsMock =
    listOf(
        AlbumData(
            album_type = "album",
            total_tracks = 15,
            available_markets = listOf("AR"),
            images =
                listOf(
                    ImageData(
                        url = "https://i.scdn.co/image/ab67616d0000b27",
                        height = 640,
                        width = 640,
                    ),
                    ImageData(
                        url = "https://i.scdn.co/image/ab67616d00001e02",
                        height = 300,
                        width = 300,
                    ),
                ),
            name = "AMERI",
            release_date = "2024-10-31",
            artists =
                listOf(
                    SimplifiedArtistData(
                        id = "1",
                        name = "Duki",
                    ),
                ),
        ),
    )

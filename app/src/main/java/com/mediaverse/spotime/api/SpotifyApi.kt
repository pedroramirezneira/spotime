package com.mediaverse.spotime.api

import com.mediaverse.spotime.model.ArtistData
import com.mediaverse.spotime.model.TrackData
import com.mediaverse.spotime.model.UserProfileData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SpotifyApi {
    @GET("me")
    suspend fun getCurrentUser(): Response<UserProfileData>

    @GET("me/top/tracks")
    suspend fun getTopTracks(
        @Query("limit") limit: Int = 10,
    ): Response<ItemsResponse<TrackData>>

    @GET("me/top/artists")
    suspend fun getTopArtists(
        @Query("limit") limit: Int = 50,
        @Query("time_range") timeRange: String = "medium_term",
    ): Response<ItemsResponse<ArtistData>>
}

data class ItemsResponse<T>(
    val items: List<T>,
)

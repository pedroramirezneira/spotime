package com.mediaverse.spotime.api

import com.mediaverse.spotime.model.UserProfileData
import retrofit2.Response
import retrofit2.http.GET

interface SpotifyApi {
    @GET("me")
    suspend fun getCurrentUser(): Response<UserProfileData>
}

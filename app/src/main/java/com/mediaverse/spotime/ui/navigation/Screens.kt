package com.mediaverse.spotime.ui.navigation

enum class Screens(
    val route: String,
) {
    Artists("artists"),
    ArtistDetails("artist_details"),
    Tracks("tracks"),
    TrackDetails("track_details"),
    ;

    fun withArgs(vararg args: String): String =
        buildString {
            append(route)
            args.forEach { arg ->
                append("/$arg")
            }
        }
}

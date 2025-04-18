package com.mediaverse.spotime.authentication

import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class SpotifyAuthenticator(
    private val tokenManager: TokenManager,
    private val onLogout: () -> Unit,
) : Authenticator {
    override fun authenticate(
        route: Route?,
        response: Response,
    ): Request? {
        onLogout()
        return null
    }
}

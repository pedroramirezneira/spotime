package com.mediaverse.spotime.authentication

import android.content.Context

class TokenManager(
    private val context: Context,
) {
    private var accessToken: String? = null

    fun saveToken(token: String) {
        accessToken = token
    }

    fun getAccessToken(): String? = accessToken

    fun clear() {
        accessToken = null
    }

    fun isLoggedIn(): Boolean = !accessToken.isNullOrEmpty()
}

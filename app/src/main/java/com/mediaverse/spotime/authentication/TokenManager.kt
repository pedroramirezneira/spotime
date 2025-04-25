package com.mediaverse.spotime.authentication

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager
    @Inject
    constructor(
        @ApplicationContext context: Context,
    ) {
        private val prefs: SharedPreferences =
            context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

        private var accessToken: String? = null

        fun saveToken(token: String) {
            accessToken = token
        }

        fun getAccessToken(): String? = accessToken

        fun clear() {
            accessToken = null
            clearCodeVerifier()
        }

        fun saveCodeVerifier(verifier: String) {
            prefs.edit { putString("code_verifier", verifier) }
        }

        fun getCodeVerifier(): String? = prefs.getString("code_verifier", null)

        fun clearCodeVerifier() {
            prefs.edit { remove("code_verifier") }
        }
    }

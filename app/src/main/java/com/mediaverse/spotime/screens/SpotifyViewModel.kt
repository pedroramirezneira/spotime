package com.mediaverse.spotime.screens

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mediaverse.spotime.api.SpotifyApi
import com.mediaverse.spotime.authentication.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import net.openid.appauth.*
import javax.inject.Inject
import androidx.core.net.toUri

@HiltViewModel
class SpotifyViewModel @Inject constructor(
    app: Application,
    private val authService: AuthorizationService,
    private val tokenManager: TokenManager,
    private val spotifyApi: SpotifyApi,
) : AndroidViewModel(app) {

    @SuppressLint("StaticFieldLeak")
    private val context = app.applicationContext

    private lateinit var codeVerifier: String

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName = _userName.asStateFlow()

    fun getAuthIntent(): Intent {
        val serviceConfig = AuthorizationServiceConfiguration(
            Constants.AUTHORIZATION_ENDPOINT.toUri(),
            Constants.TOKEN_ENDPOINT.toUri()
        )

        codeVerifier = AuthUtils.generateCodeVerifier()
        val codeChallenge = AuthUtils.generateCodeChallenge(codeVerifier)

        val request = AuthorizationRequest.Builder(
            serviceConfig,
            Constants.CLIENT_ID,
            ResponseTypeValues.CODE,
            Constants.REDIRECT_URI.toUri()
        )
            .setCodeVerifier(codeVerifier)
            .setScope(Constants.SCOPES)
            .build()

        return authService.getAuthorizationRequestIntent(request)
    }

    fun handleAuthResponse(intent: Intent) {
        val response = AuthorizationResponse.fromIntent(intent)
        val ex = AuthorizationException.fromIntent(intent)

        if (response != null) {
            val tokenRequest = response.createTokenExchangeRequest()
            authService.performTokenRequest(tokenRequest) { tokenResponse, _ ->
                tokenResponse?.accessToken?.let {
                    tokenManager.saveToken(it)
                    _isLoggedIn.value = true
                    fetchUser()
                }
            }
        }
    }

    fun fetchUser() {
        viewModelScope.launch {
            try {
                val res = spotifyApi.getCurrentUser()
                if (res.isSuccessful) {
                    _userName.value = res.body()?.display_name
                } else {
                    logout()
                }
            } catch (_: Exception) {
                logout()
            }
        }
    }

    fun logout() {
        tokenManager.clear()
        _isLoggedIn.value = false
        _userName.value = null
    }
}

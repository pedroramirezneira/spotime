package com.mediaverse.spotime.ui.screens.login

import android.app.Application
import android.content.Intent
import androidx.core.net.toUri
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

@HiltViewModel
class SpotifyViewModel @Inject constructor(
    app: Application,
    private val authService: AuthorizationService,
    private val tokenManager: TokenManager,
    private val spotifyApi: SpotifyApi,
) : AndroidViewModel(app) {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _userName = MutableStateFlow<String?>(null)
    val userName = _userName.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun getAuthIntent(): Intent {
        val serviceConfig = AuthorizationServiceConfiguration(
            Constants.AUTHORIZATION_ENDPOINT.toUri(),
            Constants.TOKEN_ENDPOINT.toUri()
        )

        val codeVerifier = AuthUtils.generateCodeVerifier()
        tokenManager.saveCodeVerifier(codeVerifier)

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
        _isLoading.value = true

        val uri = intent.data
        val code = uri?.getQueryParameter("code")

        if (code != null) {
            val serviceConfig = AuthorizationServiceConfiguration(
                Constants.AUTHORIZATION_ENDPOINT.toUri(),
                Constants.TOKEN_ENDPOINT.toUri()
            )

            val verifier = tokenManager.getCodeVerifier()
            if (verifier == null) {
                _isLoading.value = false
                return
            }

            val tokenRequest = TokenRequest.Builder(
                serviceConfig,
                Constants.CLIENT_ID
            )
                .setGrantType(GrantTypeValues.AUTHORIZATION_CODE)
                .setAuthorizationCode(code)
                .setRedirectUri(Constants.REDIRECT_URI.toUri())
                .setCodeVerifier(verifier)
                .build()

            authService.performTokenRequest(tokenRequest) { tokenResponse, exception ->
                _isLoading.value = false
                tokenResponse?.accessToken?.let {
                    tokenManager.saveToken(it)
                    tokenManager.clearCodeVerifier()
                    _isLoggedIn.value = true
                    fetchUser()
                }
            }

        } else {
            _isLoading.value = false
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
        tokenManager.clearCodeVerifier()
        _isLoggedIn.value = false
        _userName.value = null
    }
}

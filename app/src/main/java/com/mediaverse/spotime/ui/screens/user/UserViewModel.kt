package com.mediaverse.spotime.ui.screens.user

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.mediaverse.spotime.api.SpotifyApi
import com.mediaverse.spotime.authentication.Constants
import com.mediaverse.spotime.data.HistoryDataStore
import com.mediaverse.spotime.model.TrackData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "UserViewModel"

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@HiltViewModel
class UserViewModel
    @Inject
    constructor(
        @ApplicationContext val context: Context,
        private val spotifyApi: SpotifyApi,
        private val historyDataStore: HistoryDataStore,
    ) : ViewModel() {
        private val auth: FirebaseAuth = FirebaseAuth.getInstance()
        private val credentialManager = CredentialManager.create(context)

        private val _userData = MutableStateFlow(auth.currentUser)
        val userData = _userData.asStateFlow()

        private val _historyTracks = MutableStateFlow<List<TrackData>>(emptyList())
        val historyTracks = _historyTracks.asStateFlow()

        private val _firebaseReady = MutableStateFlow(false)
        val firebaseReady = _firebaseReady.asStateFlow()

        init {
            fetchListenedTracks()
            viewModelScope.launch {
                delay(0)
                val current = auth.currentUser
                _userData.emit(current)
                _firebaseReady.emit(true)
            }
        }

        private val _isLoadingTracks = MutableStateFlow(false)
        val isLoadingTracks = _isLoadingTracks.asStateFlow()

        private fun fetchListenedTracks() {
            viewModelScope.launch {
                historyDataStore.listenedTrackIds.collect { ids ->
                    _isLoadingTracks.emit(true)

                    val distinctIds = ids.distinct()

                    val tracks =
                        distinctIds
                            .map { id ->
                                async {
                                    try {
                                        val response = spotifyApi.getTrackById(id)
                                        if (response.isSuccessful) response.body()?.let { id to it } else null
                                    } catch (e: Exception) {
                                        null
                                    }
                                }
                            }.awaitAll()
                            .filterNotNull()
                            .toMap()

                    val orderedTracks = ids.mapNotNull { tracks[it] }

                    _historyTracks.emit(orderedTracks)
                    _isLoadingTracks.emit(false)
                }
            }
        }

        fun launchCredentialManager() {
            // Instantiate a Google sign-in request
            val googleIdOption =
                GetGoogleIdOption
                    .Builder()
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId((Constants.GOOGLE_SERVER_ID))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build()

            // Create the Credential Manager request
            val request =
                GetCredentialRequest
                    .Builder()
                    .addCredentialOption(googleIdOption)
                    .build()

            viewModelScope.launch {
                try {
                    // Launch Credential Manager UI
                    val result =
                        credentialManager.getCredential(
                            context = context,
                            request = request,
                        )

                    // Extract credential from the result returned by Credential Manager
                    handleSignIn(result.credential)
                } catch (e: GetCredentialException) {
                    Log.e(TAG, "Couldn't retrieve user's credentials: ${e.localizedMessage}")
                }
            }
        }

        private fun handleSignIn(credential: Credential) {
            // Check if credential is of type Google ID
            if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                // Create Google ID Token
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)

                // Sign in to Firebase with using the token
                firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
            } else {
                Log.w(TAG, "Credential is not of type Google ID!")
            }
        }

        private fun firebaseAuthWithGoogle(idToken: String) {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            auth
                .signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        viewModelScope.launch {
                            _userData.emit(user)
                        }
                    } else {
                        // If sign in fails, display a message to the user
                        viewModelScope.launch {
                            _userData.emit(null)
                        }
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                    }
                }
        }

        fun signOut() {
            // Firebase sign out
            auth.signOut()

            // When a user signs out, clear the current user credential state from all credential providers.
            viewModelScope.launch {
                try {
                    val clearRequest = ClearCredentialStateRequest()
                    credentialManager.clearCredentialState(clearRequest)
                    viewModelScope.launch {
                        _userData.emit(null)
                    }
                } catch (e: ClearCredentialException) {
                    Log.e(TAG, "Couldn't clear user credentials: ${e.localizedMessage}")
                }
            }
        }

        fun clearHistory() {
            viewModelScope.launch {
                historyDataStore.clearHistory()
                _historyTracks.emit(emptyList()) // Clear the UI as well
            }
        }
    }

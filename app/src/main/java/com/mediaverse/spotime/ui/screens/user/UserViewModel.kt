package com.mediaverse.spotime.ui.screens.user

import android.content.Context
import android.content.Intent
import android.credentials.GetCredentialException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.credentials.*
import androidx.credentials.exceptions.ClearCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.mediaverse.spotime.api.SpotifyApi
import com.mediaverse.spotime.authentication.Constants
import com.mediaverse.spotime.data.getListenedTrackIds
import com.mediaverse.spotime.model.TrackData
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

const val TAG = "UserViewModel"

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@HiltViewModel
class UserViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val spotifyApi: SpotifyApi,
) : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val credentialManager = CredentialManager.create(context)

    private val _userData = MutableStateFlow(auth.currentUser)
    val userData = _userData.asStateFlow()

    private val _historyTracks = MutableStateFlow<List<TrackData>>(emptyList())
    val historyTracks = _historyTracks.asStateFlow()

    private val _firebaseReady = MutableStateFlow(false)
    val firebaseReady = _firebaseReady.asStateFlow()

    private val _isLoadingTracks = MutableStateFlow(false)
    val isLoadingTracks = _isLoadingTracks.asStateFlow()

    init {
        viewModelScope.launch {
            val current = auth.currentUser
            _userData.emit(current)
            _firebaseReady.emit(true)
        }
    }

    fun fetchListenedTracks() {
        viewModelScope.launch {
            getListenedTrackIds(context).collect { ids ->
                _isLoadingTracks.emit(true)

                val distinctIds = ids.distinct()
                val tracks = distinctIds.map { id ->
                    async {
                        try {
                            val response = spotifyApi.getTrackById(id)
                            if (response.isSuccessful) response.body()?.let { id to it } else null
                        } catch (_: Exception) {
                            null
                        }
                    }
                }.awaitAll().filterNotNull().toMap()

                val orderedTracks = ids.mapNotNull { tracks[it] }

                _historyTracks.emit(orderedTracks)
                _isLoadingTracks.emit(false)
            }
        }
    }

    fun launchCredentialManager(activityContext: Context) {
        if (Build.MANUFACTURER.equals("Samsung", ignoreCase = true)) {
            Log.w(TAG, "Samsung device detected – using legacy Google Sign-In")
            launchLegacyGoogleSignIn(activityContext)
            return
        }

        Log.d(TAG, "Launching Credential Manager...")

        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(Constants.GOOGLE_SERVER_ID)
            .setFilterByAuthorizedAccounts(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewModelScope.launch {
            try {
                Log.d(TAG, "Calling credentialManager.getCredential...")
                val result = credentialManager.getCredential(
                    context = activityContext,
                    request = request,
                )

                Log.d(TAG, "Credential received: ${result.credential.data}")
                handleSignIn(result.credential)
            } catch (e: GetCredentialException) {
                Log.e(TAG, "CredentialManager failed: ${e.localizedMessage}")
                Log.w(TAG, "Falling back to legacy Google Sign-In...")
                launchLegacyGoogleSignIn(activityContext)
            }
        }
    }

    private fun launchLegacyGoogleSignIn(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Constants.GOOGLE_SERVER_ID)
            .requestEmail()
            .build()

        val signInClient = GoogleSignIn.getClient(context, gso)
        val signInIntent = signInClient.signInIntent
        // 👇 IMPORTANTE: este intent debe ser lanzado desde la Activity, no desde ViewModel
        signInIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(signInIntent)
    }

    fun handleGoogleSignInIntent(data: Intent?) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.result
            val idToken = account?.idToken
            if (!idToken.isNullOrEmpty()) {
                firebaseAuthWithGoogle(idToken)
            } else {
                Log.w(TAG, "Google ID Token is null or empty")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Google Sign-In failed: ${e.localizedMessage}")
        }
    }

    private fun handleSignIn(credential: Credential) {
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
        } else {
            Log.w(TAG, "Credential is not of type Google ID!")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    viewModelScope.launch {
                        _userData.emit(user)
                    }
                } else {
                    viewModelScope.launch {
                        _userData.emit(null)
                    }
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                }
            }
    }

    fun signOut() {
        auth.signOut()
        viewModelScope.launch {
            try {
                val clearRequest = ClearCredentialStateRequest()
                credentialManager.clearCredentialState(clearRequest)
                _userData.emit(null)
            } catch (e: ClearCredentialException) {
                Log.e(TAG, "Couldn't clear user credentials: ${e.localizedMessage}")
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            com.mediaverse.spotime.data.clearHistory(context)
            _historyTracks.emit(emptyList())
        }
    }

    fun getLegacyGoogleSignInIntent(context: Context): Intent {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Constants.GOOGLE_SERVER_ID)
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(context, gso).signInIntent
    }
}

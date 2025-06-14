package com.mediaverse.spotime.ui.screens.tracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediaverse.spotime.api.SpotifyApi
import com.mediaverse.spotime.model.TrackData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TracksViewModel
    @Inject
    constructor(
        private val spotifyApi: SpotifyApi,
    ) : ViewModel() {
        private val _tracks = MutableStateFlow<List<TrackData>>(emptyList())
        val tracks = _tracks.asStateFlow()
        private val _isLoading = MutableStateFlow(true)
        val isLoading = _isLoading.asStateFlow()

        fun fetchTopTracks() {
            viewModelScope.launch {
                _isLoading.value = true
                val response = spotifyApi.getTopTracks(limit = 50)
                if (response.isSuccessful) {
                    _tracks.value = response.body()?.items ?: emptyList()
                }
                _isLoading.value = false
            }
        }
    }

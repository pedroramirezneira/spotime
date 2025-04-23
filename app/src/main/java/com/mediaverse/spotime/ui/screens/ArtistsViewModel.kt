package com.mediaverse.spotime.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mediaverse.spotime.api.SpotifyApi
import com.mediaverse.spotime.model.ArtistData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistsViewModel
    @Inject
    constructor(
        private val spotifyApi: SpotifyApi,
    ) : ViewModel() {
        private val _artists = MutableStateFlow<List<ArtistData>>(emptyList())
        val artists = _artists.asStateFlow()

        private val _isLoading = MutableStateFlow(true)
        val isLoading = _isLoading.asStateFlow()

        fun fetchTopArtists() {
            viewModelScope.launch {
                _isLoading.value = true
                val response = spotifyApi.getTopArtists()
                if (response.isSuccessful) {
                    _artists.value = response.body()?.items ?: emptyList()
                }
                _isLoading.value = false
            }
        }
    }

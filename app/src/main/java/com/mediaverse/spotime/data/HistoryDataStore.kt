package com.mediaverse.spotime.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "history_prefs")

@Singleton
class HistoryDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val HISTORY_KEY = stringPreferencesKey("listened_track_history") // Not a set
    }

    // Flow of track IDs in order and with duplicates
    val listenedTrackIds: Flow<List<String>> = context.dataStore.data.map { preferences ->
        preferences[HISTORY_KEY]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    }

    suspend fun addTrackId(id: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[HISTORY_KEY]?.split(",")?.toMutableList() ?: mutableListOf()
            current.add(id)
            preferences[HISTORY_KEY] = current.joinToString(",")
        }
    }

    suspend fun clearHistory() {
        context.dataStore.edit { preferences ->
            preferences[HISTORY_KEY] = ""
        }
    }
}

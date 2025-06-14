package com.mediaverse.spotime.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "history_prefs")

        private val HISTORY_KEY = stringPreferencesKey("listened_track_history") // Not a set

fun getListenedTrackIds(context: Context): Flow<List<String>> {
    return context.dataStore.data.map { preferences ->
        preferences[HISTORY_KEY]?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
    }
}

    suspend fun addTrackId(context: Context, id: String) {
        context.dataStore.edit { preferences ->
            val current = preferences[HISTORY_KEY]?.split(",")?.toMutableList() ?: mutableListOf()
            current.add(id)
            preferences[HISTORY_KEY] = current.joinToString(",")
        }
    }

    suspend fun clearHistory(context: Context) {
        context.dataStore.edit { preferences ->
            preferences[HISTORY_KEY] = ""
        }
    }

package com.femi.a2fa

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.femi.a2fa.utils.USER_PREFERENCES
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = USER_PREFERENCES)

class UserPreferences(val context: Context) {

    private object PreferencesKeys {
        val KEY_FIRST_TIME_USER = booleanPreferencesKey("KEY_FIRST_TIME_USER")
    }

    val firstTimeUser: Flow<Boolean>
        get() = context.dataStore.data.map { preference ->
            preference[PreferencesKeys.KEY_FIRST_TIME_USER] ?: true
        }

    suspend fun firstTimeUser(status: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.KEY_FIRST_TIME_USER] = status
        }
    }

}
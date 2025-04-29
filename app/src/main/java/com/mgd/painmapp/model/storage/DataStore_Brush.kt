package com.mgd.painmapp.model.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "brush_prefs")

object ColorPrefsKeys {
    val COLOR_INDEX = intPreferencesKey("color_index")
}

suspend fun Context.saveColorIndex(index: Int) {
    dataStore.edit { prefs ->
        prefs[ColorPrefsKeys.COLOR_INDEX] = index
    }
}
suspend fun Context.getColorIndex(): Int {
    val prefs = dataStore.data.first()
    return prefs[ColorPrefsKeys.COLOR_INDEX] ?: 0
}

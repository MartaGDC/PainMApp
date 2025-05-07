package com.mgd.painmapp.model.storage

import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.mgd.painmapp.R
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "brush_prefs")

object ColorBrush {
    private val COLOR_INDEX_KEY = intPreferencesKey("color_index")

    val colorList = mutableListOf<Int>()

    fun initialize(context: Context) {
        Log.d("ColorBrush", "Initializing color list")
        colorList.clear()
        colorList.addAll(
            listOf(
                ContextCompat.getColor(context, R.color.dark_blue),
                ContextCompat.getColor(context, R.color.blue),
                ContextCompat.getColor(context, R.color.red),
                ContextCompat.getColor(context, R.color.pink),
                ContextCompat.getColor(context, R.color.yellow)
            )
        )
    }

    suspend fun Context.saveColorIndex(index: Int) {
        dataStore.edit { prefs ->
            prefs[COLOR_INDEX_KEY] = index
        }
    }

    suspend fun Context.getColorIndex(): Int {
        val prefs = dataStore.data.first()
        return prefs[COLOR_INDEX_KEY] ?: 0
    }
}
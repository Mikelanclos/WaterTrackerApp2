package com.bignerdranch.android.watertrackerapp

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("water_tracker_prefs")

class DataStoreManager(private val context: Context) {
    companion object {
        private val TOTAL_OUNCES_KEY = intPreferencesKey("total_ounces")
        private val DAILY_GOAL_KEY = intPreferencesKey("daily_goal")
        private val LAST_UPDATED_KEY = longPreferencesKey("last_updated")
    }

    val totalOuncesFlow: Flow<Int> = context.dataStore.data
        .map { it[TOTAL_OUNCES_KEY] ?: 0 }

    val dailyGoalFlow: Flow<Int> = context.dataStore.data
        .map { it[DAILY_GOAL_KEY] ?: 64 }

    val lastUpdatedFlow: Flow<Long> = context.dataStore.data
        .map { it[LAST_UPDATED_KEY] ?: 0L }

    suspend fun saveTotalOunces(value: Int) {
        context.dataStore.edit {
            it[TOTAL_OUNCES_KEY] = value
            it[LAST_UPDATED_KEY] = System.currentTimeMillis()
        }
    }

    suspend fun saveDailyGoal(value: Int) {
        context.dataStore.edit {
            it[DAILY_GOAL_KEY] = value
            it[LAST_UPDATED_KEY] = System.currentTimeMillis()
        }
    }
}

// app/src/main/java/com/example/baosapp/data/local/UserPreferencesDataStore.kt
package com.example.baosapp.data.local

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

// Sólo UNA única extensión para 'user_prefs'
val Context.userPrefsDataStore by preferencesDataStore(name = "user_prefs")

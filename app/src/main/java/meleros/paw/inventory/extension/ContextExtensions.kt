package meleros.paw.inventory.extension

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore by preferencesDataStore("inventory_preferences")
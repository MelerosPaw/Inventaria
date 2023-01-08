package meleros.paw.inventory.extension

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey

val ITEM_LIST_SORTING: Preferences.Key<Int> = intPreferencesKey("ITEM_LIST_SORTING")
val ITEM_LIST_LAYOUT: Preferences.Key<Int> = intPreferencesKey("ITEM_LIST_LAYOUT")
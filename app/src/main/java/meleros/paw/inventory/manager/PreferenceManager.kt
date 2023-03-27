package meleros.paw.inventory.manager

import android.app.Application
import android.widget.Toast
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import meleros.paw.inventory.data.ItemListLayout
import meleros.paw.inventory.data.ItemListSorting
import meleros.paw.inventory.extension.ITEM_LIST_LAYOUT
import meleros.paw.inventory.extension.ITEM_LIST_SORTING
import meleros.paw.inventory.extension.dataStore
import java.io.IOException
import java.util.LinkedList

class PreferenceManager(private val app: Application) {

  private val dataStore: DataStore<Preferences> = app.applicationContext.dataStore
  var preferences: Preferences? = null
  private var onPreferencesChangedListeners: LinkedList<(preferences: Preferences) -> Unit> = LinkedList()

  init {
    CoroutineScope(Dispatchers.Default).launch {
      dataStore.data
        .catch { onErrorWhileReadingPreferences(it) }
        .collect { preferences ->
          this@PreferenceManager.preferences = preferences
          onPreferencesChanged(preferences)
        }
    }
  }

  fun setOnPreferencesChanged(onChanged: (preferences: Preferences) -> Unit) {
    onPreferencesChangedListeners.add(onChanged)
    preferences?.let(onChanged)
  }

  suspend fun onPreferencesChanged(preferences: Preferences) {
    withContext(Dispatchers.Main) {
      onPreferencesChangedListeners.forEach { it.invoke(preferences) }
    }
  }

  fun getLayoutType(preferences: Preferences): ItemListLayout {
    val layout = preferences[ITEM_LIST_LAYOUT] ?: 0
    return ItemListLayout.values()[layout]
  }

  fun getSortingType(preferences: Preferences): ItemListSorting {
    val sortingType = preferences[ITEM_LIST_SORTING] ?: 0
    return ItemListSorting.values()[sortingType]
  }

  suspend fun changeItemListLayout(layout: ItemListLayout) {
    dataStore.edit {
      if (getLayoutType(it) != layout) {
        it[ITEM_LIST_LAYOUT] = layout.ordinal
      }
    }
  }

  suspend fun changeItemListSorting(provideSortingType: (oldSorting: ItemListSorting) -> ItemListSorting) {
    dataStore.edit {
      val currentSorting = getSortingType(it)
      val newSorting = provideSortingType(currentSorting)
      it[ITEM_LIST_SORTING] = newSorting.ordinal
    }
  }

  // TODO Melero 26/1/23: Esto habrá que quitarlo en producción
  private fun onErrorWhileReadingPreferences(it: Throwable) {
    if (it is IOException) {
      Toast.makeText(app, "Error al leer preferencias", Toast.LENGTH_SHORT).show()
    } else {
      throw it
    }
  }
}
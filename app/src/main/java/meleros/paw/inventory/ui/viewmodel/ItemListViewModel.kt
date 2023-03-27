package meleros.paw.inventory.ui.viewmodel

import android.app.Application
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import meleros.paw.inventory.bo.Item
import meleros.paw.inventory.data.ItemListLayout
import meleros.paw.inventory.data.ItemListSorting
import meleros.paw.inventory.data.ItemListSorting.ALPHABETICALLY
import meleros.paw.inventory.data.ItemListSorting.ALPHABETICALLY_REVERSED
import meleros.paw.inventory.data.ItemListSorting.GREATER_QUANTITY
import meleros.paw.inventory.data.ItemListSorting.LESS_QUANTITY
import meleros.paw.inventory.data.ItemListSorting.MOST_RECENT_FIRST
import meleros.paw.inventory.data.ItemListSorting.OLDEST_FIRST
import meleros.paw.inventory.data.mapper.toBo
import meleros.paw.inventory.data.mapper.toVo
import meleros.paw.inventory.data.repository.InventariaItemRepository
import meleros.paw.inventory.data.usecase.UCGetItems
import meleros.paw.inventory.extension.isTrue
import meleros.paw.inventory.manager.ImageManager
import meleros.paw.inventory.manager.PreferenceManager
import meleros.paw.inventory.ui.vo.ItemVO

class ItemListViewModel(app: Application): BaseViewModel(app) {

  //region Fields
  // region LiveData backing fields
  private val _itemListLiveData: MutableLiveData<ItemListUpdate> = MutableLiveData()
  // endregion

  // Would be injections
  private val preferenceManager: PreferenceManager = PreferenceManager(app)

  // Exposed LiveData
  val itemListLiveData: LiveData<ItemListUpdate>
    get() = _itemListLiveData

  //region Properties
  /**
   * Item list. Esta lista se le pasa al adapter, quien la usa directamente. Gracias a esto, Cuando se cambia el orden,
   * si está el modo selección habilitado, los items seleccionados se mantienen porque el adapter modifica esta misma
   * lista de forma referencial.
   */
  var currentVOs: List<ItemVO>? = null
  /** The current list sorting so when layout is changed, no sorting is applied if it's already applied. */
  var currentSorting: ItemListSorting? = null
  var isInSelectionMode = false
    private set
  var isMenuOpen: Boolean = false
  //endregion
  //endregion

  init {
    // Escucha las preferencias y las guarda para cuando cambie la lista, poder volver a aplicarlas sin que tengan que
    // cambiar. Tal vez debería guardar los valores que nos interesan en lugar de las preferencias enteras.
    preferenceManager.setOnPreferencesChanged { preferences ->
      setLoading(true, "Cargando artículos")
      doWork {
        getItemsFlow(preferences, app)
          .collect {
            val layout = preferenceManager.getLayoutType(preferences)
            val update = ItemListUpdate(layout, it, it)
            updateItemList(update)
          }
      }
    }
  }

  private suspend fun getItemsFlow(preferences: Preferences, app: Application): Flow<List<ItemVO>> =
    withContext(Dispatchers.Default) {
      val sorting = preferenceManager.getSortingType(preferences)
      UCGetItems(database, InventariaItemRepository)(app, sorting)
    }

  private suspend fun updateItemList(update: ItemListUpdate) {
    withContext(Dispatchers.Main) {
      _itemListLiveData.value = update
      setLoading(false)
    }
  }

  //region Public methods
  /**
   * Carga y cachea la lista para poder pasarla cuando se produzca una modificación de alguna de las preferencias no
   * haya que volver a solicitarla ni depender de la lista que hay en el adapter.
   */
//  fun loadItems() {
//    doWork(true, "Cargando la lista") {
//      UCGetItems(database)()
//        .mapToVO()
//        .collect() { vos ->
//          setLoading(true, "Actualizando lista")
//          currentVOs = vos
//          updateItemList(preferences, vos, true)
//        }
//    }
//  }

  fun setSelectionModeEnabled(enabled: Boolean) {
    isInSelectionMode = enabled

    if (!enabled) {
      deselectAllItems()
    }
  }

  fun getSelectedItemCount() : Int? = currentVOs?.count { it.isSelected }

  fun selectAllItems() {
    currentVOs?.forEach { it.isSelected = true }
  }

  fun deselectAllItems() {
    currentVOs?.forEach { it.isSelected = false }
  }

  fun sortItemsByQuantity() {
    changeSorting { currentSorting -> GREATER_QUANTITY.takeIf { currentSorting == LESS_QUANTITY } ?: LESS_QUANTITY }
  }

  fun sortItemsAlphabetically() {
    changeSorting { currentSorting -> ALPHABETICALLY_REVERSED.takeIf { currentSorting == ALPHABETICALLY } ?: ALPHABETICALLY }
  }

  fun sortItemsByCreationDate() {
    changeSorting { currentSorting -> OLDEST_FIRST.takeIf { currentSorting == MOST_RECENT_FIRST } ?: MOST_RECENT_FIRST }
  }

  fun layOutItemsAsGrid2() {
    changeItemListLayout(ItemListLayout.GRID_2)
  }

  fun layOutItemsAsGrid4() {
    changeItemListLayout(ItemListLayout.GRID_4)
  }

  fun layOutItemsAsList() {
    changeItemListLayout(ItemListLayout.LIST)
  }
  //endregion

  //region Private methods
  private fun changeItemListLayout(layout: ItemListLayout) {
    doWork(true, "Reorganizando lista") {
      preferenceManager.changeItemListLayout(layout)
    }
  }

  fun isAnyItemsSelected(): Boolean = currentVOs?.any { it.isSelected }.isTrue()

  fun getSelectedItems(): List<Item> =
    currentVOs?.mapNotNull { vo -> vo.takeIf { vo.isSelected }?.toBo() }.orEmpty()

  private fun Flow<List<Item>>.mapToVO(): Flow<List<ItemVO>> = map { list ->
    list.map { item ->
      val imageUri = item.image?.let { ImageManager.getUriFromString(it, getApplication()) }
      item.toVo(imageUri)
    }
  }

  private fun changeSorting(provideSortingType: (oldSorting: ItemListSorting) -> ItemListSorting) {
    doWork(true, "Reordenando lista") {
      preferenceManager.changeItemListSorting(provideSortingType)
    }
  }
  //endregion

  class ItemListUpdate(
    /** The list's layout */
    val layout: ItemListLayout,
    /** The list's items to be set. When no changes in item list have occurred, this list will be null, it will be null. */
    val newItems: List<ItemVO>?,
    /** The current list of items on display before the change happens. */
    val currentItems: List<ItemVO>?,
  )

  class QuantityComparator(private val descending: Boolean) : Comparator<ItemVO> {
    override fun compare(o1: ItemVO?, o2: ItemVO?): Int {
      val quantity1 = o1?.quantity?.toIntOrNull()
      val quantity2 = o2?.quantity?.toIntOrNull()

      return when {
        quantity1 == null && quantity2 == null -> 0
        quantity1 == null -> (-1).takeIf { !descending } ?: 1
        quantity2 == null -> 1.takeIf { !descending } ?: -1
        else -> quantity1.compareTo(quantity2).takeIf { it != 0 }
          ?: run {
            if (!descending) {
              o1.name.compareTo(o2.name)
            } else {
              o1.name.compareTo(o1.name)
            }
          }
      }
    }
  }

  private fun processItems(
    preferences: Preferences,
    preferenceManager: PreferenceManager,
    items: List<ItemVO>,
    forceUpdate: Boolean = false,
  ): ItemListUpdate {
    // Obtiene la disposición de la lista
    val layout = preferenceManager.getLayoutType(preferences)

    // Ordena la lista (solo si ha cambiado)
    val sorting = preferenceManager.getSortingType(preferences)
    val sortedList = applySorting(sorting, items, forceUpdate)

    return ItemListUpdate(layout, sortedList, items)
  }


  /** If the sorting type has changed, returns the item list sorted and stores it in cache. Else, returns null. */
  private fun applySorting(
    sorting: ItemListSorting,
    items: List<ItemVO>,
    forceUpdate: Boolean = false,
  ): List<ItemVO>? = items
    .takeIf { forceUpdate || currentSorting != sorting }
    ?.let { vos ->
      currentSorting = sorting

      when (sorting) {
        ALPHABETICALLY -> vos.sortedBy { it.name }
        ALPHABETICALLY_REVERSED -> vos.sortedByDescending { it.name }
        LESS_QUANTITY -> vos.sortedWith(QuantityComparator(false))
        GREATER_QUANTITY -> vos.sortedWith(QuantityComparator(true))
        MOST_RECENT_FIRST -> vos.sortedByDescending { it.creationDate }
        OLDEST_FIRST -> vos.sortedBy { it.creationDate }
      }.also { currentVOs = it }
    }
}

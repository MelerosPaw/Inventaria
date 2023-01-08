package meleros.paw.inventory.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import meleros.paw.inventory.InventoryApp
import meleros.paw.inventory.bo.Item
import meleros.paw.inventory.data.ItemListLayout
import meleros.paw.inventory.data.ItemListSorting
import meleros.paw.inventory.data.ItemListSorting.*
import meleros.paw.inventory.data.PicturesTakenFileProvider
import meleros.paw.inventory.data.db.InventoryDB
import meleros.paw.inventory.data.mapper.toVo
import meleros.paw.inventory.extension.ITEM_LIST_LAYOUT
import meleros.paw.inventory.extension.ITEM_LIST_SORTING
import meleros.paw.inventory.extension.dataStore
import meleros.paw.inventory.ui.Event
import meleros.paw.inventory.ui.viewmodel.usecase.UCCreateItems
import meleros.paw.inventory.ui.viewmodel.usecase.UCDeleteItems
import meleros.paw.inventory.ui.viewmodel.usecase.UCGetItem
import meleros.paw.inventory.ui.viewmodel.usecase.UCGetItems
import meleros.paw.inventory.ui.vo.ItemVO
import meleros.paw.inventory.ui.widget.FramedPhotoViewerView
import java.io.IOException

class ItemsViewModel(app: Application): AndroidViewModel(app) {

  // LiveData backing fields
  private val _itemListLiveData: MutableLiveData<ItemListUpdate> = MutableLiveData()
  private val _itemCreatedLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
  private val _itemDetailLiveData: MutableLiveData<Event<Item>> = MutableLiveData()
  private val _wipLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()

  // Would be injections
  private val database: InventoryDB by lazy { getApplication<InventoryApp>().database }
  private val dataStore = app.applicationContext.dataStore

  // Exposed LiveData
  val itemLiveData: LiveData<ItemListUpdate>
    get() = _itemListLiveData
  val itemCreatedLiveData: LiveData<Event<Boolean>>
    get() = _itemCreatedLiveData
  val wipLiveData: LiveData<Event<Boolean>>
    get() = _wipLiveData
  val itemDetailLiveData: LiveData<Event<Item>>
    get() = _itemDetailLiveData

  /**
   * Item list. Esta lista se le pasa al adapter, quien la usa directamente. Gracias a esto, Cuando se cambia el orden,
   * si está el modo selección habilitado, los items seleccionados se mantienen porque el adapter modifica esta misma
   * lista de forma referencial.
   */
  var currentVOs: List<ItemVO>? = null
  /** When selecting a picture, the path to the picture will be saved here. */
  var itemPicturePath: String? = null
  /** Item displayed in DetailItemFragment. */
  var itemDisplayedOnDetail: Item? = null
  /** The current list sorting so when layout is changed, no sorting is applied if it's already applied. */
  var currentSorting: ItemListSorting? = null
  var isInSelectionMode = false
    private set
  var isMenuOpen: Boolean = false

  fun loadItems() {
    doWork {
      UCGetItems(database)()
        .mapToVO()
        .let {
          // Guarda la lista para poder pasarla cuando se produzca una modificación de alguna de las preferencias pero
          // no de los items
          currentVOs = it
          setUpItemList()
        }
    }
  }

  fun loadItem(creationDate: Long) {
    doWork {
      UCGetItem(database)(creationDate)?.let {
        itemDisplayedOnDetail = it
        _itemDetailLiveData.value = Event(it)
      }
    }
  }

  fun createItem(name: CharSequence, description: CharSequence, quantity: Int, imagePath: CharSequence?) {
    doWork {
      val creationTime = System.currentTimeMillis()
      val item = Item(name.toString(), description.toString(), quantity, creationTime, imagePath?.toString())
      val created = UCCreateItems(database)(item)
      _itemCreatedLiveData.value = Event(created)
    }
  }

  fun createValidUri(imagePath: CharSequence): Uri? =
    PicturesTakenFileProvider.getUriForPicture(imagePath, getApplication())

  fun getPictureBitmap(imagePath: CharSequence) =
    PicturesTakenFileProvider.getBitmapFromUri(getApplication(), imagePath)

  fun getPictureOrigin(imagePath: CharSequence): FramedPhotoViewerView.Origin =
    if (PicturesTakenFileProvider.isFromCamera(imagePath)) {
      FramedPhotoViewerView.Origin.CAMERA
    } else {
      FramedPhotoViewerView.Origin.FILE_SYSTEM
    }

  fun setSelectionModeEnabled(enabled: Boolean) {
    isInSelectionMode = enabled

    if (!enabled) {
      deselectAllItems()
    }
  }

  fun selectAllItems() {
    currentVOs?.forEach { it.isSelected = true }
  }

  fun deselectAllItems() {
    currentVOs?.forEach { it.isSelected = false }
  }

  fun deleteSelectedItems() {
    doWork {
      currentVOs?.let { vos ->
        val itemsToRemove = getSelectedItemIds()
        UCDeleteItems(database)(itemsToRemove)
        val finalItems = vos.filterNot { it.creationDate in itemsToRemove }

        currentVOs = finalItems
        _itemListLiveData.value?.layout?.let { layout ->
          _itemListLiveData.value = ItemListUpdate(layout, finalItems, currentVOs)
        }
      }
    }
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

  fun layOutItemsAsGrid() {
    changeItemListLayout(ItemListLayout.GRID)
  }

  fun layOutItemsAsList() {
    changeItemListLayout(ItemListLayout.LIST)
  }

  private fun changeItemListLayout(layout: ItemListLayout) {
    doWork(false) {
      dataStore.edit {
        if (getLayoutType(it) != layout) {
          it[ITEM_LIST_LAYOUT] = layout.ordinal
        }
      }
    }
  }

  private fun getSelectedItemIds(): List<Long> =
    currentVOs?.mapNotNull { vo -> vo.creationDate.takeIf { vo.isSelected } }.orEmpty()

  private fun List<Item>.mapToVO(): List<ItemVO> = map { item ->
    val imageUri = item.image?.let(::createValidUri)
    item.toVo(imageUri)
  }

  private fun changeSorting(provideSortingType: (oldSorting: ItemListSorting) -> ItemListSorting) {
    doWork(false) {
      dataStore.edit {
        val currentSorting = getSortingType(it)
        val newSorting = provideSortingType(currentSorting)
        it[ITEM_LIST_SORTING] = newSorting.ordinal
      }
    }
  }

  private fun getSortingType(preferences: Preferences): ItemListSorting {
    val sortingType = preferences[ITEM_LIST_SORTING] ?: 0
    return ItemListSorting.values()[sortingType]
  }

  /** If the sorting type has changed, returns the item list sorted and stores it in cache. Else, returns null. */
  private fun applySorting(sorting: ItemListSorting, items: List<ItemVO>): List<ItemVO>? =
    items
      .takeIf { currentSorting != sorting }
      ?.let { vos ->
        currentSorting = sorting

        when (sorting) {
          ALPHABETICALLY -> vos.sortedBy { it.name }
          ALPHABETICALLY_REVERSED -> vos.sortedByDescending { it.name }
          LESS_QUANTITY -> vos.sortedBy { it.quantity.toInt() }
          GREATER_QUANTITY -> vos.sortedByDescending { it.quantity.toInt() }
          MOST_RECENT_FIRST -> vos.sortedByDescending { it.creationDate }
          OLDEST_FIRST -> vos.sortedBy { it.creationDate }
        }.also { currentVOs = it }
      }

  private fun getLayoutType(preferences: Preferences): ItemListLayout {
    val layout = preferences[ITEM_LIST_LAYOUT] ?: 0
    return ItemListLayout.values()[layout]
  }

  private suspend fun setUpItemList() {
    dataStore.data
      .catch { onErrorWhileReadingPreferences(it) }
      .collect { currentVOs?.let { items -> postItemList(it, items) } }
  }

  private suspend fun postItemList(it: Preferences, items: List<ItemVO>) {
    _itemListLiveData.value = processItems(it, items)
  }

  private fun onErrorWhileReadingPreferences(it: Throwable) {
    if (it is IOException) {
      Toast.makeText(getApplication(), "Error al leer preferencias", Toast.LENGTH_SHORT).show()
    } else {
      throw it
    }
  }

  private suspend fun processItems(it: Preferences, items: List<ItemVO>): ItemListUpdate = withContext(Dispatchers.Default) {
    // Obtiene la disposición de la lista
    val layout = getLayoutType(it)

    // Ordena la lista (solo si ha cambiado)
    val sorting = getSortingType(it)
    val sortedList = applySorting(sorting, items)

    ItemListUpdate(layout, sortedList, currentVOs)
  }

  private fun doWork(loading: Boolean = true, block: suspend () -> Unit) {
    viewModelScope.launch {
      if (loading) {
        _wipLiveData.value = Event(true)
        block()
        _wipLiveData.value = Event(false)
      } else {
        block()
      }
    }
  }

  class ItemListUpdate(
    /** The list's layout */
    val layout: ItemListLayout,
    /**
     * The list's items to be set. When no changes in item list have occured, this list will be null, it will be
     * null.
     */
    val newItems: List<ItemVO>?,
    /** The current list of items on display before the change happens. */
    val currentItems: List<ItemVO>?,
  )
}

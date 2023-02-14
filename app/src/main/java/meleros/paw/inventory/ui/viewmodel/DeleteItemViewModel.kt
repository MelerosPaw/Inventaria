package meleros.paw.inventory.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import meleros.paw.inventory.bo.Item
import meleros.paw.inventory.data.usecase.UCDeleteItems
import meleros.paw.inventory.data.usecase.UCDeleteItemsByDetails
import meleros.paw.inventory.ui.vo.ItemVO

class DeleteItemViewModel(app: Application): BaseViewModel(app) {

  //region Fields
  // region LiveData backing fields
  private val _itemsDeletedLiveData: MutableLiveData<List<Item>> = MutableLiveData()
  private val _itemsDeletedFromContextMenuLiveData: MutableLiveData<ItemVO> = MutableLiveData()
  // endregion

  // Exposed LiveData
  val itemsDeletedLiveData: LiveData<List<Item>>
    get() = _itemsDeletedLiveData
  val itemsDeletedFromContextMenuLiveData: LiveData<ItemVO>
    get() = _itemsDeletedFromContextMenuLiveData

  fun deleteItems(items: List<Item>) {
    doWork {
      val ucDeleteItemsByDetails = UCDeleteItemsByDetails(database)
      val ucDeleteItems = UCDeleteItems(ucDeleteItemsByDetails)

      if (ucDeleteItems(items, getApplication())) {
        _itemsDeletedLiveData.value = items
      }
    }
  }

  fun deleteItem(item: ItemVO) {
    doWork {
      val ucDeleteItems = UCDeleteItemsByDetails(database)
      val itemDeleted = ucDeleteItems.invoke(item.creationDate, item.image?.path, getApplication())

      if (itemDeleted) {
        // TODO Melero 15/2/23: No hace falta subscribirse porque ya se está escuchando la base de datos con un Flow
        _itemsDeletedFromContextMenuLiveData.value = item
      }
    }
  }
}

package meleros.paw.inventory.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import meleros.paw.inventory.bo.Item
import meleros.paw.inventory.data.PicturesTakenFileProvider
import meleros.paw.inventory.data.usecase.UCDeleteItems

class DeleteItemViewModel(app: Application): BaseViewModel(app) {

  //region Fields
  // region LiveData backing fields
  private val _itemsDeletedLiveData: MutableLiveData<List<Item>> = MutableLiveData()
  // endregion

  // Exposed LiveData
  val itemsDeletedLiveData: LiveData<List<Item>>
    get() = _itemsDeletedLiveData

  fun deleteItems(items: List<Item>) {
    doWork {
      if (UCDeleteItems(database)(items, getApplication())) {
        _itemsDeletedLiveData.value = items
      }
    }
  }
}

package meleros.paw.inventory.ui.itemdetail

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meleros.paw.inventory.bo.Item
import meleros.paw.inventory.data.mapper.toVo
import meleros.paw.inventory.data.usecase.UCGetItem
import meleros.paw.inventory.data.usecase.UCGetItemDBOFlow
import meleros.paw.inventory.manager.ImageManager
import meleros.paw.inventory.ui.viewmodel.BaseViewModel
import meleros.paw.inventory.ui.vo.ItemVO

class ItemDetailViewModel(app: Application): BaseViewModel(app) {

  private val _itemDetailLiveData: MutableLiveData<ItemVO> = MutableLiveData()
  var itemDisplayedOnDetail: Item? = null
  var itemName: String? = null

  val itemDetailLiveData: LiveData<ItemVO>
    get() = _itemDetailLiveData

  fun loadItemForDetail(creationDate: Long, itemName: String) {
    this.itemName = itemName

    doWork(true, "Cargando $itemName") {
      UCGetItem(UCGetItemDBOFlow(database))(creationDate).collect { item ->
        item?.let {
          itemDisplayedOnDetail = it
          _itemDetailLiveData.value = mapToVo(it)
        }
      }
    }
  }

  private suspend fun mapToVo(it: Item) = withContext(Dispatchers.Default) {
    val uri = it.image?.let { ImageManager.getUriFromString(it, getApplication()) }
    it.toVo(uri)
  }
}
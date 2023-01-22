package meleros.paw.inventory.ui.viewmodel

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meleros.paw.inventory.bo.Item
import meleros.paw.inventory.data.mapper.toVo
import meleros.paw.inventory.data.usecase.UCGetItem
import meleros.paw.inventory.data.usecase.UCGetItemDBOFlow
import meleros.paw.inventory.ui.vo.ItemVO

class ItemDetailViewModel(app: Application): BaseViewModel(app) {

  private val _itemDetailLiveData: MutableLiveData<ItemVO> = MutableLiveData()

  val itemDetailLiveData: LiveData<ItemVO>
    get() = _itemDetailLiveData

  private var itemDisplayedOnDetail: Item? = null // TODO Melero 15/1/23: De momento, no tiene ningún uso

  fun loadItemForDetail(creationDate: Long) {
    doWork {
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
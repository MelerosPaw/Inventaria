package meleros.paw.inventory.ui.itemdetail

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import meleros.paw.inventory.data.repository.InventariaItemRepository
import meleros.paw.inventory.data.usecase.UCGetCachedItemAt
import meleros.paw.inventory.data.usecase.UCGetItemCachedPosition
import meleros.paw.inventory.data.usecase.UCGetMinimalInfoList
import meleros.paw.inventory.ui.Event
import meleros.paw.inventory.ui.viewmodel.BaseViewModel
import meleros.paw.inventory.ui.vo.DetailViewPagerInfo
import meleros.paw.inventory.ui.vo.MinimalItemInfo

class ItemDetailViewPagerViewModel(app: Application): BaseViewModel(app) {

  private val _startingPositionLiveData: MutableLiveData<Event<DetailViewPagerInfo?>> = MutableLiveData()
  val startingPositionLiveData: LiveData<Event<DetailViewPagerInfo?>>
    get() = _startingPositionLiveData

  fun loadStartingPosition(startItemCreationDate: Long) {
    doWork {
      setLoading(true, "Cargando visualizador")
      val detailViewPagerInfo: DetailViewPagerInfo? = withContext(Dispatchers.Default) {
        val positionDef = async { UCGetItemCachedPosition(InventariaItemRepository)(startItemCreationDate) }
        val minimalInfoListDef = async { UCGetMinimalInfoList(InventariaItemRepository)() }

        val position: Int? = positionDef.await()
        val minimalItemInfo: List<MinimalItemInfo> = minimalInfoListDef.await()

        if (position != null && minimalItemInfo.isNotEmpty()) {
          DetailViewPagerInfo(position, minimalItemInfo)
        } else {
          null
        }
      }
      _startingPositionLiveData.postValue(Event(detailViewPagerInfo))
    }
  }
}
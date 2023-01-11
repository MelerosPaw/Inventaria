package meleros.paw.inventory.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meleros.paw.inventory.bo.Item
import meleros.paw.inventory.data.PicturesTakenFileProvider
import meleros.paw.inventory.data.mapper.toVo
import meleros.paw.inventory.data.usecase.UCCreateItems
import meleros.paw.inventory.data.usecase.UCEditItem
import meleros.paw.inventory.data.usecase.UCGetItem
import meleros.paw.inventory.data.usecase.UCGetItemDBO
import meleros.paw.inventory.data.usecase.UCGetItemDBOFlow
import meleros.paw.inventory.ui.Event
import meleros.paw.inventory.ui.vo.ItemVO
import meleros.paw.inventory.ui.widget.FramedPhotoViewerView

class ItemDetailViewModel(app: Application): BaseViewModel(app) {

  private val _itemDetailLiveData: MutableLiveData<ItemVO> = MutableLiveData()
  private val _itemEditionFinishedLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
  private val _itemEditionLiveData: MutableLiveData<Item> = MutableLiveData()

  val itemDetailLiveData: LiveData<ItemVO>
    get() = _itemDetailLiveData
  val itemEditionFinishedLiveData: LiveData<Event<Boolean>>
    get() = _itemEditionFinishedLiveData
  val itemEditionLiveData: LiveData<Item>
    get() = _itemEditionLiveData

  /** Item displayed in DetailItemFragment. */
  var itemDisplayedOnDetail: Item? = null
  /** When selecting a picture, the path to the picture will be saved here. */
  var itemPicturePath: String? = null
  private var creationDateForEdition: Long = -1
  var isInEditionMode: Boolean = false
  var itemBeingEdited: Item? = null

  fun loadItemForDetail(creationDate: Long) {
    loadItem(creationDate) {
      _itemDetailLiveData.value = mapToVo(it)
    }
  }

  fun loadItemForEdition(creationDate: Long) {
    if (isInEditionMode) {
      loadItem(creationDate) {
        itemBeingEdited = it
        _itemEditionLiveData.value = it
      }
    }
  }

  private fun loadItem(creationDate: Long, callback: suspend (Item) -> Unit) {
    doWork {
      UCGetItem(UCGetItemDBOFlow(database))(creationDate).collect { item ->
        item?.let {
          itemDisplayedOnDetail = it
          callback(item)
        }
      }
    }
  }

  private suspend fun mapToVo(it: Item) = withContext(Dispatchers.Default) {
    val uri = it.image?.let(::getUriFromString)
    it.toVo(uri)
  }

  fun setEditionModeParams(creationDate: Long) {
    isInEditionMode = isValidCreationDate(creationDate)
    creationDateForEdition = creationDate
  }

  fun saveImageDetailUri(origin: FramedPhotoViewerView.Origin, imageUri: Uri) {
    itemPicturePath = when (origin) {
      FramedPhotoViewerView.Origin.CAMERA -> imageUri.path
      FramedPhotoViewerView.Origin.FILE_SYSTEM -> imageUri.toString()
    }
  }

  fun getUriFromString(path: CharSequence): Uri? = when (getPictureOrigin(path)) {
    FramedPhotoViewerView.Origin.CAMERA -> createValidUri(path)
    FramedPhotoViewerView.Origin.FILE_SYSTEM -> Uri.parse(path.toString())
  }

  fun createItem(name: CharSequence, description: CharSequence, quantity: Int, imagePath: CharSequence?) {
    doWork {
      val creationTime = System.currentTimeMillis()
      val item = Item(name.toString(), description.toString(), quantity, creationTime, imagePath?.toString())
      val created = UCCreateItems(database)(item)
      _itemEditionFinishedLiveData.value = Event(created)
    }
  }

  fun modifyItem(name: CharSequence, description: CharSequence, quantity: Int, imagePath: CharSequence?) {
    doWork {
      if (isInEditionMode && isValidCreationDate(creationDateForEdition)) {
        val item = Item(name.toString(), description.toString(), quantity, creationDateForEdition, imagePath?.toString())
        val created = UCEditItem(database, UCGetItemDBO(database))(item)
        _itemEditionFinishedLiveData.value = Event(created)
      }
    }
  }

  fun getPictureOrigin(imagePath: CharSequence): FramedPhotoViewerView.Origin =
    if (PicturesTakenFileProvider.isFromCamera(imagePath)) {
      FramedPhotoViewerView.Origin.CAMERA
    } else {
      FramedPhotoViewerView.Origin.FILE_SYSTEM
    }

  fun createValidUri(imagePath: CharSequence): Uri? =
    PicturesTakenFileProvider.getUriForPicture(imagePath, getApplication())

  private fun isValidCreationDate(creationDate: Long): Boolean = Item.isValidCreationDate(creationDate)
}
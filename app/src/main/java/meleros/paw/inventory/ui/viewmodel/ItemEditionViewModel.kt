package meleros.paw.inventory.ui.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import meleros.paw.inventory.manager.ImageManager
import meleros.paw.inventory.bo.Item
import meleros.paw.inventory.data.usecase.UCCreateItems
import meleros.paw.inventory.data.usecase.UCEditItem
import meleros.paw.inventory.data.usecase.UCGetItem
import meleros.paw.inventory.data.usecase.UCGetItemDBO
import meleros.paw.inventory.data.usecase.UCGetItemDBOFlow
import meleros.paw.inventory.ui.Event
import meleros.paw.inventory.ui.widget.FramedPhotoViewerView

class ItemEditionViewModel(app: Application): BaseViewModel(app) {

  private val _itemEditionFinishedLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()
  private val _itemEditionLiveData: MutableLiveData<Item> = MutableLiveData()

  val itemEditionFinishedLiveData: LiveData<Event<Boolean>>
    get() = _itemEditionFinishedLiveData
  val itemEditionLiveData: LiveData<Item>
    get() = _itemEditionLiveData

  /** When selecting a picture, the path to the picture will be saved here. */
  var itemPicturePath: String? = null
  private var creationDateForEdition: Long = -1
  var isInEditionMode: Boolean = false
  var itemBeingEdited: Item? = null

  fun loadItemForEdition(creationDate: Long) {
    doWork {
      if (isInEditionMode) {
        UCGetItem(UCGetItemDBOFlow(database))(creationDate).collect { item ->
          item?.let {
            itemBeingEdited = it
            _itemEditionLiveData.value = it
          }
        }
      }
    }
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

  fun getUriFromString(path: CharSequence): Uri? = ImageManager.getUriFromString(path, getApplication())

  private fun isValidCreationDate(creationDate: Long): Boolean = Item.isValidCreationDate(creationDate)
}
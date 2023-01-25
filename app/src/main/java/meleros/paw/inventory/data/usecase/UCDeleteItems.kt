package meleros.paw.inventory.data.usecase

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import meleros.paw.inventory.bo.Item
import meleros.paw.inventory.data.PicturesTakenFileProvider
import meleros.paw.inventory.data.db.InventoryDB
import meleros.paw.inventory.extension.orNot
import meleros.paw.inventory.ui.viewmodel.ImageManager

class UCDeleteItems(db: InventoryDB) {

  private val itemDao = db.itemDao()

  suspend operator fun invoke(items: List<Item>, context: Context): Boolean =
    withContext(Dispatchers.Default + NonCancellable) {
      items.map {
        async {
          itemDao.deleteItem(it.creationDate)
          deletePhoto(it, context)
        }
      }.awaitAll()
        .all { it }
    }

  private fun deletePhoto(item: Item, context: Context): Boolean =
    item.image == null || ImageManager.deletePicture(item.image, context)
}
package meleros.paw.inventory.data.usecase

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import meleros.paw.inventory.data.db.InventoryDB
import meleros.paw.inventory.manager.ImageManager

class UCDeleteItemsByDetails(db: InventoryDB) {

  private val itemDao = db.itemDao()

  suspend operator fun invoke(itemCreationDate: Long, itemImagePath: CharSequence?, context: Context): Boolean =
    withContext(Dispatchers.Default + NonCancellable) {
      itemDao.deleteItem(itemCreationDate)
      deletePhoto(itemImagePath, context)
    }

  private fun deletePhoto(imagePath: CharSequence?, context: Context): Boolean =
    imagePath == null || ImageManager.deletePicture(imagePath, context)
}
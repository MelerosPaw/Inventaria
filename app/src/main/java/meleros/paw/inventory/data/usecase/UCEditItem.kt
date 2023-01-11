package meleros.paw.inventory.data.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meleros.paw.inventory.bo.Item
import meleros.paw.inventory.data.db.InventoryDB
import meleros.paw.inventory.data.mapper.toDBO
import meleros.paw.inventory.extension.orNot

class UCEditItem(db: InventoryDB, private val ucGetItem: UCGetItemDBO) {

  private val itemDao = db.itemDao()

  suspend operator fun invoke(item: Item): Boolean = withContext(Dispatchers.Default) {
    ucGetItem(item.creationDate)?.let {
      val updatedItem = item.toDBO()
      updatedItem.id = it.id
      itemDao.updateItem(updatedItem) == 1
    }.orNot()
  }
}
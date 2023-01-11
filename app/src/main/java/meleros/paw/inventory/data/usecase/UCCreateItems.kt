package meleros.paw.inventory.data.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meleros.paw.inventory.bo.Item
import meleros.paw.inventory.data.db.InventoryDB
import meleros.paw.inventory.data.db.ItemDBO
import meleros.paw.inventory.data.mapper.toBo
import meleros.paw.inventory.data.mapper.toDBO

class UCCreateItems(db: InventoryDB) {

  private val itemDao = db.itemDao()

  suspend operator fun invoke(item: Item): Boolean = withContext(Dispatchers.Default) {
    itemDao.createItem(item.toDBO()) != -1L
  }
}
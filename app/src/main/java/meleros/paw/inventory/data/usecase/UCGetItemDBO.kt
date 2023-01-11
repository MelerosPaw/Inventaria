package meleros.paw.inventory.data.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meleros.paw.inventory.data.db.InventoryDB
import meleros.paw.inventory.data.db.ItemDBO

class UCGetItemDBO(db: InventoryDB) {

  private val itemDao = db.itemDao()

  suspend operator fun invoke(creationDate: Long): ItemDBO? = withContext(Dispatchers.Default) {
    itemDao.getItemByCreationDate(creationDate)
  }
}
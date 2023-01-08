package meleros.paw.inventory.ui.viewmodel.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meleros.paw.inventory.bo.Item
import meleros.paw.inventory.data.db.InventoryDB
import meleros.paw.inventory.data.db.ItemDBO
import meleros.paw.inventory.data.mapper.toBo

class UCGetItems(db: InventoryDB) {

  private val itemDao = db.itemDao()

  suspend operator fun invoke(): List<Item> = withContext(Dispatchers.Default) {
    itemDao.getAllItems().map(ItemDBO::toBo)
  }
}
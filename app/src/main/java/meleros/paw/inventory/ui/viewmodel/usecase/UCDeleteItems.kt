package meleros.paw.inventory.ui.viewmodel.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meleros.paw.inventory.data.db.InventoryDB

class UCDeleteItems(db: InventoryDB) {

  private val itemDao = db.itemDao()

  suspend operator fun invoke(listCreationDates: List<Long>) = withContext(Dispatchers.Default) {
    listCreationDates.forEach(itemDao::deleteItem)
  }
}
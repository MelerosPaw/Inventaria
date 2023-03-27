package meleros.paw.inventory.data.usecase

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import meleros.paw.inventory.data.ItemListSorting
import meleros.paw.inventory.data.db.InventoryDB
import meleros.paw.inventory.data.repository.ItemRepository
import meleros.paw.inventory.ui.vo.ItemVO

class UCGetItems(
  private val db: InventoryDB,
  private val repository: ItemRepository,
) {

  suspend operator fun invoke(
    context: Context,
    sorting: ItemListSorting,
    forceUpdateSorting: Boolean = false,
  ): Flow<List<ItemVO>> = withContext(Dispatchers.Default) {
    repository.getItems(context, db, sorting, forceUpdateSorting)
  }
}
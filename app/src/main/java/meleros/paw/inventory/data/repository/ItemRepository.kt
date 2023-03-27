package meleros.paw.inventory.data.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import meleros.paw.inventory.data.ItemListSorting
import meleros.paw.inventory.data.db.InventoryDB
import meleros.paw.inventory.ui.vo.ItemVO
import meleros.paw.inventory.ui.vo.MinimalItemInfo

interface ItemRepository {

  fun getItems(
    context: Context,
    db: InventoryDB,
    sorting: ItemListSorting,
    forceUpdateSorting: Boolean = false,
  ): Flow<List<ItemVO>>

  fun getItemAt(position: Int): ItemVO?

  fun getCachedPosition(creationDate: Long): Int?

  fun getMinimalInfo(): List<MinimalItemInfo>?
}
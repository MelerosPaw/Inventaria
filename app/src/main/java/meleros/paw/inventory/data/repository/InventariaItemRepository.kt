package meleros.paw.inventory.data.repository

import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import meleros.paw.inventory.bo.Item
import meleros.paw.inventory.data.ItemListSorting
import meleros.paw.inventory.data.db.InventoryDB
import meleros.paw.inventory.data.db.ItemDBO
import meleros.paw.inventory.data.mapper.itemCreationDateFormatter
import meleros.paw.inventory.data.mapper.toBo
import meleros.paw.inventory.data.mapper.toVo
import meleros.paw.inventory.manager.ImageManager
import meleros.paw.inventory.ui.viewmodel.ItemListViewModel
import meleros.paw.inventory.ui.vo.ItemVO
import meleros.paw.inventory.ui.vo.MinimalItemInfo
import java.util.LinkedList

object InventariaItemRepository: ItemRepository {

  /**
   * Items cacheados una vez que se hace una consulta a la base de datos. Estarán ordenados según la ordenación
   * actual.
   */
  private val sortedItems: LinkedList<ItemVO> = LinkedList()

  override fun getItems(
    context: Context,
    db: InventoryDB,
    sorting: ItemListSorting,
    forceUpdateSorting: Boolean,
  ): Flow<List<ItemVO>> =
    db.itemDao()
      .getAllItems()
      .mapToBO()
      .mapToVO(context)
      .sort(sorting)

  override fun getItemAt(position: Int): ItemVO? = sortedItems.getOrNull(position)

  override fun getCachedPosition(creationDate: Long): Int? = sortedItems.indexOfFirst {
    it.creationDate == creationDate
  }.takeIf { it != -1 }

  override fun getMinimalInfo(): List<MinimalItemInfo> = sortedItems.map {
    MinimalItemInfo(it.name, it.creationDate, itemCreationDateFormatter)
  }

  private fun Flow<List<ItemDBO>>.mapToBO(): Flow<List<Item>> = map { it.map(ItemDBO::toBo) }

  private fun Flow<List<Item>>.mapToVO(context: Context): Flow<List<ItemVO>> = map { list ->
    list.map { item ->
      val imageUri = item.image?.let { ImageManager.getUriFromString(it, context) }
      item.toVo(imageUri)
    }
  }

  private fun Flow<List<ItemVO>>.sort(sorting: ItemListSorting): Flow<List<ItemVO>> = map { vos ->
    when (sorting) {
      ItemListSorting.ALPHABETICALLY -> vos.sortedBy { it.name }
      ItemListSorting.ALPHABETICALLY_REVERSED -> vos.sortedByDescending { it.name }
      ItemListSorting.LESS_QUANTITY -> vos.sortedWith(ItemListViewModel.QuantityComparator(false))
      ItemListSorting.GREATER_QUANTITY -> vos.sortedWith(ItemListViewModel.QuantityComparator(true))
      ItemListSorting.MOST_RECENT_FIRST -> vos.sortedByDescending { it.creationDate }
      ItemListSorting.OLDEST_FIRST -> vos.sortedBy { it.creationDate }
    }.also {
      sortedItems.clear()
      sortedItems.addAll(it)
    }
  }
}
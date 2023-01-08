package meleros.paw.inventory.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemDAO {

  @Query("SELECT * FROM ITEMS")
  fun getAllItems(): List<ItemDBO>

  @Insert
  fun createItem(item: ItemDBO): Long

  @Query("SELECT * FROM ITEMS WHERE ITEMS.creationDate = :creationDate LIMIT 1")
  fun getItemByCreationDate(creationDate: Long): ItemDBO?

  @Query("DELETE FROM ITEMS WHERE ITEMS.creationDate = :creationDate")
  fun deleteItem(creationDate: Long)
}
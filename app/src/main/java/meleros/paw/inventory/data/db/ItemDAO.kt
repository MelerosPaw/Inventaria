package meleros.paw.inventory.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ItemDAO {

  @Query("SELECT * FROM ITEMS")
  fun getAllItems(): Flow<List<ItemDBO>>

  @Insert
  fun createItem(item: ItemDBO): Long

  @Query("SELECT * FROM ITEMS WHERE ITEMS.creationDate = :creationDate LIMIT 1")
  fun getItemFlowByCreationDate(creationDate: Long): Flow<ItemDBO?>

  @Query("SELECT * FROM ITEMS WHERE ITEMS.creationDate = :creationDate LIMIT 1")
  fun getItemByCreationDate(creationDate: Long): ItemDBO?

  @Query("DELETE FROM ITEMS WHERE ITEMS.creationDate = :creationDate")
  fun deleteItem(creationDate: Long)

  @Update(entity = ItemDBO::class, onConflict = OnConflictStrategy.REPLACE)
  fun updateItem(item: ItemDBO): Int
}
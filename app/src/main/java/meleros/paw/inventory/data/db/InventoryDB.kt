package meleros.paw.inventory.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

fun createDB(context: Context): InventoryDB =
  Room.databaseBuilder(context.applicationContext, InventoryDB::class.java, "InventoryDB")
    .build()

@Database(entities = [ItemDBO::class], version = 1)
abstract class InventoryDB: RoomDatabase() {

  abstract fun itemDao(): ItemDAO
}
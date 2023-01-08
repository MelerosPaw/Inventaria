package meleros.paw.inventory.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ITEMS")
class ItemDBO(
  @PrimaryKey(autoGenerate = true)
  var id: Int,
  var name: String,
  var description: String,
  var quantity: Int,
  var creationDate: Long,
  var image: String? = null,
)
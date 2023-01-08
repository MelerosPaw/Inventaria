package meleros.paw.inventory

import android.app.Application
import meleros.paw.inventory.data.db.InventoryDB
import meleros.paw.inventory.data.db.createDB

class InventoryApp : Application() {

  lateinit var database: InventoryDB

  override fun onCreate() {
    super.onCreate()
    database = createDB(this)
  }

}
package meleros.paw.inventory.data.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import meleros.paw.inventory.bo.Item
import meleros.paw.inventory.data.mapper.toBo

class UCGetItem(private val ucGetItemDBOFlow: UCGetItemDBOFlow) {

  suspend operator fun invoke(creationDate: Long): Flow<Item?> = withContext(Dispatchers.Default) {
    ucGetItemDBOFlow(creationDate).map { it?.toBo() }
  }
}
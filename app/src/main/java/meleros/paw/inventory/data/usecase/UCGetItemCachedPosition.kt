package meleros.paw.inventory.data.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meleros.paw.inventory.data.repository.InventariaItemRepository

class UCGetItemCachedPosition(private val repo: InventariaItemRepository) {

  suspend operator fun invoke(creationDate: Long): Int? = withContext(Dispatchers.Default) {
    repo.getCachedPosition(creationDate)
  }
}
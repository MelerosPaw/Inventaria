package meleros.paw.inventory.data.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meleros.paw.inventory.data.repository.InventariaItemRepository
import meleros.paw.inventory.ui.vo.ItemVO

class UCGetCachedItemAt(private val repo: InventariaItemRepository) {

  suspend operator fun invoke(position: Int): ItemVO? = withContext(Dispatchers.Default) {
    repo.getItemAt(position)
  }
}
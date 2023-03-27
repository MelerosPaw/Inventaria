package meleros.paw.inventory.data.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meleros.paw.inventory.data.repository.InventariaItemRepository
import meleros.paw.inventory.ui.vo.MinimalItemInfo

class UCGetMinimalInfoList(private val repo: InventariaItemRepository) {

  suspend operator fun invoke(): List<MinimalItemInfo> = withContext(Dispatchers.Default) {
    repo.getMinimalInfo()
  }
}
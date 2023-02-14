package meleros.paw.inventory.data.usecase

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import meleros.paw.inventory.bo.Item

class UCDeleteItems(private val ucDeleteItemsByDetails: UCDeleteItemsByDetails) {

  suspend operator fun invoke(items: List<Item>, context: Context): Boolean =
    withContext(Dispatchers.Default + NonCancellable) {
      items.map {
        async { ucDeleteItemsByDetails.invoke(it.creationDate, it.image, context) }
      }.awaitAll().all { it }
    }
}
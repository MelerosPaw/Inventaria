package meleros.paw.inventory.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import meleros.paw.inventory.InventoryApp
import meleros.paw.inventory.data.db.InventoryDB
import meleros.paw.inventory.ui.Event

open class BaseViewModel(app: Application): AndroidViewModel(app) {

  private val _wipLiveData: MutableLiveData<Event<LoadingState>> = MutableLiveData()

  val wipLiveData: LiveData<Event<LoadingState>>
    get() = _wipLiveData

  protected val database: InventoryDB by lazy { getApplication<InventoryApp>().database }

  protected fun doWork(
    loading: Boolean = true,
    message: CharSequence? = null,
    dispatcher: CoroutineDispatcher? = null,
    block: suspend () -> Unit,
  ) {
    if (loading) {
      setLoading(true, message)
    }

    viewModelScope.launch(dispatcher ?: Dispatchers.Main) {
      delay(2000L)
      block()

      if (loading) {
        withContext(Dispatchers.Main) {
          setLoading(false, null)
        }
      }
    }
  }

  fun setLoading(isLoading: Boolean, message: CharSequence? = null) {
    val state = if (isLoading) { LoadingState.Loading(message) } else { LoadingState.NotLoading()}
    val event = Event(state)
    _wipLiveData.value = event
  }

  sealed class LoadingState(val message: CharSequence? = null) {
    class Loading(message: CharSequence? = null): LoadingState(message)
    class NotLoading: LoadingState(null)
  }
}
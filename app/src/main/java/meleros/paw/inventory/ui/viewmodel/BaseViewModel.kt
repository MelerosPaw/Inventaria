package meleros.paw.inventory.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    viewModelScope.launch(dispatcher ?: Dispatchers.Main) {
      if (loading) {
        setLoading(dispatcher, true, message)
        block()
        setLoading(dispatcher, false, message)
      } else {
        block()
      }
    }
  }

  private fun setLoading(dispatcher: CoroutineDispatcher?, isLoading: Boolean, message: CharSequence?) {
    val state = if (isLoading) { LoadingState.Loading(message) } else { LoadingState.NotLoading()}
    val event = Event(state)

    if (dispatcher != Dispatchers.Main) {
      _wipLiveData.value = event
    } else {
      _wipLiveData.postValue(event)
    }
  }

  protected fun stopWorking() {
    _wipLiveData.value = Event(LoadingState.NotLoading())
  }

  sealed class LoadingState(val message: CharSequence? = null) {
    class Loading(message: CharSequence? = null): LoadingState(message)
    class NotLoading: LoadingState(null)
  }
}
package meleros.paw.inventory.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import meleros.paw.inventory.InventoryApp
import meleros.paw.inventory.data.db.InventoryDB
import meleros.paw.inventory.ui.Event

open class BaseViewModel(app: Application): AndroidViewModel(app) {

  private val _wipLiveData: MutableLiveData<Event<Boolean>> = MutableLiveData()

  protected val wipLiveData: LiveData<Event<Boolean>>
    get() = _wipLiveData

  protected val database: InventoryDB by lazy { getApplication<InventoryApp>().database }

  protected fun doWork(loading: Boolean = true, block: suspend () -> Unit) {
    viewModelScope.launch {
      if (loading) {
        _wipLiveData.value = Event(true)
        block()
        _wipLiveData.value = Event(false)
      } else {
        block()
      }
    }
  }
}
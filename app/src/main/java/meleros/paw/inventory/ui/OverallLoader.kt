package meleros.paw.inventory.ui

import meleros.paw.inventory.ui.viewmodel.BaseViewModel

interface OverallLoader {
  fun updateState(state: BaseViewModel.LoadingState)
}
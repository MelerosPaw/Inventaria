package meleros.paw.inventory.ui.fragment

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import meleros.paw.inventory.ui.OverallLoader
import meleros.paw.inventory.ui.viewmodel.BaseViewModel

open class BaseFragment: Fragment() {

  fun navigateBack() {
    findNavController().popBackStack()
  }

  fun navigate(directions: NavDirections) {
    findNavController().navigate(directions)
  }

  fun setLoading(state: BaseViewModel.LoadingState) {
    (activity as? OverallLoader)?.updateState(state)
  }
}
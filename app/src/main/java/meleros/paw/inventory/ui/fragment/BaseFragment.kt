package meleros.paw.inventory.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import meleros.paw.inventory.ui.OverallLoader
import meleros.paw.inventory.ui.viewmodel.BaseViewModel

abstract class BaseFragment: Fragment() {

  abstract fun getBaseViewModel() : BaseViewModel

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    setUpLoader()
    super.onViewCreated(view, savedInstanceState)
  }

  fun navigateBack() {
    findNavController().popBackStack()
  }

  fun navigate(directions: NavDirections) {
    findNavController().navigate(directions)
  }

  fun setLoading(state: BaseViewModel.LoadingState) {
    (activity as? OverallLoader)?.updateState(state)
  }

  private fun setUpLoader() {
    getBaseViewModel().wipLiveData.observe(viewLifecycleOwner) {
      it.get()?.let { state ->
        (activity as? OverallLoader)?.updateState(state)
      }
    }
  }
}

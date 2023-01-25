package meleros.paw.inventory.ui.fragment

import androidx.fragment.app.Fragment
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController

open class BaseFragment: Fragment() {

  fun navigateBack() {
    findNavController().popBackStack()
  }

  fun navigate(directions: NavDirections) {
    findNavController().navigate(directions)
  }
}
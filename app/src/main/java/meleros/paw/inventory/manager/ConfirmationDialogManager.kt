package meleros.paw.inventory.manager

import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import meleros.paw.inventory.ui.fragment.ConfirmRemovalDialogFragment

class ConfirmationDialogManager {

  fun showDialog(fragment: Fragment, itemCount: Int, onConfirmed: () -> Unit) {
    ConfirmRemovalDialogFragment.newInstance(itemCount)
      .show(fragment.parentFragmentManager, ConfirmRemovalDialogFragment.TAG)

    fragment.setFragmentResultListener(ConfirmRemovalDialogFragment.REQUEST_KEY) { key, data ->
      if (key == ConfirmRemovalDialogFragment.REQUEST_KEY
        && data.getBoolean(ConfirmRemovalDialogFragment.RESULT_EXTRA_CONFIRMED)
      ) {
        onConfirmed()
      }
    }
  }
}
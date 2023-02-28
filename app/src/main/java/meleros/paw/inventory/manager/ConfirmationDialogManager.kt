package meleros.paw.inventory.manager

import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import meleros.paw.inventory.ui.fragment.ConfirmExitWithoutSavingDialogFragment
import meleros.paw.inventory.ui.fragment.ConfirmRemovalDialogFragment

class ConfirmationDialogManager {

  fun showConfirmItemRemovalDialog(fragment: Fragment, itemCount: Int, onConfirmed: () -> Unit) {
    fragment.setFragmentResultListener(ConfirmRemovalDialogFragment.REQUEST_KEY) { key, data ->
      if (key == ConfirmRemovalDialogFragment.REQUEST_KEY
        && data.getBoolean(ConfirmRemovalDialogFragment.RESULT_EXTRA_CONFIRMED)) {
        onConfirmed()
      }
    }

    ConfirmRemovalDialogFragment
      .newInstance(itemCount)
      .show(fragment.parentFragmentManager, ConfirmRemovalDialogFragment.TAG)
  }

  fun showExitWithoutSavingDialog(fragment: Fragment, onConfirmed: () -> Unit) {
    fragment.setFragmentResultListener(ConfirmExitWithoutSavingDialogFragment.REQUEST_KEY) { key, data ->
      if (key == ConfirmExitWithoutSavingDialogFragment.REQUEST_KEY
        && data.getBoolean(ConfirmExitWithoutSavingDialogFragment.RESULT_EXTRA_CONFIRMED)) {
        onConfirmed()
      }
    }

    ConfirmExitWithoutSavingDialogFragment
      .newInstance()
      .show(fragment.parentFragmentManager, ConfirmExitWithoutSavingDialogFragment.TAG)
  }
}
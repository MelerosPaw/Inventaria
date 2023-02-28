package meleros.paw.inventory.ui.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import meleros.paw.inventory.R

class ConfirmExitWithoutSavingDialogFragment : DialogFragment() {

  companion object {

    const val TAG = "ConfirmItemCreationIncomplete"
    const val REQUEST_KEY = "REQUEST_KEY"
    const val RESULT_EXTRA_CONFIRMED = "CONFIRMED"

    fun newInstance(): ConfirmExitWithoutSavingDialogFragment = ConfirmExitWithoutSavingDialogFragment()
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val message = resources.getString(R.string.exit_without_saving_confirmation_message)
    return AlertDialog.Builder(requireActivity())
      .setTitle(R.string.exit_without_saving_confirmation_title)
      .setMessage(message)
      .setPositiveButton(R.string.yes) { _, _ -> onConfirmRemoval() }
      .setNegativeButton(R.string.no) { _, _ -> dismiss() }
      .create()
  }

  private fun onConfirmRemoval() {
    setFragmentResult(REQUEST_KEY, bundleOf(RESULT_EXTRA_CONFIRMED to true))
    dismiss()
  }
}
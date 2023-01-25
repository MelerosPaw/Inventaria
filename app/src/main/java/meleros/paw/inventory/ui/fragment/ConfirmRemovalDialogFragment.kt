package meleros.paw.inventory.ui.fragment

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import meleros.paw.inventory.R

class ConfirmRemovalDialogFragment : DialogFragment() {

  companion object {

    const val TAG = "ConfirmRemovalDialogFragment"
    const val REQUEST_KEY = "REQUEST_KEY"
    const val RESULT_EXTRA_CONFIRMED = "CONFIRMED"
    const val EXTRA_ITEM_COUNT = "ITEM_COUNT"

    fun newInstance(itemCount: Int): ConfirmRemovalDialogFragment =
      ConfirmRemovalDialogFragment().apply {
        arguments = bundleOf(EXTRA_ITEM_COUNT to itemCount)
      }
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val itemCount = arguments?.getInt(EXTRA_ITEM_COUNT) ?: 2
    val message = resources.getQuantityString(R.plurals.delete_item_cofirmation_message, itemCount)
    return AlertDialog.Builder(requireActivity())
      .setTitle(R.string.delete_item)
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
package meleros.paw.inventory.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import meleros.paw.inventory.R
import meleros.paw.inventory.bo.Item
import meleros.paw.inventory.databinding.FragmentEditItemBinding
import meleros.paw.inventory.extension.hasText
import meleros.paw.inventory.extension.isGreaterThan
import meleros.paw.inventory.extension.orNot
import meleros.paw.inventory.extension.whenTrue
import meleros.paw.inventory.manager.ConfirmationDialogManager
import meleros.paw.inventory.ui.TitleHolder
import meleros.paw.inventory.ui.viewmodel.BaseViewModel
import meleros.paw.inventory.ui.viewmodel.ItemEditionViewModel
import meleros.paw.inventory.ui.widget.FramedPhotoViewerView

class EditItemFragment : BaseFragment() {

  private var binding: FragmentEditItemBinding? = null
  private val viewModel: ItemEditionViewModel by activityViewModels()
  private val photoManager = FramedPhotoViewerView.Manager(this) { origin, uri -> saveAndShowImage(origin, uri) }
  private val args: EditItemFragmentArgs by navArgs()

  //region Overridden methods
  override fun getBaseViewModel(): BaseViewModel = viewModel

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val binding = FragmentEditItemBinding.inflate(inflater, container, false)
    this.binding = binding
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setUpOnBackPressed()

    binding?.editDescription?.let {
      it.inputType = it.inputType or InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
    }

    val creationDateForEdition = args.itemCreationDateForEdition
    viewModel.setEditionModeParams(creationDateForEdition)
    val isInEditionMode = viewModel.isInEditionMode

    setUpViews(isInEditionMode)
    tryAndSetUpEditionMode(isInEditionMode, creationDateForEdition)
  }

  private fun setUpOnBackPressed() {
    activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        if (isInTheMiddleOfCreating() || isInTheMiddleOfEditing()) {
          ConfirmationDialogManager().showExitWithoutSavingDialog(this@EditItemFragment) { proceed() }
        } else {
          proceed()
        }
      }

      private fun isInTheMiddleOfCreating() = !viewModel.isInEditionMode && hasFilledSomeData()

      private fun isInTheMiddleOfEditing() = hasModifiedSomeData()

      private fun proceed() {
        isEnabled = false
        activity?.onBackPressedDispatcher?.onBackPressed()
      }
    })
  }

  override fun onDestroyView() {
    super.onDestroyView()
    this.binding = null
  }
  //endregion

  //region Private methods
  private fun setUpViews(isInEditionMode: Boolean) {
    binding?.run {
      setUpQuantityPicker()
      setUpCreateButton(isInEditionMode)
      setUpPhotoButtons()
    }
  }

  private fun tryAndSetUpEditionMode(isInEditionMode: Boolean, creationDate: Long) {
    if (isInEditionMode) {
      val itemName = args.itemName.orEmpty()
      val title: String = getString(R.string.edit_item_fragment_label, itemName)
      (activity as? TitleHolder)?.setTitleInToolbar(title)
      viewModel.itemEditionLiveData.observe(viewLifecycleOwner) {
        drawItem(it)
      }
      viewModel.loadItemForEdition(creationDate, itemName)
    }
  }

  private fun drawItem(item: Item) {
    binding?.run {
      pickerQuanitity.amount = item.quantity
      editItemName.setText(item.name)
      editDescription.setText(item.description)
      loadPicture(item)
    }
  }

  private fun FragmentEditItemBinding.setUpQuantityPicker() {
    with(pickerQuanitity) {
      amount = 1
      minAmount = 1
    }
  }

  private fun FragmentEditItemBinding.setUpCreateButton(isInEditionMode: Boolean) {
    btnCreateItem.setOnClickListener { modifyItem(isInEditionMode) }

    viewModel.itemEditionFinishedLiveData.observe(viewLifecycleOwner) {
      it.whenTrue { findNavController().popBackStack() }
    }
  }

  private fun FragmentEditItemBinding.setUpPhotoButtons() {
    imageViewerPhoto.manager = photoManager
  }

  private fun FragmentEditItemBinding.loadPicture(item: Item) {
    item.image
      ?.let { path -> viewModel.getUriFromString(path) }
      ?.let { uri -> showImage(uri) }
  }

  private fun modifyItem(isInEditionMode: Boolean) {
    binding?.run {
      val name: CharSequence = editItemName.text
      val quantity: Int? = pickerQuanitity.amount

      verifyName(name, editItemName)

      if (quantity != null && quantity > 0 && name.isNotBlank()) {
        val description: CharSequence = editDescription.text

        if (isInEditionMode) {
          viewModel.modifyItem(name, description, quantity, viewModel.itemBeingEdited?.image)
        } else {
          viewModel.createItem(name, description, quantity, viewModel.itemPicturePath)
        }
      }
    }
  }

  private fun verifyName(name: CharSequence, editText: EditText) {
    if (name.isBlank()) {
      editText.error = editText.context.getString(R.string.error__item_name_missing)
    }
  }

  private fun saveAndShowImage(origin: FramedPhotoViewerView.Origin, imageUri: Uri) {
    viewModel.saveImageDetailUri(origin, imageUri)
    binding?.showImage(imageUri)
  }

  private fun FragmentEditItemBinding.showImage(imageUri: Uri) {
    imageViewerPhoto.setImageURI(imageUri)
  }

  private fun hasFilledSomeData(): Boolean = binding?.run {
    val hasName = editItemName.hasText()
    val hasDescription = editDescription.hasText()
    val hasQuantity = pickerQuanitity.amount isGreaterThan 1
    val hasPicture = imageViewerPhoto.hasImage()
    hasName || hasDescription || hasQuantity || hasPicture
  }.orNot()

  private fun hasModifiedSomeData(): Boolean = binding?.run {
    val name = editItemName.text
    val description = editDescription.text
    val quantity = pickerQuanitity.amount
    viewModel.hasItemInfoBeenModified(name, description, quantity)
  }.orNot()
  //endregion
}
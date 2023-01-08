package meleros.paw.inventory.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import meleros.paw.inventory.R
import meleros.paw.inventory.data.PicturesTakenFileProvider
import meleros.paw.inventory.databinding.FragmentCreateItemBinding
import meleros.paw.inventory.extension.whenTrue
import meleros.paw.inventory.ui.viewmodel.ItemsViewModel
import meleros.paw.inventory.ui.widget.FramedPhotoViewerView

class CreateItemFragment : BaseFragment() {

  private var binding: FragmentCreateItemBinding? = null
  private val viewModel: ItemsViewModel by activityViewModels()
  private val photoManager = FramedPhotoViewerView.Manager(this, ::saveAndShowImage)

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val binding = FragmentCreateItemBinding.inflate(inflater, container, false)
    this.binding = binding
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    binding?.run {
      setUpQuantityPicker()
      setUpCreateButton()
      setUpPhotoButtons()
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    this.binding = null
  }

  private fun FragmentCreateItemBinding.setUpQuantityPicker() {
    with(pickerQuanitity) {
      amount = 1
      minAmount = 1
    }
  }

  private fun FragmentCreateItemBinding.setUpCreateButton() {
    btnCreateItem.setOnClickListener(::onCreateItemClicked)

    viewModel.itemCreatedLiveData.observe(viewLifecycleOwner) {
      it.whenTrue { findNavController().popBackStack() }
    }
  }

  private fun FragmentCreateItemBinding.setUpPhotoButtons() {
    imageViewerPhoto.manager = photoManager
  }

  private fun onCreateItemClicked(view: View) {
    binding?.run {
      val name: CharSequence = editItemName.text
      val quantity: Int = pickerQuanitity.amount

      verifyName(name, editItemName)

      if (quantity > 0 && name.isNotBlank()) {
        val description: CharSequence = editDescription.text
        val image: CharSequence? = viewModel.itemPicturePath
        viewModel.createItem(name, description, quantity, image)
      }
    }
  }

  private fun verifyName(name: CharSequence, editText: EditText) {
    if (name.isBlank()) {
      editText.error = editText.context.getString(R.string.error__item_name_missing)
    }
  }

  private fun saveAndShowImage(origin: FramedPhotoViewerView.Origin, imageUri: Uri) {
    viewModel.itemPicturePath = when (origin) {
      FramedPhotoViewerView.Origin.CAMERA -> imageUri.path
      FramedPhotoViewerView.Origin.FILE_SYSTEM -> imageUri.toString()
    }
    showImage(imageUri)
  }

  private fun showImage(imageUri: Uri) {
    binding?.imageViewerPhoto?.setImageURI(imageUri)
  }
}
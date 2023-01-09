package meleros.paw.inventory.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import meleros.paw.inventory.databinding.FragmentItemDetailBinding
import meleros.paw.inventory.ui.viewmodel.ItemDetailViewModel
import meleros.paw.inventory.ui.vo.ItemVO

class DetailItemFragment : BaseFragment() {

  private var binding: FragmentItemDetailBinding? = null
  private val args: DetailItemFragmentArgs by navArgs()
  private val viewModel: ItemDetailViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModel.loadItemForDetail(args.itemCreationDate)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val binding = FragmentItemDetailBinding.inflate(inflater, container, false)
    this.binding = binding
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    binding?.run {
      viewModel.itemDetailLiveData.observe(viewLifecycleOwner) { item ->
        (activity as? TitleHolder)?.setTitleInToolbar(item.name)
        labelItemName.text = item.name
        labelQuanitity.text = item.quantity
        labelDescription.text = item.description
        loadPicture(item.image)
        setUpEditButton(item)
      }
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    this.binding = null
  }

  private fun FragmentItemDetailBinding.setUpEditButton(item: ItemVO) {
    btnEditItem.setOnClickListener { navigateToEdit(item.creationDate) }
  }

  private fun navigateToEdit(creationDate: Long) {
    navigate(DetailItemFragmentDirections.actionDetailToEdition(creationDate))
  }

  private fun FragmentItemDetailBinding.loadPicture(imageUri: Uri?) {
    imageViewerPhoto.displayControls = false
    showImage(imageUri)
  }

  private fun FragmentItemDetailBinding.showImage(imageUri: Uri?) {
    imageUri?.let(imageViewerPhoto::setImageURI)
    imageViewerPhoto.isVisible = imageUri != null
  }
}
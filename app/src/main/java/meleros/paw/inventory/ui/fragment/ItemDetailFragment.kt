package meleros.paw.inventory.ui.fragment

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import meleros.paw.inventory.R
import meleros.paw.inventory.databinding.FragmentItemDetailBinding
import meleros.paw.inventory.ui.TitleHolder
import meleros.paw.inventory.ui.viewmodel.DeleteItemViewModel
import meleros.paw.inventory.ui.viewmodel.ItemDetailViewModel
import meleros.paw.inventory.ui.vo.ItemVO
import java.lang.ref.WeakReference
import java.util.Collections

class ItemDetailFragment : BaseFragment() {

  private var binding: FragmentItemDetailBinding? = null
  private val args: ItemDetailFragmentArgs by navArgs()
  private val viewModel: ItemDetailViewModel by viewModels()
  private val deletionViewModel: DeleteItemViewModel by viewModels()
  private var menuProvider: MenuProvider? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModel.loadItemForDetail(args.itemCreationDate)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val binding = FragmentItemDetailBinding.inflate(inflater, container, false)
    this.binding = binding
    setUpMenu()
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
    menuProvider?.let { activity?.removeMenuProvider(it) }
  }

  private fun setUpMenu() {
    val menuProvider = ItemDetailMenuProvider(this)
    activity?.addMenuProvider(menuProvider, viewLifecycleOwner)
    this.menuProvider = menuProvider
  }

  private fun FragmentItemDetailBinding.setUpEditButton(item: ItemVO) {
    btnEditItem.setOnClickListener { navigateToEdit(item.creationDate) }
  }

  private fun navigateToEdit(creationDate: Long) {
    navigate(ItemDetailFragmentDirections.actionDetailToEdition(creationDate))
  }

  private fun FragmentItemDetailBinding.loadPicture(imageUri: Uri?) {
    imageViewerPhoto.displayControls = false
    showImage(imageUri)
  }

  private fun FragmentItemDetailBinding.showImage(imageUri: Uri?) {
    imageUri?.let(imageViewerPhoto::setImageURI)
    imageViewerPhoto.isVisible = imageUri != null
  }

  private fun openConfirmationDialog() {
    ConfirmRemovalDialogFragment.newInstance(1)
      .show(parentFragmentManager, ConfirmRemovalDialogFragment.TAG)

    setFragmentResultListener(ConfirmRemovalDialogFragment.REQUEST_KEY) { key, data ->
      if (key == ConfirmRemovalDialogFragment.REQUEST_KEY
        && data.getBoolean(ConfirmRemovalDialogFragment.RESULT_EXTRA_CONFIRMED)
      ) {
        onConfirmItemRemoval()
      }
    }
  }

  private fun onConfirmItemRemoval() {
    viewModel.itemDisplayedOnDetail?.let { deletionViewModel.deleteItems(Collections.singletonList(it)) }
    navigateBack()
  }

  class ItemDetailMenuProvider(fragment: ItemDetailFragment) : MenuProvider {

    private val fragmentWR = WeakReference(fragment)

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
      menuInflater.inflate(R.menu.menu_item_detail, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
      return when (menuItem.itemId) {
        R.id.action_delete_item -> {
          fragmentWR.get()?.openConfirmationDialog()
          true
        }
        else -> false
      }
    }
  }
}
package meleros.paw.inventory.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.view.isVisible
import meleros.paw.inventory.databinding.RowItemBinding
import meleros.paw.inventory.extension.isTrue
import meleros.paw.inventory.ui.vo.ItemVO

class ListItemAdapter(
  data: List<ItemVO>,
  isSelectionModeEnabled: Boolean,
  onClickListener: (ItemVO) -> Unit,
) : BaseItemAdapter(data, isSelectionModeEnabled, onClickListener) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
    val binding = RowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ListItemViewHolder(binding, isInSelectionMode, onClickListener)
  }

  class ListItemViewHolder(
    private val binding: RowItemBinding,
    isSelectionModeEnabled: Boolean,
    onClickListener: (ItemVO) -> Unit,
  ) : BaseItemViewHolder(binding.root, isSelectionModeEnabled, onClickListener) {

    override fun bind(item: ItemVO) {
      super.bind(item)

      with(binding) {
        labelItemName.text = item.name
        labelDescription.text = item.description
        labelQuantity.text = item.quantity
        labelCreationDate?.text = item.creationDateForDisplay
      }
    }

    override fun getSelectionCheckBox(): CheckBox = binding.checkSelected
  }
}
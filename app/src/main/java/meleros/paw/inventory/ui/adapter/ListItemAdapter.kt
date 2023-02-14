package meleros.paw.inventory.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import meleros.paw.inventory.databinding.RowItemBinding
import meleros.paw.inventory.ui.vo.ItemVO

class ListItemAdapter(
  data: List<ItemVO>,
  isSelectionModeEnabled: Boolean,
  onClickListener: (ItemVO) -> Unit,
  onLongClickListener: (ItemVO, View) -> Unit,
) : BaseItemAdapter(data, isSelectionModeEnabled, onClickListener, onLongClickListener) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItemViewHolder {
    val binding = RowItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return ListItemViewHolder(binding, onClickListener, { isInSelectionMode }, onLongClickListener)
  }

  class ListItemViewHolder(
    private val binding: RowItemBinding,
    onClickListener: (ItemVO) -> Unit,
    isInSelectionMode: () -> Boolean,
    onLongClickListener: (ItemVO, View) -> Unit,
  ) : BaseItemViewHolder(binding.root, onClickListener, isInSelectionMode, onLongClickListener) {

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
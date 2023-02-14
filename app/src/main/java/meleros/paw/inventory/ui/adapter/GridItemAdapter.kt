package meleros.paw.inventory.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.core.view.doOnLayout
import androidx.core.view.updateLayoutParams
import meleros.paw.inventory.databinding.RowGridItemBinding
import meleros.paw.inventory.ui.vo.ItemVO

class GridItemAdapter(
  data: List<ItemVO>,
  isSelectionModeEnabled: Boolean,
  onClickListener: (ItemVO) -> Unit,
  onLongClickListener: (ItemVO, View) -> Unit,
) : BaseItemAdapter(data, isSelectionModeEnabled, onClickListener, onLongClickListener) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseItemViewHolder {
    val binding = RowGridItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    return GridItemViewHolder(binding, onClickListener, { isInSelectionMode }, onLongClickListener)
  }

  class GridItemViewHolder(
    private val binding: RowGridItemBinding,
    onClickListener: (ItemVO) -> Unit,
    isSelectionModeEnabled: () -> Boolean,
    onLongClickListener: (ItemVO, View) -> Unit,
  ) : BaseItemViewHolder(binding.root, onClickListener, isSelectionModeEnabled, onLongClickListener) {

    override fun bind(item: ItemVO) {
      super.bind(item)

      with(binding) {
        labelItemName.text = item.name
        labelQuantity.text = item.quantity
        setUpPhoto(item)
      }
    }

    override fun getSelectionCheckBox(): CheckBox = binding.checkSelected

    /**
     * Waiting until the view holder is measured to apply the same width to the photo's height and
     * get a squared picture.
     */
    private fun RowGridItemBinding.setUpPhoto(item: ItemVO) {
      itemView.doOnLayout {
        imgItemPhoto.updateLayoutParams {
          height = itemView.width
          imgItemPhoto.setImageURI(item.image)
        }
      }
    }
  }
}
package meleros.paw.inventory.ui.adapter

import android.view.View
import android.widget.CheckBox
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import meleros.paw.inventory.ui.vo.ItemVO
import java.util.LinkedList

abstract class BaseItemAdapter(
  data: List<ItemVO>,
  protected var isInSelectionMode: Boolean,
  protected val onClickListener: (ItemVO) -> Unit,
) : RecyclerView.Adapter<BaseItemAdapter.BaseItemViewHolder>() {

  companion object {
    private const val ON_SELECTION_MODE_CHANGED = "SELECTION_MODE_ENABLED"
  }

  val data: MutableList<ItemVO> = LinkedList(data)

  override fun onBindViewHolder(holder: BaseItemViewHolder, position: Int) {
    holder.bind(data[position])
  }

  override fun getItemCount(): Int = data.size

  override fun onBindViewHolder(holder: BaseItemViewHolder, position: Int, payloads: MutableList<Any>) {
    super.onBindViewHolder(holder, position, payloads)

    if (payloads.contains(ON_SELECTION_MODE_CHANGED)) {
      holder.setSelectable(isInSelectionMode)
    }
  }

  fun replaceItems(items: List<ItemVO>) {
    DiffUtil.calculateDiff(ItemDiffUtilCallback(data, items)).dispatchUpdatesTo(this)
    data.clear()
    data.addAll(items)
  }

  fun setSelectionModeEnabled(enabled: Boolean) {
    isInSelectionMode = enabled
    onSelectedStateChanged()
  }

  fun onSelectedStateChanged() {
    notifyItemRangeChanged(0, itemCount, ON_SELECTION_MODE_CHANGED)
  }

  abstract class BaseItemViewHolder(
    itemView: View,
    private var isSelectionModeEnabled: Boolean,
    protected var onClickListener: (ItemVO) -> Unit,
  ): RecyclerView.ViewHolder(itemView) {

    abstract fun getSelectionCheckBox(): CheckBox

    open fun bind(item: ItemVO) {
      itemView.setOnClickListener {
        if (isSelectionModeEnabled) {
          getSelectionCheckBox().isChecked = !item.isSelected
        } else {
          onClickListener(item)
        }
      }

      setSelectable(isSelectionModeEnabled)
      setSelected(item.isSelected, item)
    }

    fun setSelectable(selectable: Boolean) {
      isSelectionModeEnabled = selectable
      getSelectionCheckBox().isVisible = selectable
    }

    private fun setSelected(isSelected: Boolean, item: ItemVO) {
      getSelectionCheckBox().run {
        setOnCheckedChangeListener(null)
        isChecked = isSelected
        setOnCheckedChangeListener { _, isChecked -> item.isSelected = isChecked } // Tal vez esto esté mejor en el view model, con un listener?
      }
    }
  }

  class ItemDiffUtilCallback(private val oldList: List<ItemVO>, private val newList: List<ItemVO>) : DiffUtil.Callback() {

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
      oldList.getOrNull(oldItemPosition)?.creationDate == newList.getOrNull(newItemPosition)?.creationDate

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
      oldList.getOrNull(oldItemPosition)?.isSelected == newList.getOrNull(newItemPosition)?.isSelected
  }

}
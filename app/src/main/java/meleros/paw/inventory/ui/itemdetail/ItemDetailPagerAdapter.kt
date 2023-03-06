package meleros.paw.inventory.ui.itemdetail

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ItemDetailPagerAdapter(
  fragment: Fragment,
  private val creationDate: Long,
  private val itemName: String,
)
  : FragmentStateAdapter(fragment) {

  override fun getItemCount(): Int = 3

  override fun createFragment(position: Int): Fragment = ItemDetailFragment.newInstance(creationDate, itemName)
}
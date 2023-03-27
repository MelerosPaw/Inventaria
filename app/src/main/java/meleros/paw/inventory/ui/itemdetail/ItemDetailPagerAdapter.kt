package meleros.paw.inventory.ui.itemdetail

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import meleros.paw.inventory.data.mapper.itemCreationDateFormatter
import meleros.paw.inventory.ui.vo.DetailViewPagerInfo
import meleros.paw.inventory.ui.vo.MinimalItemInfo

class ItemDetailPagerAdapter(
  fragment: Fragment,
  private val detailViewPagerInfo: DetailViewPagerInfo,
) : FragmentStateAdapter(fragment) {

  override fun getItemCount(): Int = detailViewPagerInfo.minimalInfo.size

  override fun createFragment(position: Int): Fragment {
    val minimalInfo = detailViewPagerInfo.minimalInfo[position]
    return ItemDetailFragment.newInstance(minimalInfo)
  }
}
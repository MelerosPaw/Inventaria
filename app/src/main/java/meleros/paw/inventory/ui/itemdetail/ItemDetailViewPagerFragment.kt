package meleros.paw.inventory.ui.itemdetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import meleros.paw.inventory.databinding.FragmentDetailViewPagerBinding
import meleros.paw.inventory.ui.fragment.BaseFragment
import meleros.paw.inventory.ui.viewmodel.BaseViewModel
import meleros.paw.inventory.ui.vo.DetailViewPagerInfo

class ItemDetailViewPagerFragment : BaseFragment() {

  private val viewModel: ItemDetailViewPagerViewModel by viewModels()
  private var binding: FragmentDetailViewPagerBinding? = null
  private val args: ItemDetailViewPagerFragmentArgs by navArgs()

  override fun getBaseViewModel(): BaseViewModel = viewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    viewModel.loadStartingPosition(args.itemCreationDate)
  }

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
    binding = FragmentDetailViewPagerBinding.inflate(inflater, container, false)
    return binding?.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    viewModel.startingPositionLiveData.observe(viewLifecycleOwner) { event ->
      event.get()?.let(::setUpDetailViewPager)
    }
  }

  private fun setUpDetailViewPager(viewPagerInfo: DetailViewPagerInfo) {
    binding?.pagerDetail?.run {
      adapter = ItemDetailPagerAdapter(this@ItemDetailViewPagerFragment, viewPagerInfo)
      setCurrentItem(viewPagerInfo.startPosition, false)
    }
    viewModel.setLoading(false)
  }
}
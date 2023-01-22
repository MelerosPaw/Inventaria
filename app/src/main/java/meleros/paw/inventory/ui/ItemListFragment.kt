package meleros.paw.inventory.ui

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import meleros.paw.inventory.R
import meleros.paw.inventory.data.ItemListLayout
import meleros.paw.inventory.databinding.FragmentItemListBinding
import meleros.paw.inventory.databinding.SelectionFabMenuBinding
import meleros.paw.inventory.ui.adapter.BaseItemAdapter
import meleros.paw.inventory.ui.adapter.GridItemAdapter
import meleros.paw.inventory.ui.adapter.ListItemAdapter
import meleros.paw.inventory.ui.viewmodel.ItemListViewModel
import meleros.paw.inventory.ui.vo.ItemVO
import java.lang.ref.WeakReference

class ItemListFragment : BaseFragment() {

  private var binding: FragmentItemListBinding? = null
  private var selectionBinding: SelectionFabMenuBinding? = null
  private val viewModel: ItemListViewModel by activityViewModels()
  private var menuProvider: ItemListMenuProvider? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val binding = FragmentItemListBinding.inflate(inflater, container, false)
    val selectionBinding = SelectionFabMenuBinding.bind(binding.root)
    this.binding = binding
    this.selectionBinding = selectionBinding
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpOptionMenu()
    setUpAddButton()
    setUpSelectionButtons()
    setUpItemList()
  }

  override fun onDestroyView() {
    super.onDestroyView()
    binding = null
    menuProvider?.let { activity?.removeMenuProvider(it) }
  }

  private fun setUpAddButton() {
    binding?.btnCreateItem?.setOnClickListener { onFabClicked() }
  }

  private fun setUpSelectionButtons() {
    selectionBinding?.run {
      setUpBackground()
      setUpDeselectAllButton()
      setUpSelectAllButton()
      setUpOnDeleteItemsButton()
    }
  }

  private fun SelectionFabMenuBinding.setUpBackground() {
    viewCloseSelectionMenu.setOnClickListener {
      closeSelectionMenu(viewModel.isInSelectionMode)
    }
  }

  private fun SelectionFabMenuBinding.setUpDeselectAllButton() {
    btnDeselectAll.setOnClickListener {
      (binding?.listItems?.adapter as? BaseItemAdapter)?.let {
        viewModel.deselectAllItems()
        it.onSelectedStateChanged()
      }
    }
  }

  private fun SelectionFabMenuBinding.setUpSelectAllButton() {
    btnSelectAll.setOnClickListener {
      (binding?.listItems?.adapter as? BaseItemAdapter)?.let {
        viewModel.selectAllItems()
        it.onSelectedStateChanged()
      }
    }
  }

  private fun SelectionFabMenuBinding.setUpOnDeleteItemsButton() {
    btnDeleteItems.setOnClickListener {
      (binding?.listItems?.adapter as? BaseItemAdapter)?.let {
        viewModel.deleteSelectedItems()
        // Actualiza la lista publicando un ItemListUpdate, que, como ya existe, hará replace.
      }
    }
  }

  private fun setUpItemList() {
    viewModel.itemListLiveData.observe(viewLifecycleOwner) { items ->
      items?.let {
        drawItems(it)

        // Cuando se llama tras eliminar items, queremos inhabilitar la selección.
        if (!it.selectionModeEnabled) {
          setSelectionModeEnabled(false)
        }
      }
    }
    // TODO Melero 31/12/22: Habría simplemente que actualizar la lista con el item nuevo, no volverla a pintar entera
//    viewModel.itemEditionFinishedLiveData.observe(viewLifecycleOwner) { it.whenTrue(false) { viewModel.loadItems() } }

    // Esta comprobación es necesaria porque creo que, como estamos creando el view model con la activity, no se
    // destruye y se queda la lista que está guardada, actuando como caché. Si el view model se crease con el fragment,
    // estaríamos obligados a tener que implementar una caché en repositorio.
    if (viewModel.currentVOs == null) {
      viewModel.loadItems()
    }
  }

  private fun setUpOptionMenu() {
    val itemMenuProvider = ItemListMenuProvider(viewModel, this)
    activity?.addMenuProvider(itemMenuProvider)
    this.menuProvider = itemMenuProvider
  }

  private fun drawItems(itemListUpdate: ItemListViewModel.ItemListUpdate) {
    binding?.listItems?.run {
      val items = itemListUpdate.newItems ?: itemListUpdate.currentItems
      val layout = itemListUpdate.layout

      if (adapter == null) {
        // When the adapter hasn't been set, newItems won't be null, but when the view is being recreated
        // (i.e., when navigating up), items won't be new, so we'll be using the current ones.
        items?.let { setUpAdapter(layout, it) }
      } else {
        modifyAdapter(layout, items)
      }
    }
  }

  /** Cuando aún no se ha puesto adapter, se crea el adapter con la disposición solicitada. */
  private fun RecyclerView.setUpAdapter(layout: ItemListLayout, newItems: List<ItemVO>) {
    updateAdapter(layout, newItems)
    updateLayoutManager(layout)
  }

  private fun RecyclerView.updateAdapter(layout: ItemListLayout, newItems: List<ItemVO>) {
    val isInSelectionMode = viewModel.isInSelectionMode
    adapter = when (layout) {
      ItemListLayout.LIST -> ListItemAdapter(newItems, isInSelectionMode, ::navigateToDetail)
      ItemListLayout.GRID -> GridItemAdapter(newItems, isInSelectionMode, ::navigateToDetail)
    }
  }

  private fun RecyclerView.updateLayoutManager(layout: ItemListLayout) {
    layoutManager = when (layout) {
      ItemListLayout.LIST -> LinearLayoutManager(context)
      ItemListLayout.GRID -> GridLayoutManager(context, 2)
    }
  }

  /**
   * Si ya hay adapter, se cambian la disposición y los ítems:
   * * Si la disposición es distinta (`if`), se pone la nueva disposición con los nuevos items o los que ya hubiera si
   * no hay items nuevos.
   * * Si la disposición es la misma (`else if`), se cambian los items solo si hay items.
   *
   * @param items Si son nulos, significa que solo ha cambiado la disposición, por lo que hay que habría que poner los
   * items que ya estuvieran en la lista. Si no, es que son nuevos.
   */
  private fun RecyclerView.modifyAdapter(layout: ItemListLayout, items: List<ItemVO>?) {
    val adapter = this.adapter

    if (items != null && adapter is BaseItemAdapter) {
      val currentLayoutIsDifferent = layout == ItemListLayout.LIST && adapter is GridItemAdapter
          || layout == ItemListLayout.GRID && adapter is ListItemAdapter

      if (currentLayoutIsDifferent) {
        setUpAdapter(layout, items)
      } else {
        adapter.replaceItems(items)
      }
    }
  }

  private fun setSelectionModeEnabled(enabled: Boolean) {
    (binding?.listItems?.adapter as? BaseItemAdapter)?.let { adapter ->
      viewModel.setSelectionModeEnabled(enabled)
      menuProvider?.resetSelectionOptions(!enabled && viewModel.isMenuOpen)
      adapter.setSelectionModeEnabled(enabled)
      setUpToolbarForSelectionMode(enabled)
      setUpFabForSelectionMode(enabled)
      closeSelectionMenu(enabled)
    }
  }

  private fun closeSelectionMenu(isInSelectionMode: Boolean) {
    if (!isInSelectionMode && viewModel.isMenuOpen) {
      toggleSelectionModeMenuVisibility()
    }
  }

  private fun setUpToolbarForSelectionMode(isSelectionModeEnabled: Boolean) {
    val toolbarTitle = getToolbarTitleAccordingToSelectionModeEnabled(isSelectionModeEnabled)
    val toolbarColor = getToolbarColorAccordingToSelectionModeEnabled(isSelectionModeEnabled)
    (activity as? SelectionModeListener)?.onSelectionModeChanged(toolbarTitle, toolbarColor)
  }

  private fun setUpFabForSelectionMode(isSelectionModeEnabled: Boolean) {
    binding?.btnCreateItem?.run {
      val color = getFabColorAccordingToSelectionMode(isSelectionModeEnabled)
      backgroundTintList = ColorStateList.valueOf(color)
      val icon = getFabIconAccordingToSelectionMode(isSelectionModeEnabled)
      setImageResource(icon)
    }
  }

  @DrawableRes
  private fun getFabIconAccordingToSelectionMode(isSelectionModeEnabled: Boolean): Int =
    R.drawable.ic_close.takeIf { isSelectionModeEnabled } ?: R.drawable.ic_add

  @StringRes
  private fun getToolbarTitleAccordingToSelectionModeEnabled(isSelectionModeEnabled: Boolean): Int =
    R.string.item_list_fragment_label_on_selection_mode.takeIf { isSelectionModeEnabled }
      ?: R.string.item_list_fragment_label

  @ColorInt
  private fun getToolbarColorAccordingToSelectionModeEnabled(isSelectionModeEnabled: Boolean): Int {
    val color = R.color.secondaryVariantColor.takeIf { isSelectionModeEnabled } ?: R.color.primaryColor
    return ResourcesCompat.getColor(resources, color, null)
  }

  @ColorInt
  private fun getFabColorAccordingToSelectionMode(isSelectionModeEnabled: Boolean): Int {
    val color = R.color.secondaryVariantColor.takeIf { isSelectionModeEnabled } ?: R.color.primaryColor
    return ResourcesCompat.getColor(resources, color, null)
  }

  private fun onFabClicked() {
    if (viewModel.isInSelectionMode) {
      toggleSelectionModeMenuVisibility()
    } else {
      navigateToCreate()
    }
  }

  private fun toggleSelectionModeMenuVisibility() {
    selectionBinding?.run {
      if (viewModel.isMenuOpen) {
        btnDeleteItems.hide()
        btnDeselectAll.hide()
        btnSelectAll.hide()
      } else {
        btnDeleteItems.show()
        btnDeselectAll.show()
        btnSelectAll.show()
      }

      viewModel.isMenuOpen = !viewModel.isMenuOpen
    }
  }

  private fun navigateToDetail(item: ItemVO) {
    navigate(ItemListFragmentDirections.actionItemListToDetail(item.creationDate))
  }

  private fun navigateToCreate() {
    navigate(ItemListFragmentDirections.actionItemListToCreate())
  }

  class ItemListMenuProvider(private val viewModel: ItemListViewModel? = null, fragment: ItemListFragment) : MenuProvider {

    private val fragmentWR = WeakReference(fragment)
    private var menu: Menu? = null
    private var menuInflater: MenuInflater? = null

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
      this.menu = menu
      this.menuInflater = menuInflater
      menuInflater.inflate(R.menu.menu_item_list, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
      return when (menuItem.itemId) {
        R.id.action_sort_by_quantity -> { viewModel?.sortItemsByQuantity(); true }
        R.id.action_sort_alphabetically -> { viewModel?.sortItemsAlphabetically(); true }
        R.id.action_sort_by_creation_date -> { viewModel?.sortItemsByCreationDate(); true }
        R.id.action_lay_out_as_list -> { viewModel?.layOutItemsAsList(); true }
        R.id.action_lay_out_as_grid -> { viewModel?.layOutItemsAsGrid(); true }
        R.id.action_enable_selection_mode -> { setSelectionModeEnabled(true); true }
        R.id.action_disable_selection_mode -> { setSelectionModeEnabled(false); true }
        else -> false
      }
    }

    fun resetSelectionOptions(mustReset: Boolean) {
      if (mustReset) {
        setSelectionOptions(false)
      }
    }

    private fun setSelectionModeEnabled(enabled: Boolean) {
      fragmentWR.get()?.setSelectionModeEnabled(enabled)
      setSelectionOptions(enabled)
    }

    private fun setSelectionOptions(enabled: Boolean) {
      menu?.let { setSelectionModeEnabled(it, enabled) }
    }

    private fun setSelectionModeEnabled(menu: Menu, enabled: Boolean) {
      val enableSelectionOption = menu.findItem(R.id.action_enable_selection_mode)
      val disableSelectionOption = menu.findItem(R.id.action_disable_selection_mode)

      enableSelectionOption?.isVisible = !enabled
      disableSelectionOption?.isVisible = enabled
    }
  }
}
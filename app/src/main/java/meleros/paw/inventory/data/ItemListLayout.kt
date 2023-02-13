package meleros.paw.inventory.data

enum class ItemListLayout(val itemsPerRow: Int) {
  LIST(1),
  GRID_2(2),
  GRID_4(4),
}
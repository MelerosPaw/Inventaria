package meleros.paw.inventory.bo

class Item(
  val name: String,
  val description: String,
  val quantity: Int = 1,
  val creationDate: Long,
  val image: String? = null,
) {

  override fun equals(other: Any?): Boolean = name == (other as? Item)?.name
}

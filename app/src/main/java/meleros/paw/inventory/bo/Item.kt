package meleros.paw.inventory.bo

class Item(
  val name: String,
  val description: String,
  val quantity: Int = 1,
  val creationDate: Long,
  val image: String? = null,
) {

  companion object {
    private const val INVALID_CREATION_DATE = -1L

    fun isValidCreationDate(creationDate: Long) = creationDate != INVALID_CREATION_DATE
  }

  override fun equals(other: Any?): Boolean = name == (other as? Item)?.name
}

package meleros.paw.inventory.ui.vo

import android.net.Uri

class ItemVO(
  val name: String,
  val description: String?,
  val quantity: String,
  val creationDate: Long,
  val creationDateForDisplay: String,
  val image: Uri?,
  var isSelected: Boolean = false,
) {

  override fun toString(): String {
    return "$name (selected: $isSelected) - image: ${image?.path}"
  }
}
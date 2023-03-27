package meleros.paw.inventory.ui.vo

import java.text.SimpleDateFormat

class MinimalItemInfo(
  val name: String,
  val creationDate: Long,
  private val creationDateFormatter: SimpleDateFormat,
) {

  override fun toString(): String {
    return "$name (${creationDateFormatter.format(creationDate)})"
  }
}
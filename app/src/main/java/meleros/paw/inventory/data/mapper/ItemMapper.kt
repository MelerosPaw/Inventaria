package meleros.paw.inventory.data.mapper

import android.net.Uri
import meleros.paw.inventory.bo.Item
import meleros.paw.inventory.data.db.ItemDBO
import meleros.paw.inventory.ui.vo.ItemVO
import java.text.SimpleDateFormat
import java.util.Locale

val itemCreationDateFormatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

fun Item.toDBO() = ItemDBO(0, name, description, quantity, creationDate, image)

fun ItemDBO.toBo() = Item(name, description, quantity, creationDate, image)

fun Item.toVo(imageUri: Uri?) = ItemVO(
  name,
  description,
  quantity.toString(),
  creationDate,
  itemCreationDateFormatter.format(creationDate),
  imageUri,
)

fun ItemVO.toBo(): Item = Item(name, description.orEmpty(), quantity.toInt(), creationDate, image?.path)
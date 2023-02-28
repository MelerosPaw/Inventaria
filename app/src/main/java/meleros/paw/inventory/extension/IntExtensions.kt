package meleros.paw.inventory.extension

infix fun Int?.isGreaterThan(otherValue: Int) = this?.let { it > otherValue }.orNot()
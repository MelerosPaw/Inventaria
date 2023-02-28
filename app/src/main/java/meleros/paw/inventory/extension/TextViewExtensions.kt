package meleros.paw.inventory.extension

import android.widget.TextView

fun TextView.hasText() = text.isNotBlank()
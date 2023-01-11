package meleros.paw.inventory.ui

import androidx.annotation.ColorInt
import androidx.annotation.StringRes

interface SelectionModeListener {
  fun onSelectionModeChanged(@StringRes toolbarTitle: Int, @ColorInt toolbarColor: Int)
}
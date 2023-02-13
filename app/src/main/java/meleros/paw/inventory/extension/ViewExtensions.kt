package meleros.paw.inventory.extension

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible

fun View.show() {
  isVisible = true
}

fun View.hide() {
  isVisible = false
}

fun View.hideKeyboard() {
  val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
  inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}
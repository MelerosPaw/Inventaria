package meleros.paw.inventory.extension

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet

fun AttributeSet.consume(context: Context, attrs: IntArray, useTypedArray: (TypedArray) -> Unit) {
  with(context.obtainStyledAttributes(this, attrs)) {
    useTypedArray(this)
    recycle()
  }
}
package meleros.paw.inventory.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import meleros.paw.inventory.R
import meleros.paw.inventory.databinding.LoaderViewBinding

class LoaderView @JvmOverloads constructor(
  context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

  private val binding: LoaderViewBinding

  var text: CharSequence?
    get() = binding.loaderLabelMessage.text
    set(value) {
      binding.loaderLabelMessage.apply {
        text = value
        isVisible = !value.isNullOrBlank()
      }
    }

  init {
    binding = LoaderViewBinding.inflate(LayoutInflater.from(context), this)
    binding.loaderProgress.indeterminateDrawable.setColorFilter(
      ContextCompat.getColor(context, R.color.primaryColor),
      android.graphics.PorterDuff.Mode.MULTIPLY
    )
  }

  fun setText(@StringRes textId: Int) {
    text = context.getText(textId)
  }
}
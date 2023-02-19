package meleros.paw.inventory.ui.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import meleros.paw.inventory.R
import meleros.paw.inventory.databinding.TextImageButtonViewBinding
import meleros.paw.inventory.extension.consume

class TextImageButtonView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
  ConstraintLayout(context, attrs) {

  private val binding: TextImageButtonViewBinding

  init {
    layOut()
    binding = TextImageButtonViewBinding.inflate(LayoutInflater.from(context), this)

    attrs?.consume(context, R.styleable.TextImageButtonView) {
      setImageDrawable(it.getDrawable(R.styleable.TextImageButtonView_image))
      setText(it.getString(R.styleable.TextImageButtonView_text))
      setUpTextAlignment(it.getInt(R.styleable.TextImageButtonView_textAlignment, 0))
      setMinified(it.getBoolean(R.styleable.TextImageButtonView_minified, false))
    }
  }

  private fun layOut() {
    val padding = resources.getDimensionPixelSize(R.dimen.text_image_button_view_padding)
    val minClickabeArea = resources.getDimensionPixelSize(R.dimen.min_clickable_area)
    setPadding(padding, padding, padding, padding)
    minHeight = minClickabeArea
    minWidth = minClickabeArea
  }

  fun setText(text: CharSequence?) {
    binding.tibvLabelText.text = text
  }

  private fun setUpTextAlignment(textAlignment: Int) {
    binding.tibvLabelText.textAlignment = when (textAlignment) {
      1 -> TEXT_ALIGNMENT_CENTER
      2 -> TEXT_ALIGNMENT_TEXT_END
      else -> TEXT_ALIGNMENT_TEXT_START
    }
  }

  fun setImageDrawable(drawable: Drawable?) {
    binding.tibvImgIcon.setImageDrawable(drawable)
  }

  fun setMinified(minified: Boolean) {
    binding.tibvLabelText.isVisible = !minified
  }
}
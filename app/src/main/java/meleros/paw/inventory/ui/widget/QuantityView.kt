package meleros.paw.inventory.ui.widget

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import meleros.paw.inventory.R
import meleros.paw.inventory.databinding.QuantityViewBinding

class QuantityView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
) : LinearLayout(context, attrs) {

  private val binding: QuantityViewBinding
  var amount: Int
    get() = binding.inputAmount.text.toString().toIntOrNull() ?: 0
    set(value) {
      if (value in minAmount.. maxAmount) {
        binding.inputAmount.setText(value.toString())
        onAmountChanged()
      }
    }

  var minAmount: Int = Int.MIN_VALUE
    set(value) {
      field = value
      onAmountChanged()
    }
  var maxAmount: Int = Int.MAX_VALUE
    set(value) {
      field = value
      onAmountChanged()
    }

  init {
    binding = QuantityViewBinding.inflate(LayoutInflater.from(context), this)
    setUpButtons()
    setUpTextField()
  }

  private fun setUpButtons() {
    binding.btnDecrease.setOnClickListener {
      if (amount > minAmount) {
        amount--
      }
    }
    binding.btnIncrease.setOnClickListener {
      if (amount < maxAmount) {
        amount++
      }
    }
  }

  private fun setUpTextField() {
    binding.inputAmount.doAfterTextChanged {
      val error: String? = "La cantidad debe ser al menos $minAmount".takeIf { amount < minAmount }
      binding.inputAmount.error = error
      onAmountChanged()
    }
  }

  private fun onAmountChanged() {
    with(binding) {
      val decreaseButtonEnabled = amount > minAmount
      val increaseButtonEnabled = amount < maxAmount

      btnDecrease.isEnabled = decreaseButtonEnabled
      btnIncrease.isEnabled = increaseButtonEnabled
      btnDecrease.backgroundTintList = getButtonBackgroundColor(decreaseButtonEnabled)
      btnIncrease.backgroundTintList = getButtonBackgroundColor(increaseButtonEnabled)
      btnDecrease.imageTintList = getButtonIconColor(decreaseButtonEnabled)
      btnIncrease.imageTintList = getButtonIconColor(increaseButtonEnabled)
    }
  }

  private fun getButtonBackgroundColor(isEnabled: Boolean): ColorStateList {
    val enabledColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.primaryColor))
    val disabledColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.gray))
    return enabledColor.takeIf { isEnabled } ?: disabledColor
  }

  private fun getButtonIconColor(isEnabled: Boolean): ColorStateList {
    val enabledColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white))
    val disabledColor = ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_gray))
    return enabledColor.takeIf { isEnabled } ?: disabledColor
  }
}